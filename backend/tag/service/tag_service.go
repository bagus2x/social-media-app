package service

import (
	"context"
	"sosmed-go-backend/models"
	tagRepo "sosmed-go-backend/tag/repository"
	"time"
)

type TagService struct {
	tagRepository tagRepo.TagRepository
}

func NewTagService(tagRepository tagRepo.TagRepository) TagService {
	return TagService{tagRepository}
}

func (t *TagService) GetTrending(ctx context.Context, duration time.Duration) ([]models.TrendingTags, error) {
	trending, err := t.tagRepository.GetTrendingIn(ctx, duration)
	if err != nil {
		return nil, err
	}

	return trending, nil
}
