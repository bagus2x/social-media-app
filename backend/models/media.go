package models

type Media struct {
	// ImageUrl will represent thumbnailUrl if type is video
	ImageUrl string `json:"imageUrl"`
	VideoUrl string `json:"videoUrl"`
	Type     string `json:"type"`
}
