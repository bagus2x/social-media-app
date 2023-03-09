package models

import "time"

type FollowedUser struct {
	FollowedId int64
	FollowerId int64
	CreatedAt  time.Time
}
