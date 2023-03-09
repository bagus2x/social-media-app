package service

import (
	"context"
	"database/sql"
	"github.com/pkg/errors"
	"github.com/sirupsen/logrus"
	"golang.org/x/crypto/bcrypt"
	"sosmed-go-backend/models"
	userRepo "sosmed-go-backend/user/repository"
	"time"
)

type AuthService struct {
	userRepository userRepo.UserRepository
	authManager    AuthManager
}

func NewAuthService(userRepository userRepo.UserRepository, authManager AuthManager) AuthService {
	return AuthService{userRepository, authManager}
}

func (s *AuthService) SignUp(ctx context.Context, req *models.SignUpReq) (models.AuthResponse, error) {
	user, err := s.userRepository.GetByUsernameOrEmail(ctx, req.Username)
	if err != nil && !errors.Is(err, sql.ErrNoRows) {
		return models.AuthResponse{}, err
	} else if user != (models.User{}) {
		return models.AuthResponse{}, errors.WithMessage(models.ErrConflict, "Username or email already exists")
	}

	user, err = s.userRepository.GetByUsernameOrEmail(ctx, req.Email)
	if err != nil && !errors.Is(err, sql.ErrNoRows) {
		return models.AuthResponse{}, err
	} else if user != (models.User{}) {
		return models.AuthResponse{}, errors.WithMessage(models.ErrConflict, "Username or email already exists")
	}

	password, err := bcrypt.GenerateFromPassword([]byte(req.Password), 12)
	if err != nil {
		return models.AuthResponse{}, err
	}

	user = models.User{
		Username:  req.Username,
		Name:      req.Username,
		Email:     req.Email,
		Password:  string(password),
		CreatedAt: time.Now(),
		UpdatedAt: time.Now(),
	}
	err = s.userRepository.Create(ctx, &user)
	if err != nil {
		return models.AuthResponse{}, err
	}

	profile := models.Profile{
		Id:       user.Id,
		Name:     user.Name,
		Username: user.Name,
		Photo:    user.Photo,
	}

	auth, err := s.authManager.Create(ctx, profile)
	if err != nil {
		return models.AuthResponse{}, err
	}

	res := models.AuthResponse{
		AccessToken:  auth.AccessToken,
		RefreshToken: auth.RefreshToken,
		Profile:      profile,
	}
	return res, nil
}

func (s *AuthService) SignIn(ctx context.Context, req *models.SignInReq) (models.AuthResponse, error) {
	user, err := s.userRepository.GetByUsernameOrEmail(ctx, req.UsernameOrEmail)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			logrus.Error(err)
			return models.AuthResponse{}, errors.WithMessagef(models.ErrNotFound, "User does not exists")
		}
		return models.AuthResponse{}, err
	}

	err = bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(req.Password))
	if err != nil {
		logrus.Error(err)
		return models.AuthResponse{}, errors.WithMessagef(models.ErrBadRequest, "Password doesn't match")
	}

	profile := models.Profile{
		Id:       user.Id,
		Name:     user.Name,
		Username: user.Name,
		Photo:    user.Photo,
	}

	auth, err := s.authManager.Create(ctx, profile)
	if err != nil {
		return models.AuthResponse{}, err
	}

	res := models.AuthResponse{
		AccessToken:  auth.AccessToken,
		RefreshToken: auth.RefreshToken,
		Profile:      profile,
	}
	return res, nil
}
