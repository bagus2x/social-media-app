package service

import (
	"context"
	"encoding/json"
	"firebase.google.com/go/messaging"
	"github.com/sirupsen/logrus"
	"math"
	memberRepository "sosmed-go-backend/chat/repository"
	chatService "sosmed-go-backend/chat/service"
	"sosmed-go-backend/models"
	userRepository "sosmed-go-backend/user/repository"
	"strconv"
)

type MessagingService struct {
	client           *messaging.Client
	userRepository   userRepository.UserRepository
	memberRepository memberRepository.MemberRepository
	chatService      chatService.ChatService
}

func NewMessagingService(
	client *messaging.Client,
	userRepository userRepository.UserRepository,
	memberRepository memberRepository.MemberRepository,
	chatService chatService.ChatService,
) MessagingService {
	return MessagingService{client, userRepository, memberRepository, chatService}
}

func (m *MessagingService) SendChatNotification(ctx context.Context, chatId int64) error {
	userIds, err := m.memberRepository.GetAllMemberIds(ctx, chatId)
	if err != nil {
		logrus.Error(err)
		return err
	}

	users, err := m.userRepository.GetByIds(ctx, userIds, models.Page{NextId: math.MaxInt64, Size: uint64(len(userIds))})
	if err != nil {
		logrus.Error(err)
		return err
	}

	messages := make([]*messaging.Message, 0)
	for _, user := range users {
		if !user.FcmToken.Valid {
			continue
		}
		chat, err := m.chatService.GetChatByChatIdAndUserId(ctx, chatId, user.Id)
		if err != nil {
			logrus.Error(err)
			continue
		}
		if chat == nil {
			logrus.Error("Chat is nil")
			continue
		}

		content, err := json.Marshal(chat)
		if err != nil {
			logrus.Error(err)
			continue
		}

		var title string
		var body string
		var imageUrl string
		if groupChat, ok := chat.(models.ChatGroupRes); ok {
			if len(groupChat.RecentMessages) != 0 {
				title = groupChat.RecentMessages[0].Sender.Name
				body = groupChat.RecentMessages[0].Description
			}
		}
		if privateChat, ok := chat.(models.ChatPrivateRes); ok {
			if len(privateChat.RecentMessages) != 0 {
				title = privateChat.RecentMessages[0].Sender.Name
				body = privateChat.RecentMessages[0].Description
			}
		}

		message := messaging.Message{
			Data: map[string]string{
				"type":    "receive_new_chat_message",
				"content": string(content),
				"user_id": strconv.Itoa(int(user.Id)),
			},
			Notification: &messaging.Notification{
				Title:    title,
				Body:     body,
				ImageURL: imageUrl,
			},
			Token: user.FcmToken.String,
		}

		messages = append(messages, &message)
	}

	all, err := m.client.SendAll(ctx, messages)
	if err != nil {
		return err
	}

	logrus.Info("SuccessCount ", all.SuccessCount)
	logrus.Info("FailureCount ", all.FailureCount)

	return nil
}
