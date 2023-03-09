package models

import (
	"time"
)

type Member struct {
	UserId    int64
	ChatId    int64
	CreatedAt time.Time
}
