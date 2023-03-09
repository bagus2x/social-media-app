package models

import "time"

type Message struct {
	Id          int64
	ChatId      int64
	Sender      Profile
	Description string
	Medias      []Media
	CreatedAt   time.Time
}

type MessageRes struct {
	Id          int64   `json:"id"`
	ChatId      int64   `json:"chatId"`
	Sender      Profile `json:"sender"`
	Description string  `json:"description"`
	Medias      []Media `json:"medias"`
	CreatedAt   int64   `json:"createdAt"`
}

type CreateMessageReq struct {
	ChatId      int64   `json:"chatId"`
	Description string  `json:"description"`
	Medias      []Media `json:"medias"`
}

type MessageSocket[T MessageRes | string] struct {
	Success bool `json:"success"`
	Content T    `json:"content"` // string = error message
}

func MessageToResponse(message Message) MessageRes {
	return MessageRes{
		Id:          message.Id,
		ChatId:      message.ChatId,
		Sender:      message.Sender,
		Description: message.Description,
		Medias:      message.Medias,
		CreatedAt:   message.CreatedAt.UnixMilli(),
	}
}

func MessagesToResponses(messages []Message) []MessageRes {
	res := make([]MessageRes, 0)
	for _, message := range messages {
		res = append(res, MessageToResponse(message))
	}
	return res
}
