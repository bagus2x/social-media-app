package handler

import (
	"github.com/gofiber/fiber/v2"
	"math"
	authMiddleware "sosmed-go-backend/auth/handler"
	"sosmed-go-backend/comment/service"
	"sosmed-go-backend/models"
	"strconv"
)

type commentHandler struct {
	commentService service.CommentService
}

func NewCommentHandler(router fiber.Router, authMiddleware authMiddleware.AuthMiddleware, commentService service.CommentService) {
	h := commentHandler{commentService}

	router.Post("/comment", authMiddleware.Authenticate, h.create)
	router.Get("/comment/:comment_id", authMiddleware.Authenticate, h.getCommentById)
	router.Get("/feed/:feed_id/comments", authMiddleware.Authenticate, h.getRootComments)
	router.Get("/comment/:comment_id/replies", authMiddleware.Authenticate, h.getChildComments)
}

func (h *commentHandler) create(ctx *fiber.Ctx) error {
	var req models.CreateCommentReq
	err := ctx.BodyParser(&req)
	if err != nil {
		return models.ErrBadRequest
	}

	res, err := h.commentService.Create(ctx.Context(), &req)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}

func (h *commentHandler) getCommentById(ctx *fiber.Ctx) error {
	commentId, _ := strconv.ParseInt(ctx.Params("comment_id"), 10, 64)
	res, err := h.commentService.GetCommentById(ctx.Context(), commentId)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}

func (h *commentHandler) getRootComments(ctx *fiber.Ctx) error {
	feedId, _ := strconv.ParseInt(ctx.Params("feed_id"), 10, 64)

	nextInt, _ := strconv.ParseInt(ctx.Query("next_id"), 10, 64)
	if nextInt == 0 {
		nextInt = math.MaxInt64
	}
	previousId, _ := strconv.ParseInt(ctx.Query("previous_id"), 10, 64)
	limit, _ := strconv.ParseInt(ctx.Query("limit"), 10, 64)
	if limit == 0 || limit > 30 {
		limit = 30
	}

	page := models.Page{
		PreviousId: previousId,
		NextId:     nextInt,
		Size:       uint64(limit),
	}

	res, err := h.commentService.GetRootComment(ctx.Context(), feedId, page)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}

func (h *commentHandler) getChildComments(ctx *fiber.Ctx) error {
	parentId, _ := strconv.ParseInt(ctx.Params("comment_id"), 10, 64)

	nextInt, _ := strconv.ParseInt(ctx.Query("next_id"), 10, 64)
	if nextInt == 0 {
		nextInt = math.MaxInt64
	}
	previousId, _ := strconv.ParseInt(ctx.Query("previous_id"), 10, 64)
	limit, _ := strconv.ParseInt(ctx.Query("limit"), 10, 64)
	if limit == 0 || limit > 30 {
		limit = 30
	}

	page := models.Page{
		PreviousId: previousId,
		NextId:     nextInt,
		Size:       uint64(limit),
	}

	res, err := h.commentService.GetChildComments(ctx.Context(), parentId, page)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}
