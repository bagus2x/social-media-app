package models

import (
	"gopkg.in/guregu/null.v4"
	"time"
)

type Feed struct {
	Id             int64       `json:"id"`
	Author         Profile     `json:"author"`
	Description    string      `json:"description"`
	Medias         []Media     `json:"medias"`
	TotalFavorites int         `json:"totalFavorites"`
	TotalComments  int         `json:"totalComments"`
	TotalReposts   int         `json:"totalReposts"`
	Language       null.String `json:"language"`
	CreatedAt      time.Time   `json:"createdAt"`
	UpdatedAt      time.Time   `json:"updatedAt"`
}

type CreateFeedReq struct {
	Description string      `json:"description"`
	Medias      []Media     `json:"medias"`
	Language    null.String `json:"language"`
}

type FeedRes struct {
	Id             int64       `json:"id"`
	Author         Profile     `json:"author"`
	Description    string      `json:"description"`
	Medias         []Media     `json:"medias"`
	TotalFavorites int         `json:"totalFavorites"`
	TotalComments  int         `json:"totalComments"`
	TotalReposts   int         `json:"totalReposts"`
	Language       null.String `json:"language"`
	Favorite       bool        `json:"favorite"`
	CreatedAt      int64       `json:"createdAt"`
	UpdatedAt      int64       `json:"updatedAt"`
}
