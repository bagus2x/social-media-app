package service

import (
	"context"
	chatRepository "sosmed-go-backend/chat/repository"
	"sosmed-go-backend/common"
	"sosmed-go-backend/firebase/service"
	messageRepository "sosmed-go-backend/message/repository"
	"sosmed-go-backend/models"
	"time"
)

type MessageService struct {
	messageRepository messageRepository.MessageRepository
	chatRepository    chatRepository.ChatRepository
	messagingService  service.MessagingService
}

func NewMessageRepository(
	messageRepository messageRepository.MessageRepository,
	chatRepository chatRepository.ChatRepository,
) MessageService {
	return MessageService{
		messageRepository: messageRepository,
		chatRepository:    chatRepository,
	}
}

func (m *MessageService) Create(ctx context.Context, req *models.CreateMessageReq) (models.MessageRes, error) {
	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return models.MessageRes{}, err
	}

	message := models.Message{
		ChatId:      req.ChatId,
		Sender:      profile,
		Description: req.Description,
		Medias:      req.Medias,
		CreatedAt:   time.Now(),
	}

	err = m.messageRepository.WithTransaction(ctx, func(ctx context.Context) error {
		err := m.messageRepository.Create(ctx, &message)
		if err != nil {
			return err
		}

		err = m.chatRepository.UpdateLastMessageSent(ctx, req.ChatId)
		if err != nil {
			return err
		}

		return nil
	})

	res := models.MessageRes{
		Id:          message.Id,
		ChatId:      message.ChatId,
		Sender:      message.Sender,
		Description: message.Description,
		Medias:      message.Medias,
		CreatedAt:   message.CreatedAt.UnixMilli(),
	}

	return res, nil
}

func (m *MessageService) GetMessages(ctx context.Context, chatId int64, page models.Page) ([]models.MessageRes, error) {
	res := make([]models.MessageRes, 0)

	messages, err := m.messageRepository.GetByChatId(ctx, chatId, page)
	if err != nil {
		return []models.MessageRes{}, err
	}

	for _, message := range messages {
		res = append(res, models.MessageRes{
			Id:          message.Id,
			ChatId:      message.ChatId,
			Sender:      message.Sender,
			Description: message.Description,
			Medias:      message.Medias,
			CreatedAt:   message.CreatedAt.UnixMilli(),
		})
	}

	return res, nil
}
