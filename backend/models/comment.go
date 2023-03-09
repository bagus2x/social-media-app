package models

import (
	"gopkg.in/guregu/null.v4"
	"time"
)

type Comment struct {
	Id             int64
	FeedId         int64
	ParentId       null.Int
	Path           string
	Author         Profile
	Description    string
	Medias         []Media
	TotalFavorites int
	TotalReplies   int
	CreatedAt      time.Time
}

type CommentRes struct {
	Id             int64    `json:"id"`
	FeedId         int64    `json:"feedId"`
	ParentId       null.Int `json:"parentId"`
	Path           string   `json:"path"`
	Author         Profile  `json:"author"`
	Description    string   `json:"description"`
	Medias         []Media  `json:"medias"`
	TotalFavorites int      `json:"totalFavorites"`
	TotalReplies   int      `json:"totalReplies"`
	CreatedAt      int64    `json:"createdAt"`
}

type CreateCommentReq struct {
	FeedId      int64   `json:"feedId"`
	ParentId    int64   `json:"parentId"`
	Description string  `json:"description"`
	Medias      []Media `json:"medias"`
}
