package handler

import (
	"github.com/gofiber/fiber/v2"
	"github.com/sirupsen/logrus"
	authService "sosmed-go-backend/auth/service"
	"sosmed-go-backend/models"
)

type authHandler struct {
	authService authService.AuthService
}

func NewAuthHandler(router fiber.Router, authService authService.AuthService) {
	h := authHandler{authService}

	router.Post("/auth/signup", h.signUp)
	router.Post("/auth/signin", h.signIn)
}

func (h *authHandler) signUp(ctx *fiber.Ctx) error {
	var req models.SignUpReq
	err := ctx.BodyParser(&req)
	if err != nil {
		logrus.Error(err)
		return models.ErrBadRequest
	}

	res, err := h.authService.SignUp(ctx.Context(), &req)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}

func (h *authHandler) signIn(ctx *fiber.Ctx) error {
	var req models.SignInReq
	err := ctx.BodyParser(&req)
	if err != nil {
		logrus.Error(err)
		return models.ErrBadRequest
	}

	res, err := h.authService.SignIn(ctx.Context(), &req)
	if err != nil {
		return err
	}

	return ctx.JSON(res)
}
