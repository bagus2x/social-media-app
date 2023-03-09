package models

import (
	"gopkg.in/guregu/null.v4"
	"time"
)

type User struct {
	Id             int64       `json:"id"`
	Username       string      `json:"username"`
	Name           string      `json:"name"`
	Email          string      `json:"email"`
	Password       string      `json:"password"`
	Photo          null.String `json:"photo"`
	Header         null.String `json:"header"`
	Bio            null.String `json:"bio"`
	Location       null.String `json:"location"`
	Website        null.String `json:"website"`
	Verified       bool        `json:"verified"`
	DateOfBirth    null.String `json:"dateOfBirth"`
	TotalFollowers int         `json:"totalFollowers"`
	TotalFollowing int         `json:"totalFollowing"`
	FcmToken       null.String `json:"fcmToken"`
	CreatedAt      time.Time   `json:"createdAt"`
	UpdatedAt      time.Time   `json:"updatedAt"`
}

type Profile struct {
	Id       int64       `json:"id"`
	Name     string      `json:"name"`
	Username string      `json:"username"`
	Photo    null.String `json:"photo"`
}

type CreateUserReq struct {
	Username string `json:"username"`
	Name     string `json:"name"`
	Email    string `json:"email"`
	Password string `json:"omitempty"`
}

type UpdateUserReq struct {
	Username    null.String `json:"username"`
	Name        null.String `json:"name"`
	Email       null.String `json:"email"`
	Password    null.String `json:"password"`
	Photo       null.String `json:"photo"`
	Header      null.String `json:"header"`
	Bio         null.String `json:"bio"`
	Location    null.String `json:"location"`
	Website     null.String `json:"website"`
	DateOfBirth null.String `json:"dateOfBirth"`
}

type UserResponse struct {
	Id             int64       `json:"id"`
	Username       string      `json:"username"`
	Name           string      `json:"name"`
	Email          string      `json:"email"`
	Photo          null.String `json:"photo"`
	Header         null.String `json:"header"`
	Bio            null.String `json:"bio"`
	Location       null.String `json:"location"`
	Website        null.String `json:"website"`
	Verified       bool        `json:"verified"`
	DateOfBirth    null.String `json:"dateOfBirth"`
	TotalFollowers int         `json:"totalFollowers"`
	TotalFollowing int         `json:"totalFollowing"`
	Following      bool        `json:"following"`
	CreatedAt      int64       `json:"createdAt"`
	UpdatedAt      int64       `json:"updatedAt"`
}
