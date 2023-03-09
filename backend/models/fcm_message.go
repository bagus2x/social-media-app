package models

import "firebase.google.com/go/messaging"

type FcmMessage struct {
	UserId  int64
	Message *messaging.Message
}
