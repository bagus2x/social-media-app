package common

import (
	"context"
	"sosmed-go-backend/models"
)

func GetCurrentProfileFromContext(ctx context.Context) (models.Profile, error) {
	if author, ok := ctx.Value("profile").(models.Profile); ok && (author != models.Profile{}) {
		return author, nil
	}

	return models.Profile{}, models.ErrUnauthorized
}
