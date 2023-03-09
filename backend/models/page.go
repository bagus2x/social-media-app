package models

import (
	"math"
	"time"
)

type Page struct {
	PreviousId int64
	NextId     int64
	Size       uint64
}

func PageDefault() Page {
	return Page{
		PreviousId: 0,
		NextId:     time.Now().UnixMilli(),
		Size:       math.MaxInt64,
	}
}
