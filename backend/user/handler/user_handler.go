package handler

import (
	"github.com/gofiber/fiber/v2"
	"gopkg.in/guregu/null.v4"
	"math"
	authMiddleware "sosmed-go-backend/auth/handler"
	"sosmed-go-backend/models"
	userService "sosmed-go-backend/user/service"
	"strconv"
)

type userHandler struct {
	userService userService.UserService
}

func NewUserHandler(router fiber.Router, userService userService.UserService, authMiddleware authMiddleware.AuthMiddleware) {
	h := userHandler{userService}
	router.Get("/user/:user_id", authMiddleware.Authenticate, h.getUserById)
	router.Get("/user/:user_id/followers", authMiddleware.Authenticate, h.getFollowers)
	router.Get("/user/:user_id/following", authMiddleware.Authenticate, h.getFollowing)
	router.Patch("/user/:user_id/following", authMiddleware.Authenticate, h.follow)
	router.Delete("/user/:user_id/following", authMiddleware.Authenticate, h.unfollow)
	router.Patch("/user/fcm-token", authMiddleware.Authenticate, h.updateFcmToken)
	router.Get("/search/users", authMiddleware.Authenticate, h.searchUsers)
	router.Patch("/user", authMiddleware.Authenticate, h.update)
}

func (h *userHandler) getUserById(ctx *fiber.Ctx) error {
	userId, _ := strconv.ParseInt(ctx.Params("user_id"), 10, 64)
	res, err := h.userService.GetUserById(ctx.Context(), userId)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}

func (h *userHandler) getFollowers(ctx *fiber.Ctx) error {
	userId, _ := strconv.ParseInt(ctx.Params("user_id"), 10, 64)
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

	res, err := h.userService.GetFollowers(ctx.Context(), userId, page)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}

func (h *userHandler) getFollowing(ctx *fiber.Ctx) error {
	userId, _ := strconv.ParseInt(ctx.Params("user_id"), 10, 64)
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

	res, err := h.userService.GetFollowing(ctx.Context(), userId, page)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}

func (h *userHandler) searchUsers(ctx *fiber.Ctx) error {
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

	res, err := h.userService.SearchUsers(ctx.Context(), ctx.Query("query"), page)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}

func (h *userHandler) follow(ctx *fiber.Ctx) error {
	userId, _ := strconv.ParseInt(ctx.Params("user_id"), 10, 64)
	err := h.userService.Follow(ctx.Context(), userId)
	if err != nil {
		return err
	}

	return ctx.SendStatus(fiber.StatusNoContent)
}

func (h *userHandler) unfollow(ctx *fiber.Ctx) error {
	userId, _ := strconv.ParseInt(ctx.Params("user_id"), 10, 64)
	err := h.userService.Unfollow(ctx.Context(), userId)
	if err != nil {
		return err
	}

	return ctx.SendStatus(fiber.StatusNoContent)
}

func (h *userHandler) updateFcmToken(ctx *fiber.Ctx) error {
	type Req struct {
		FcmToken string `json:"fcmToken"`
	}
	var req Req

	err := ctx.BodyParser(&req)
	if err != nil {
		return err
	}

	err = h.userService.UpdateFcmToken(ctx.Context(), null.NewString(req.FcmToken, req.FcmToken != ""))
	if err != nil {
		return err
	}

	return ctx.SendStatus(fiber.StatusNoContent)
}

func (h *userHandler) update(ctx *fiber.Ctx) error {
	var req models.UpdateUserReq

	err := ctx.BodyParser(&req)
	if err != nil {
		return err
	}

	res, err := h.userService.Update(ctx.Context(), &req)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}
