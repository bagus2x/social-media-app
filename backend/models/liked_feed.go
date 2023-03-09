package models

import "time"

type FavoriteFeed struct {
	LikerId   int64
	FeedId    int64
	CreatedAt time.Time
}
