package models

import (
	"gopkg.in/guregu/null.v4"
	"time"
)

type Chat struct {
	Id                int64
	Creator           Profile
	Name              string
	Photo             null.String
	Type              TypeChat
	PrivateChatId     null.String
	CreatedAt         time.Time
	UpdatedAt         time.Time
	LastMessageSentAt time.Time
}

type TypeChat string

const (
	TypeGroup   = "group"
	TypePrivate = "private"
)

func (t TypeChat) Valid() bool {
	return t == TypeGroup || t == TypePrivate
}

type ChatGroupRes struct {
	Id                int64        `json:"id"`
	Creator           Profile      `json:"creator"`
	Name              string       `json:"name"`
	Photo             null.String  `json:"photo"`
	Type              TypeChat     `json:"type"`
	RecentMessages    []MessageRes `json:"recentMessages"`
	Members           []Profile    `json:"members"`
	CreatedAt         int64        `json:"createdAt"`
	UpdatedAt         int64        `json:"updatedAt"`
	LastMessageSentAt int64        `json:"lastMessageSentAt"`
}

type ChatPrivateRes struct {
	Id                int64        `json:"id"`
	PrivateChatId     string       `json:"privateChatId"`
	Pair              Profile      `json:"pair"`
	TypeChat          TypeChat     `json:"type"`
	RecentMessages    []MessageRes `json:"recentMessages"`
	CreatedAt         int64        `json:"createdAt"`
	LastMessageSentAt int64        `json:"lastMessageSentAt"`
}

type CreateChatReq struct {
	Members []int64     `json:"members"`
	Name    string      `json:"name"`
	Photo   null.String `json:"photo"`
	Type    TypeChat    `json:"type"`
}
