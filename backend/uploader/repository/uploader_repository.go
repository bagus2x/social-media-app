package repository

import (
	"context"
	"fmt"
	"github.com/cloudinary/cloudinary-go/v2"
	"github.com/cloudinary/cloudinary-go/v2/api/uploader"
	"github.com/pkg/errors"
	"github.com/sirupsen/logrus"
	"mime/multipart"
	"sosmed-go-backend/models"
)

type UploaderRepository struct {
	cld *cloudinary.Cloudinary
}

func NewUploaderRepository(cld *cloudinary.Cloudinary) UploaderRepository {
	return UploaderRepository{cld}
}

func (r *UploaderRepository) Upload(ctx context.Context, file models.File[multipart.File]) (models.File[string], error) {
	var publicId string
	if file.Type == models.TypeFileVideo {
		publicId = fmt.Sprintf("sosmed/video/%s", file.Name)
	}
	if file.Type == models.TypeFileImage {
		publicId = fmt.Sprintf("sosmed/image/%s", file.Name)
	}
	if len(publicId) == 0 {
		return models.File[string]{}, errors.WithMessagef(models.ErrBadRequest, "unsupported file type: %s")
	}

	result, err := r.cld.Upload.Upload(ctx, file.Content, uploader.UploadParams{PublicID: publicId, ResourceType: string(file.Type)})
	if err != nil {
		logrus.Error(err)
		return models.File[string]{}, err
	}

	fileResult := models.File[string]{
		Content: result.SecureURL,
		Name:    result.DisplayName,
		Size:    file.Size,
		Type:    file.Type,
	}

	return fileResult, nil
}
