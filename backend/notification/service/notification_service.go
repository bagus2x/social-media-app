package service

import (
	"context"
	"database/sql"
	"github.com/pkg/errors"
	"github.com/sirupsen/logrus"
	"gopkg.in/guregu/null.v4"
	commentRepo "sosmed-go-backend/comment/repository"
	"sosmed-go-backend/common"
	feedRepo "sosmed-go-backend/feed/repository"
	"sosmed-go-backend/models"
	notificationRepo "sosmed-go-backend/notification/repository"
	"time"
)

type NotificationService struct {
	notificationRepository notificationRepo.NotificationRepository
	feedRepository         feedRepo.FeedRepository
	commentRepository      commentRepo.CommentRepository
}

func NewNotificationService(
	notificationRepository notificationRepo.NotificationRepository,
	feedRepository feedRepo.FeedRepository,
	commentRepository commentRepo.CommentRepository,
) NotificationService {
	return NotificationService{
		notificationRepository: notificationRepository,
		feedRepository:         feedRepository,
		commentRepository:      commentRepository,
	}
}

func (n *NotificationService) CreateNotificationWhenFeedIsLiked(ctx context.Context, issuer *models.Profile, feedId int64) error {
	return n.notificationRepository.WithTransaction(ctx, func(ctx context.Context) error {
		feed, err := n.feedRepository.GetById(ctx, feedId)
		if err != nil {
			logrus.Error(err)
			return err
		}

		if feed.Author.Id == issuer.Id {
			return nil
		}

		notification := models.Notification{
			OwnerId:   feed.Author.Id,
			Issuer:    *issuer,
			FeedId:    null.NewInt(feedId, feedId != 0),
			CreatedAt: time.Now(),
			Type:      models.FeedLiked,
		}

		err = n.notificationRepository.Create(ctx, &notification)
		if err != nil {
			logrus.Error(err)
			return err
		}

		return nil
	})
}

func (n *NotificationService) CreateNotificationWhenFeedIsCommented(ctx context.Context, issuer *models.Profile, commentId int64) error {
	return n.notificationRepository.WithTransaction(ctx, func(ctx context.Context) error {
		comment, err := n.commentRepository.GetCommentById(ctx, commentId)
		if err != nil {
			logrus.Error(err)
			return err
		}

		feed, err := n.feedRepository.GetById(ctx, comment.FeedId)
		if err != nil {
			logrus.Error(err)
			return err
		}

		if feed.Author.Id == issuer.Id {
			return nil
		}

		notification := models.Notification{
			OwnerId:   feed.Author.Id,
			Issuer:    *issuer,
			CommentId: null.NewInt(commentId, commentId != 0),
			CreatedAt: time.Now(),
			Type:      models.FeedCommented,
		}

		err = n.notificationRepository.Create(ctx, &notification)
		if err != nil {
			logrus.Error(err)
			return err
		}

		return nil
	})
}

func (n *NotificationService) CreateNotificationWhenCommentIsReplied(ctx context.Context, issuer *models.Profile, commentId int64) error {
	return n.notificationRepository.WithTransaction(ctx, func(ctx context.Context) error {
		comment, err := n.commentRepository.GetCommentById(ctx, commentId)
		if err != nil {
			logrus.Error(err)
			return err
		}

		// Comment Replied
		repliedComment, err := n.commentRepository.GetCommentById(ctx, comment.ParentId.Int64)
		if err != nil {
			logrus.Error(err)
			return err
		}

		err = n.CreateNotificationWhenFeedIsCommented(ctx, issuer, commentId)
		if err != nil {
			logrus.Error(err)
			return err
		}

		if repliedComment.Author.Id != issuer.Id {
			notification := models.Notification{
				OwnerId:   repliedComment.Author.Id,
				Issuer:    *issuer,
				CommentId: null.NewInt(commentId, commentId != 0),
				CreatedAt: time.Now(),
				Type:      models.CommentReplied,
			}

			err = n.notificationRepository.Create(ctx, &notification)
			if err != nil {
				logrus.Error(err)
				return err
			}
		}

		return nil
	})
}

func (n *NotificationService) CreateNotificationWhenUserStartedFollowing(ctx context.Context, issuer *models.Profile, userId int64) error {
	return n.notificationRepository.WithTransaction(ctx, func(ctx context.Context) error {
		notification := models.Notification{
			OwnerId:   userId,
			Issuer:    *issuer,
			Type:      models.UserFollowing,
			CreatedAt: time.Now(),
		}

		err := n.notificationRepository.Create(ctx, &notification)
		if err != nil {
			logrus.Error(err)
			return err
		}

		return nil
	})
}

func (n *NotificationService) Get(ctx context.Context, page models.Page) ([]models.NotificationRes, error) {
	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return nil, err
	}

	notifications, err := n.notificationRepository.GetByUserId(ctx, profile.Id, page)
	if err != nil {
		return nil, err
	}

	res := make([]models.NotificationRes, 0)
	notificationIds := make([]int64, 0)

	for _, notification := range notifications {
		notificationRes := models.NotificationRes{
			Id:        notification.Id,
			Type:      notification.Type,
			Seen:      notification.Seen,
			CreatedAt: notification.CreatedAt.UnixMilli(),
		}
		notificationIds = append(notificationIds, notification.Id)

		if notification.Type == models.FeedLiked {
			feed, err := n.feedRepository.GetById(ctx, notification.FeedId.Int64)
			if err != nil && !errors.Is(err, sql.ErrNoRows) {
				logrus.Error(err)
				return nil, err
			}

			notificationRes.Issuer = &models.Profile{
				Id:       notification.Id,
				Name:     notification.Issuer.Name,
				Username: notification.Issuer.Username,
				Photo:    notification.Issuer.Photo,
			}
			notificationRes.DataId = notification.FeedId
			notificationRes.Description = feed.Description
			notificationRes.Medias = feed.Medias

			res = append(res, notificationRes)
		}
		if notification.Type == models.FeedCommented {
			comment, err := n.commentRepository.GetCommentById(ctx, notification.CommentId.Int64)
			if err != nil && !errors.Is(err, sql.ErrNoRows) {
				logrus.Error(err)
				return nil, err
			}

			notificationRes.Issuer = &models.Profile{
				Id:       notification.Id,
				Name:     notification.Issuer.Name,
				Username: notification.Issuer.Username,
				Photo:    notification.Issuer.Photo,
			}
			notificationRes.DataId = notification.CommentId
			notificationRes.Description = comment.Description
			notificationRes.Medias = comment.Medias

			res = append(res, notificationRes)
		}
		if notification.Type == models.CommentReplied {

			comment, err := n.commentRepository.GetCommentById(ctx, notification.CommentId.Int64)
			if err != nil && !errors.Is(err, sql.ErrNoRows) {
				logrus.Error(err)
				return nil, err
			}

			notificationRes.Issuer = &models.Profile{
				Id:       notification.Id,
				Name:     notification.Issuer.Name,
				Username: notification.Issuer.Username,
				Photo:    notification.Issuer.Photo,
			}
			notificationRes.DataId = notification.CommentId
			notificationRes.Description = comment.Description
			notificationRes.Medias = comment.Medias

			res = append(res, notificationRes)
		}
		if notification.Type == models.UserFollowing {
			notificationRes.Issuer = &models.Profile{
				Id:       notification.Id,
				Name:     notification.Issuer.Name,
				Username: notification.Issuer.Username,
				Photo:    notification.Issuer.Photo,
			}
			notificationRes.Medias = []models.Media{}

			res = append(res, notificationRes)
		}
	}

	err = n.notificationRepository.MarkAsSeen(ctx, notificationIds)
	if err != nil {
		return nil, err
	}

	return res, nil
}
