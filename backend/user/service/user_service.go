package service

import (
	"context"
	"database/sql"
	"github.com/pkg/errors"
	"gopkg.in/guregu/null.v4"
	"sosmed-go-backend/common"
	"sosmed-go-backend/models"
	userRepo "sosmed-go-backend/user/repository"
	"time"
)

type UserService struct {
	userRepository         userRepo.UserRepository
	followedUserRepository userRepo.FollowedUserRepository
}

func NewUserService(userRepository userRepo.UserRepository, followedUserRepository userRepo.FollowedUserRepository) UserService {
	return UserService{userRepository, followedUserRepository}
}

func (s *UserService) GetUserById(ctx context.Context, userId int64) (models.UserResponse, error) {
	user, err := s.userRepository.GetById(ctx, userId)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return models.UserResponse{}, errors.WithMessagef(models.ErrNotFound, "User not found")
		}

		return models.UserResponse{}, err
	}

	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return models.UserResponse{}, err
	}

	following, err := s.followedUserRepository.CheckFollowingByIds(ctx, []int64{user.Id}, profile.Id)
	if err != nil {
		return models.UserResponse{}, err
	}

	res := models.UserResponse{
		Id:             user.Id,
		Username:       user.Username,
		Name:           user.Name,
		Email:          user.Email,
		Photo:          user.Photo,
		Header:         user.Header,
		Bio:            user.Bio,
		Location:       user.Location,
		Website:        user.Website,
		Verified:       user.Verified,
		DateOfBirth:    user.DateOfBirth,
		TotalFollowers: user.TotalFollowers,
		TotalFollowing: user.TotalFollowing,
		Following:      following[user.Id],
		CreatedAt:      user.CreatedAt.UnixMilli(),
		UpdatedAt:      user.UpdatedAt.UnixMilli(),
	}

	return res, nil
}

func (s *UserService) SearchUsers(ctx context.Context, query string, page models.Page) ([]models.UserResponse, error) {
	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return []models.UserResponse{}, err
	}

	users, err := s.userRepository.Search(ctx, query, page)
	if err != nil {
		return []models.UserResponse{}, err
	}

	userIds := make([]int64, 0)
	for _, user := range users {
		userIds = append(userIds, user.Id)
	}

	following, err := s.followedUserRepository.CheckFollowingByIds(ctx, userIds, profile.Id)
	if err != nil {
		return []models.UserResponse{}, err
	}

	res := make([]models.UserResponse, 0)
	for _, user := range users {
		res = append(res, models.UserResponse{
			Id:             user.Id,
			Username:       user.Username,
			Name:           user.Name,
			Email:          user.Email,
			Photo:          user.Photo,
			Header:         user.Header,
			Bio:            user.Bio,
			Location:       user.Location,
			Website:        user.Website,
			Verified:       user.Verified,
			DateOfBirth:    user.DateOfBirth,
			TotalFollowers: user.TotalFollowers,
			TotalFollowing: user.TotalFollowing,
			Following:      following[user.Id],
			CreatedAt:      user.CreatedAt.UnixMilli(),
			UpdatedAt:      user.UpdatedAt.UnixMilli(),
		})
	}

	return res, nil
}

func (s *UserService) GetUserByIds(ctx context.Context, userIds []int64, page models.Page) ([]models.UserResponse, error) {
	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return []models.UserResponse{}, err
	}

	users, err := s.userRepository.GetByIds(ctx, userIds, page)
	if err != nil {
		return []models.UserResponse{}, err
	}

	following, err := s.followedUserRepository.CheckFollowingByIds(ctx, userIds, profile.Id)
	if err != nil {
		return []models.UserResponse{}, err
	}

	res := make([]models.UserResponse, 0)
	for _, user := range users {
		res = append(res, models.UserResponse{
			Id:             user.Id,
			Username:       user.Username,
			Name:           user.Name,
			Email:          user.Email,
			Photo:          user.Photo,
			Header:         user.Header,
			Bio:            user.Bio,
			Location:       user.Location,
			Website:        user.Website,
			Verified:       user.Verified,
			DateOfBirth:    user.DateOfBirth,
			TotalFollowers: user.TotalFollowers,
			TotalFollowing: user.TotalFollowing,
			Following:      following[user.Id],
			CreatedAt:      user.CreatedAt.UnixMilli(),
			UpdatedAt:      user.UpdatedAt.UnixMilli(),
		})
	}

	return res, nil
}

func (s *UserService) GetFollowers(ctx context.Context, userId int64, page models.Page) ([]models.UserResponse, error) {
	followerIds, err := s.followedUserRepository.GetFollowersIds(ctx, userId)
	if err != nil {
		return nil, err
	}
	if len(followerIds) == 0 {
		return []models.UserResponse{}, nil
	}

	users, err := s.userRepository.GetByIds(ctx, followerIds, page)
	if err != nil {
		return []models.UserResponse{}, err
	}

	userIds := make([]int64, 0)
	for _, user := range users {
		userIds = append(userIds, user.Id)
	}

	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return []models.UserResponse{}, err
	}

	following, err := s.followedUserRepository.CheckFollowingByIds(ctx, userIds, profile.Id)
	if err != nil {
		return []models.UserResponse{}, err
	}

	res := make([]models.UserResponse, 0)
	for _, user := range users {
		res = append(res, models.UserResponse{
			Id:             user.Id,
			Username:       user.Username,
			Name:           user.Name,
			Email:          user.Email,
			Photo:          user.Photo,
			Header:         user.Header,
			Bio:            user.Bio,
			Location:       user.Location,
			Website:        user.Website,
			Verified:       user.Verified,
			DateOfBirth:    user.DateOfBirth,
			TotalFollowers: user.TotalFollowers,
			TotalFollowing: user.TotalFollowing,
			Following:      following[user.Id],
			CreatedAt:      user.CreatedAt.UnixMilli(),
			UpdatedAt:      user.UpdatedAt.UnixMilli(),
		})
	}

	return res, nil
}

func (s *UserService) GetFollowing(ctx context.Context, userId int64, page models.Page) ([]models.UserResponse, error) {
	followedIds, err := s.followedUserRepository.GetFollowedIds(ctx, userId)
	if err != nil {
		return nil, err
	}
	if len(followedIds) == 0 {
		return []models.UserResponse{}, nil
	}

	users, err := s.userRepository.GetByIds(ctx, followedIds, page)
	if err != nil {
		return []models.UserResponse{}, err
	}

	userIds := make([]int64, 0)
	for _, user := range users {
		userIds = append(userIds, user.Id)
	}

	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return []models.UserResponse{}, err
	}

	following, err := s.followedUserRepository.CheckFollowingByIds(ctx, userIds, profile.Id)
	if err != nil {
		return []models.UserResponse{}, err
	}

	res := make([]models.UserResponse, 0)
	for _, user := range users {
		res = append(res, models.UserResponse{
			Id:             user.Id,
			Username:       user.Username,
			Name:           user.Name,
			Email:          user.Email,
			Photo:          user.Photo,
			Header:         user.Header,
			Bio:            user.Bio,
			Location:       user.Location,
			Website:        user.Website,
			Verified:       user.Verified,
			DateOfBirth:    user.DateOfBirth,
			TotalFollowers: user.TotalFollowers,
			TotalFollowing: user.TotalFollowing,
			Following:      following[user.Id],
			CreatedAt:      user.CreatedAt.UnixMilli(),
			UpdatedAt:      user.UpdatedAt.UnixMilli(),
		})
	}

	return res, nil
}

func (s *UserService) Follow(ctx context.Context, followedId int64) error {
	return s.followedUserRepository.WithTransaction(ctx, func(ctx context.Context) error {
		profile, err := common.GetCurrentProfileFromContext(ctx)
		if err != nil {
			return err
		}

		follUser := models.FollowedUser{
			FollowedId: followedId,
			FollowerId: profile.Id,
			CreatedAt:  time.Now(),
		}

		err = s.followedUserRepository.Create(ctx, &follUser)
		if err != nil {
			return err
		}

		err = s.userRepository.IncrementTotalFollowers(ctx, followedId)
		if err != nil {
			return err
		}

		err = s.userRepository.IncrementTotalFollowing(ctx, profile.Id)
		if err != nil {
			return err
		}

		return nil
	})
}

func (s *UserService) Unfollow(ctx context.Context, followedId int64) error {
	return s.followedUserRepository.WithTransaction(ctx, func(ctx context.Context) error {
		profile, err := common.GetCurrentProfileFromContext(ctx)
		if err != nil {
			return err
		}

		err = s.followedUserRepository.Delete(ctx, followedId, profile.Id)
		if err != nil {
			return err
		}

		err = s.userRepository.DecrementTotalFollowers(ctx, followedId)
		if err != nil {
			return err
		}

		err = s.userRepository.DecrementTotalFollowing(ctx, profile.Id)
		if err != nil {
			return err
		}

		return nil
	})
}

func (s *UserService) UpdateFcmToken(ctx context.Context, token null.String) error {
	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return err
	}

	err = s.userRepository.UpdateFcmToken(ctx, profile.Id, token)
	if err != nil {
		return err
	}

	return nil
}

func (s *UserService) Update(ctx context.Context, req *models.UpdateUserReq) (models.UserResponse, error) {
	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return models.UserResponse{}, err
	}

	user, err := s.userRepository.GetById(ctx, profile.Id)
	if err != nil {
		return models.UserResponse{}, err
	}

	if req.Username.Valid {
		user.Username = req.Username.String
	}

	if req.Name.Valid {
		user.Name = req.Name.String
	}

	if req.Email.Valid {
		user.Email = req.Email.String
	}

	if req.Password.Valid {
		user.Password = req.Password.String
	}

	if req.Photo.Valid {
		user.Photo = req.Photo
	}

	if req.Photo.Valid {
		user.Photo = req.Photo
	}

	if req.Header.Valid {
		user.Header = req.Header
	}

	if req.Bio.Valid {
		user.Bio = req.Bio
	}

	if req.Location.Valid {
		user.Location = req.Location
	}

	if req.Website.Valid {
		user.Website = req.Website
	}

	if req.DateOfBirth.Valid {
		user.DateOfBirth = req.DateOfBirth
	}

	err = s.userRepository.Update(ctx, &user)
	if err != nil {
		return models.UserResponse{}, err
	}

	res := models.UserResponse{
		Id:             user.Id,
		Username:       user.Username,
		Name:           user.Name,
		Email:          user.Email,
		Photo:          user.Photo,
		Header:         user.Header,
		Bio:            user.Bio,
		Location:       user.Location,
		Website:        user.Website,
		Verified:       user.Verified,
		DateOfBirth:    user.DateOfBirth,
		TotalFollowers: user.TotalFollowers,
		TotalFollowing: user.TotalFollowing,
		Following:      false,
		CreatedAt:      user.CreatedAt.UnixMilli(),
		UpdatedAt:      user.UpdatedAt.UnixMilli(),
	}

	return res, nil
}
