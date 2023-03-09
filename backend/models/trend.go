package models

import (
	"gopkg.in/guregu/null.v4"
	"time"
)

type Tag struct {
	Id        int64
	UserId    int64
	FeedId    int64
	CommentId null.Int
	Name      string
	Type      string
	Country   null.String
	CreatedAt time.Time
}

type TrendingTags struct {
	Name    string      `json:"name"`
	Type    string      `json:"type"`
	Country null.String `json:"country"`
	Count   int64       `json:"count"`
}
