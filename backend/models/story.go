package models

import "time"

type Story struct {
	Id        int64     `json:"id"`
	Author    Profile   `json:"author"`
	Media     Media     `json:"media"`
	CreatedAt time.Time `json:"createdAt"`
}

type CreateStoryReq struct {
	Author Profile `json:"author"`
	Media  Media   `json:"media"`
}

type StoriesRes struct {
	Author  Profile `json:"author"`
	Stories []Story `json:"stories"`
}
