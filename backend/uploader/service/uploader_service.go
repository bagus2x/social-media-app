package service

import (
	"context"
	"mime/multipart"
	"sosmed-go-backend/models"
	uploaderRepo "sosmed-go-backend/uploader/repository"
)

type UploaderService struct {
	uploaderRepository uploaderRepo.UploaderRepository
}

func NewUploaderService(uploaderRepository uploaderRepo.UploaderRepository) UploaderService {
	return UploaderService{uploaderRepository}
}

func (s *UploaderService) Upload(ctx context.Context, file models.File[multipart.File]) (models.File[string], error) {
	return s.uploaderRepository.Upload(ctx, file)
}
