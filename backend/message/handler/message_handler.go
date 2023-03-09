package handler

import (
	"context"
	"encoding/json"
	"fmt"
	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/websocket/v2"
	"github.com/pkg/errors"
	"github.com/sirupsen/logrus"
	authMiddleware "sosmed-go-backend/auth/handler"
	chatService "sosmed-go-backend/chat/service"
	"sosmed-go-backend/common"
	"sosmed-go-backend/firebase/service"
	messageService "sosmed-go-backend/message/service"
	"sosmed-go-backend/models"
	userService "sosmed-go-backend/user/service"
	"strconv"
	"time"
)

type messageHandler struct {
	messageService   messageService.MessageService
	chatService      chatService.ChatService
	userService      userService.UserService
	messagingService service.MessagingService
}

func NewMessageHandler(
	router fiber.Router,
	authMiddleware authMiddleware.AuthMiddleware,
	messageService messageService.MessageService,
	chatService chatService.ChatService,
	userService userService.UserService,
	messagingService service.MessagingService,
) {
	h := messageHandler{
		messageService:   messageService,
		chatService:      chatService,
		userService:      userService,
		messagingService: messagingService,
	}
	router.Post("/chat/:chat_id/message", authMiddleware.Authenticate, h.create)
	router.Get("/chat/:chat_id/messages", authMiddleware.Authenticate, h.getMessages)
	router.Get("/chat/:chat_id/socket", authMiddleware.Authenticate, h.upgradeSocket, websocket.New(h.socket))
	router.Get("/chat/:chat_id/online-members", authMiddleware.Authenticate, h.onlineMembers)
	router.Get("/chat-monitor", authMiddleware.Authenticate, h.monitor)

	go observeEvent()
}

func (m *messageHandler) create(ctx *fiber.Ctx) error {
	var req models.CreateMessageReq
	err := ctx.BodyParser(&req)
	if err != nil {
		return errors.WithMessage(models.ErrBadRequest, err.Error())
	}

	res, err := m.messageService.Create(ctx.Context(), &req)
	if err != nil {
		return err
	}

	go func() {
		err := m.messagingService.SendChatNotification(context.Background(), res.ChatId)
		if err != nil {
			logrus.Error(err)
			return
		}
	}()

	resByte, err := json.Marshal(res)
	if err != nil {
		return err
	}

	broadcaster <- &Payload{
		chatId: req.ChatId,
		body:   resByte,
	}

	return ctx.JSON(res)
}

func (m *messageHandler) getMessages(ctx *fiber.Ctx) error {
	nextInt, _ := strconv.ParseInt(ctx.Query("next_id"), 10, 64)
	if nextInt == 0 {
		nextInt = time.Now().UnixMilli()
	}

	previousId, _ := strconv.ParseInt(ctx.Query("previous_id"), 10, 64)
	limit, _ := strconv.ParseInt(ctx.Query("limit"), 10, 64)
	if limit == 0 || limit > 10 {
		limit = 10
	}

	page := models.Page{
		PreviousId: previousId,
		NextId:     nextInt,
		Size:       uint64(limit),
	}

	chatId, err := strconv.ParseInt(ctx.Params("chat_id"), 10, 64)
	if err != nil {
		return err
	}

	res, err := m.messageService.GetMessages(ctx.Context(), chatId, page)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}

func (m *messageHandler) socket(conn *websocket.Conn) {
	chatId, err := strconv.ParseInt(conn.Params("chat_id"), 10, 64)
	if err != nil {
		err := conn.Close()
		if err != nil {
			logrus.Error()
		}
		return
	}

	profile, ok := conn.Locals("profile").(models.Profile)
	if !ok {
		err := conn.Close()
		if err != nil {
			logrus.Error()
		}
		return
	}
	client := Client{
		conn:   conn,
		chatId: chatId,
		userId: profile.Id,
	}

	defer func() {
		unregister <- &client

		err := conn.Close()
		if err != nil {
			logrus.Error()
		}
	}()

	register <- &client

	_ = conn.SetReadDeadline(time.Now().Add(time.Second * 10))
	conn.SetPongHandler(func(string) error {
		logrus.Debug("Pong received")
		_ = conn.SetReadDeadline(time.Now().Add(time.Second * 10))
		return nil
	})

	for {
		mt, body, err := conn.ReadMessage()
		if err != nil {
			if websocket.IsUnexpectedCloseError(err, websocket.CloseGoingAway, websocket.CloseAbnormalClosure) {
				logrus.Error("read error:", err)
			}
			return
		}

		if mt == websocket.TextMessage {
			var req models.CreateMessageReq
			err = json.Unmarshal(body, &req)
			if err != nil {
				continue
			}
			req.ChatId = chatId

			// store message in database
			res, err := m.messageService.Create(context.WithValue(context.Background(), "profile", profile), &req)
			if err != nil {
				logrus.Error(err)
				continue
			}

			resBody, err := json.Marshal(res)
			if err != nil {
				logrus.Error(err)
				continue
			}

			// broadcast to online client
			broadcaster <- &Payload{
				chatId: chatId,
				body:   resBody,
			}

		} else {
			logrus.Infof("WebSocket received message with content type: %d", websocket.TextMessage)
		}
	}
}

func (m *messageHandler) upgradeSocket(ctx *fiber.Ctx) error {
	chatId, err := strconv.ParseInt(ctx.Params("chat_id"), 10, 64)
	if err != nil {
		return errors.WithMessage(models.ErrBadRequest, "Invalid chat_id format")
	}

	profile, err := common.GetCurrentProfileFromContext(ctx.Context())
	if err != nil {
		return err
	}

	isMember, err := m.chatService.IsMember(ctx.Context(), chatId, profile.Id)
	if err != nil {
		return err
	}

	if !isMember {
		return errors.WithMessagef(models.ErrNotFound, "Chat not found")
	}

	if websocket.IsWebSocketUpgrade(ctx) {
		ctx.Locals("allowed", true)
		return ctx.Next()
	}
	return fiber.ErrUpgradeRequired
}

type Client struct {
	conn   *websocket.Conn
	chatId int64
	userId int64
}

type Payload struct {
	chatId int64
	body   []byte
}

var rooms = make(map[int64]map[*Client]struct{})
var register = make(chan *Client)
var unregister = make(chan *Client)
var broadcaster = make(chan *Payload)

func observeEvent() {
	for {
		select {
		case client := <-register:
			addClientToChatRoom(client)
			logrus.Debug("Client registered")
		case payload := <-broadcaster:
			broadcastToClient(payload)
			logrus.Debug("payload received: ", string(payload.body))
		case client := <-unregister:
			deleteClientFromChatRoom(client)
			logrus.Info("Client unregistered")
		}
	}
}

func addClientToChatRoom(client *Client) {
	members := rooms[client.chatId]
	if members == nil {
		members = make(map[*Client]struct{})
	}

	members[client] = struct{}{}
	rooms[client.chatId] = members
	logrus.Infof("%+v", rooms)
}

func deleteClientFromChatRoom(client *Client) {
	members := rooms[client.chatId]
	if members != nil {
		delete(members, client)
	}
}

func broadcastToClient(payload *Payload) {
	for client := range rooms[payload.chatId] {
		if err := client.conn.WriteMessage(websocket.TextMessage, payload.body); err != nil {
			logrus.Error(err)

			if err := client.conn.WriteMessage(websocket.CloseMessage, []byte{}); err != nil {
				logrus.Error(err)
				return
			}

			err := client.conn.Close()
			if err != nil {
				logrus.Error(err)
				return
			}

			deleteClientFromChatRoom(client)
		}
	}
}

func (m *messageHandler) monitor(ctx *fiber.Ctx) error {
	res := make(map[string][]models.UserResponse)

	for chatId, members := range rooms {
		users := make([]models.UserResponse, 0)
		for client := range members {
			user, err := m.userService.GetUserById(ctx.Context(), client.userId)
			if err != nil {
				return err
			}

			users = append(users, user)
		}

		res[fmt.Sprintf("chat_%d", chatId)] = users
	}

	return ctx.JSON(res)
}

func (m *messageHandler) onlineMembers(ctx *fiber.Ctx) error {
	chatId, err := strconv.ParseInt(ctx.Params("chat_id"), 10, 64)
	if err != nil {
		return err
	}

	userIds := make([]int64, 0)
	for client := range rooms[chatId] {
		userIds = append(userIds, client.userId)
	}

	return ctx.JSON(userIds)
}
