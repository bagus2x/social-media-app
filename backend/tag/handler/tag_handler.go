package handler

import (
	"github.com/gofiber/fiber/v2"
	"github.com/pkg/errors"
	"sosmed-go-backend/models"
	tagService "sosmed-go-backend/tag/service"
	"strconv"
	"time"
)

type tagHandler struct {
	tagService tagService.TagService
}

func NewTagService(router fiber.Router, tagService tagService.TagService) {
	h := tagHandler{tagService}

	router.Get("/tag/trending/:duration", h.getTrending)
}

func (t *tagHandler) getTrending(ctx *fiber.Ctx) error {
	durationMillis, err := strconv.ParseInt(ctx.Params("duration"), 10, 64)
	if err != nil {
		return errors.WithMessage(models.ErrBadRequest, err.Error())
	}

	duration := time.Duration(durationMillis) * time.Millisecond

	res, err := t.tagService.GetTrending(ctx.Context(), duration)
	if err != nil {
		return err
	}
	return ctx.JSON(res)
}
