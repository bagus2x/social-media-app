package models

type File[T any] struct {
	Content T        `json:"content"`
	Name    string   `json:"name"`
	Size    int64    `json:"size"`
	Type    TypeFile `json:"type"`
}

type TypeFile string

func (t TypeFile) Valid() bool {
	return t == TypeFileVideo || t == TypeFileImage
}

const (
	TypeFileVideo TypeFile = "video"
	TypeFileImage TypeFile = "image"
)
