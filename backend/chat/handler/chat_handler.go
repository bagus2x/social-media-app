package handler

import (
	"github.com/gofiber/fiber/v2"
	"github.com/pkg/errors"
	authMiddleware "sosmed-go-backend/auth/handler"
	chatService "sosmed-go-backend/chat/service"
	messagingService "sosmed-go-backend/firebase/service"
	"sosmed-go-backend/models"
	"strconv"
	"time"
)

type chatHandler struct {
	chatService      chatService.ChatService
	messagingService messagingService.MessagingService
}

func NewChatHandler(router fiber.Router, authMiddleware authMiddleware.AuthMiddleware, chatService chatService.ChatService, messagingService messagingService.MessagingService) {
	h := chatHandler{chatService, messagingService}

	router.Post("/chat", authMiddleware.Authenticate, h.create)
	router.Get("/chat/:chat_id", authMiddleware.Authenticate, h.getChat)
	router.Get("/chats", authMiddleware.Authenticate, h.getChats)
}

func (c *chatHandler) create(ctx *fiber.Ctx) error {
	var req models.CreateChatReq
	err := ctx.BodyParser(&req)
	if err != nil {
		return errors.WithMessage(models.ErrBadRequest, err.Error())
	}

	res, err := c.chatService.Create(ctx.Context(), &req)
	if err != nil {
		return err
	}

	if chatGroup, ok := res.(models.ChatGroupRes); ok {
		err := c.messagingService.SendChatNotification(ctx.Context(), chatGroup.Id)
		if err != nil {
			return err
		}
	}

	if chatPrivate, ok := res.(models.ChatGroupRes); ok {
		err := c.messagingService.SendChatNotification(ctx.Context(), chatPrivate.Id)
		if err != nil {
			return err
		}
	}

	return ctx.JSON(res)
}

func (c *chatHandler) getChats(ctx *fiber.Ctx) error {
	nextId, _ := strconv.ParseInt(ctx.Query("next_id"), 10, 64)
	if nextId == 0 || nextId > time.Now().UnixMilli() {
		nextId = time.Now().UnixMilli()
	}

	previousId, _ := strconv.ParseInt(ctx.Query("previous_id"), 10, 64)
	limit, _ := strconv.ParseInt(ctx.Query("limit"), 10, 64)
	if limit == 0 || limit > 10 {
		limit = 10
	}

	page := models.Page{
		PreviousId: previousId,
		NextId:     nextId,
		Size:       uint64(limit),
	}

	res, err := c.chatService.GetChats(ctx.Context(), page)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}

func (c *chatHandler) getChat(ctx *fiber.Ctx) error {
	chatId, _ := strconv.ParseInt(ctx.Params("chat_id"), 10, 64)

	res, err := c.chatService.GetChatById(ctx.Context(), chatId)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}
