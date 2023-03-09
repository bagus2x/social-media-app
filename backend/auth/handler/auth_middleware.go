package handler

import (
	"github.com/gofiber/fiber/v2"
	authService "sosmed-go-backend/auth/service"
	"sosmed-go-backend/models"
	"strings"
)

type AuthMiddleware struct {
	authManager authService.AuthManager
}

func NewAuthMiddleware(authManager authService.AuthManager) AuthMiddleware {
	return AuthMiddleware{authManager}
}

func (m *AuthMiddleware) Authenticate(ctx *fiber.Ctx) error {
	bearer := strings.Split(ctx.Get("Authorization"), " ")
	if len(bearer) != 2 {
		return models.ErrForbidden
	}

	accessToken := bearer[1]
	profile, err := m.authManager.ParseAccessToken(accessToken)
	if err != nil {
		return err
	}

	ctx.Locals("profile", profile)

	return ctx.Next()
}
