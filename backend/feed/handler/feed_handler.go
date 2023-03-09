package handler

import (
	"github.com/gofiber/fiber/v2"
	"github.com/pkg/errors"
	"github.com/sirupsen/logrus"
	"math"
	"sosmed-go-backend/auth/handler"
	feedService "sosmed-go-backend/feed/service"
	"sosmed-go-backend/models"
	"strconv"
)

type feedHandler struct {
	feedService feedService.FeedService
}

func NewFeedHandler(router fiber.Router, feedService feedService.FeedService, authMiddleware handler.AuthMiddleware) {
	h := feedHandler{feedService}

	router.Post("/feed", authMiddleware.Authenticate, h.create)
	router.Get("/feeds", authMiddleware.Authenticate, h.getFeedsOfFollowedUsers)
	router.Get("/search/feeds", authMiddleware.Authenticate, h.searchFeeds)
	router.Get("/user/:user_id/feeds", authMiddleware.Authenticate, h.getFeedsByAuthor)
	router.Get("/feed/:feed_id", authMiddleware.Authenticate, h.getFeedsById)
	router.Patch("/feed/:feed_id/favorite", authMiddleware.Authenticate, h.favorite)
	router.Delete("/feed/:feed_id/favorite", authMiddleware.Authenticate, h.unfavorite)
}

func (h *feedHandler) create(ctx *fiber.Ctx) error {
	var req models.CreateFeedReq
	err := ctx.BodyParser(&req)
	if err != nil {
		logrus.Error(err)
		return models.ErrBadRequest
	}

	feed, err := h.feedService.Create(ctx.Context(), &req)
	if err != nil {
		return err
	}

	return ctx.JSON(feed)
}

func (h *feedHandler) getFeedsOfFollowedUsers(ctx *fiber.Ctx) error {
	nextInt, _ := strconv.ParseInt(ctx.Query("next_id"), 10, 64)
	if nextInt == 0 {
		nextInt = math.MaxInt64
	}
	previousId, _ := strconv.ParseInt(ctx.Query("previous_id"), 10, 64)
	limit, _ := strconv.ParseInt(ctx.Query("limit"), 10, 64)
	if limit == 0 || limit > 10 {
		limit = 10
	}

	page := models.Page{
		PreviousId: previousId,
		NextId:     nextInt,
		Size:       uint64(limit),
	}

	res, err := h.feedService.GetFeedsOfFollowedUsers(ctx.Context(), page)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}

func (h *feedHandler) searchFeeds(ctx *fiber.Ctx) error {
	nextInt, _ := strconv.ParseInt(ctx.Query("next_id"), 10, 64)
	if nextInt == 0 {
		nextInt = math.MaxInt64
	}
	previousId, _ := strconv.ParseInt(ctx.Query("previous_id"), 10, 64)
	limit, _ := strconv.ParseInt(ctx.Query("limit"), 10, 64)
	if limit == 0 || limit > 10 {
		limit = 10
	}

	page := models.Page{
		PreviousId: previousId,
		NextId:     nextInt,
		Size:       uint64(limit),
	}

	res, err := h.feedService.SearchFeeds(ctx.Context(), ctx.Query("query"), page)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}

func (h *feedHandler) getFeedsByAuthor(ctx *fiber.Ctx) error {
	authorId, err := strconv.ParseInt(ctx.Params("user_id"), 10, 64)
	if err != nil {
		return errors.WithMessage(models.ErrBadRequest, "next_id must be integer and greater than > 0")
	}

	nextInt, _ := strconv.ParseInt(ctx.Query("next_id"), 10, 64)
	if nextInt == 0 {
		nextInt = math.MaxInt64
	}
	previousId, _ := strconv.ParseInt(ctx.Query("previous_id"), 10, 64)
	limit, _ := strconv.ParseInt(ctx.Query("limit"), 10, 64)
	if limit == 0 || limit > 10 {
		limit = 10
	}

	page := models.Page{
		PreviousId: previousId,
		NextId:     nextInt,
		Size:       uint64(limit),
	}

	feed, err := h.feedService.GetFeedsByAuthor(ctx.Context(), authorId, page)
	if err != nil {
		return err
	}

	return ctx.JSON(feed)
}

func (h *feedHandler) getFeedsById(ctx *fiber.Ctx) error {
	feedId, err := strconv.ParseInt(ctx.Params("feed_id"), 10, 64)
	if err != nil {
		return errors.WithMessage(models.ErrBadRequest, "feed_id must be integer and greater than > 0")
	}

	feed, err := h.feedService.GetFeedById(ctx.Context(), feedId)
	if err != nil {
		return err
	}

	return ctx.JSON(feed)
}

func (h *feedHandler) favorite(ctx *fiber.Ctx) error {
	feedId, err := strconv.ParseInt(ctx.Params("feed_id"), 10, 64)
	if err != nil {
		return errors.WithMessage(models.ErrBadRequest, "feed_id must be integer and greater than > 0")
	}

	err = h.feedService.Favorite(ctx.Context(), feedId)
	if err != nil {
		return err
	}

	return ctx.SendStatus(fiber.StatusNoContent)
}

func (h *feedHandler) unfavorite(ctx *fiber.Ctx) error {
	feedId, err := strconv.ParseInt(ctx.Params("feed_id"), 10, 64)
	if err != nil {
		return errors.WithMessage(models.ErrBadRequest, "feed_id must be integer and greater than > 0")
	}

	err = h.feedService.Unfavorite(ctx.Context(), feedId)
	if err != nil {
		return err
	}

	return ctx.SendStatus(fiber.StatusNoContent)
}
