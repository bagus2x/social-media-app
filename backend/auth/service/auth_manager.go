package service

import (
	"context"
	"fmt"
	"github.com/golang-jwt/jwt/v4"
	"github.com/pkg/errors"
	"github.com/sirupsen/logrus"
	"sosmed-go-backend/models"
	"time"
)

type AuthManager struct {
	accessTokenLifeTime  time.Duration
	accessTokenSecret    string
	refreshTokenLifeTime time.Duration
	refreshTokenSecret   string
}

func NewAuthManager(accessTokenLifeTime time.Duration, accessTokenSecret string, refreshTokenLifeTime time.Duration, refreshTokenSecret string) AuthManager {
	return AuthManager{accessTokenLifeTime, accessTokenSecret, refreshTokenLifeTime, refreshTokenSecret}
}

type claims struct {
	jwt.RegisteredClaims
	Profile models.Profile `json:"Profile"`
}

func (r *AuthManager) Create(_ context.Context, profile models.Profile) (models.Auth, error) {
	accessToken, err := createToken(r.accessTokenSecret, profile, r.accessTokenLifeTime)
	if err != nil {
		return models.Auth{}, err
	}

	refreshToken, err := createToken(r.refreshTokenSecret, profile, r.refreshTokenLifeTime)
	if err != nil {
		return models.Auth{}, err
	}

	return models.Auth{
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
	}, nil
}

func (r *AuthManager) ParseAccessToken(tokenString string) (models.Profile, error) {
	token, err := jwt.ParseWithClaims(tokenString, &claims{}, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, errors.Errorf("unexpected signing method: %v", token.Header["alg"])
		}
		return []byte(r.accessTokenSecret), nil
	})
	if err != nil {
		logrus.Error(err)
		if errors.Is(err, jwt.ErrTokenExpired) {
			return models.Profile{}, errors.WithMessage(models.ErrUnauthorized, "Token is expired")
		}
		return models.Profile{}, errors.WithMessage(models.ErrForbidden, "Token is invalid")
	}

	if claims, ok := token.Claims.(*claims); ok && token.Valid {
		if err != nil {
			return models.Profile{}, err
		}
		return claims.Profile, nil
	}

	return models.Profile{}, err
}

func createToken(key string, profile models.Profile, duration time.Duration) (string, error) {
	claims := &claims{
		RegisteredClaims: jwt.RegisteredClaims{
			Subject:   fmt.Sprintf("%d", profile.Id),
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(duration)),
		},
		Profile: profile,
	}
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	tokenString, err := token.SignedString([]byte(key))
	if err != nil {
		return "", err
	}

	return tokenString, nil
}
