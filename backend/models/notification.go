package models

import (
	"gopkg.in/guregu/null.v4"
	"time"
)

type Notification struct {
	Id        int64
	OwnerId   int64
	Issuer    Profile
	FeedId    null.Int
	CommentId null.Int
	Type      NotificationType
	Seen      bool
	CreatedAt time.Time
}

type NotificationType string

const (
	FeedLiked      = "feed_liked"
	FeedCommented  = "feed_commented"
	CommentReplied = "comment_replied"
	UserFollowing  = "user_following"
)

type NotificationRes struct {
	Id          int64            `json:"id"`
	Issuer      *Profile         `json:"issuer"`
	Icon        null.String      `json:"icon"`
	Description string           `json:"description"`
	Medias      []Media          `json:"medias"`
	DataId      null.Int         `json:"dataId"`
	Type        NotificationType `json:"type"`
	Seen        bool             `json:"seen"`
	CreatedAt   int64            `json:"createdAt"`
}

type CreateNotificationReq struct {
	OwnerId   int64
	Issuer    *Profile
	FeedId    null.Int
	CommentId null.Int
	Type      NotificationType
}
