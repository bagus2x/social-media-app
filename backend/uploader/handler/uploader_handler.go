package handler

import (
	"github.com/gofiber/fiber/v2"
	"github.com/pkg/errors"
	"github.com/sirupsen/logrus"
	"mime/multipart"
	"sosmed-go-backend/auth/handler"
	"sosmed-go-backend/models"
	uploaderService "sosmed-go-backend/uploader/service"
	"strings"
)

type uploaderHandler struct {
	uploaderService uploaderService.UploaderService
}

func NewUploaderHandler(router fiber.Router, authMiddleware handler.AuthMiddleware, uploaderService uploaderService.UploaderService) {
	h := uploaderHandler{uploaderService}
	router.Post("/upload", authMiddleware.Authenticate, h.upload)
}

func (h *uploaderHandler) upload(ctx *fiber.Ctx) error {
	fileHeader, err := ctx.FormFile("file")
	if err != nil {
		logrus.Error(err)
		return errors.WithMessage(models.ErrBadRequest, "File must be provided")
	}

	multipartFile, err := fileHeader.Open()
	if err != nil {
		logrus.Error(err)
		return errors.WithMessage(models.ErrBadRequest, "File must be provided")
	}

	var typeFile models.TypeFile

	if strings.Contains(fileHeader.Header.Get("Content-Type"), "image") {
		typeFile = models.TypeFileImage
	}
	if strings.Contains(fileHeader.Header.Get("Content-Type"), "video") {
		typeFile = models.TypeFileVideo
	}

	file := models.File[multipart.File]{
		Content: multipartFile,
		Name:    fileHeader.Filename,
		Size:    fileHeader.Size,
		Type:    typeFile,
	}

	res, err := h.uploaderService.Upload(ctx.Context(), file)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}
