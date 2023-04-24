package handler

import (
	"github.com/gofiber/fiber/v2"
	"math"
	"sosmed-go-backend/auth/handler"
	"sosmed-go-backend/models"
	notificationSvc "sosmed-go-backend/notification/service"
	"strconv"
)

type notificationHandler struct {
	notificationService notificationSvc.NotificationService
}

func NewNotificationHandler(router fiber.Router, authMiddleware handler.AuthMiddleware, notificationService notificationSvc.NotificationService) {
	h := notificationHandler{notificationService}
	router.Get("/notifications", authMiddleware.Authenticate, h.Get)
}

func (h *notificationHandler) Get(ctx *fiber.Ctx) error {
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

	notifications, err := h.notificationService.Get(ctx.Context(), page)
	if err != nil {
		return err
	}

	names := make([]string, 0)

	for _, name := range notifications {
		names = append(names, strconv.Itoa(int(name.Id))+""+name.Issuer.Name)
	}

	return ctx.JSON(notifications)
}
