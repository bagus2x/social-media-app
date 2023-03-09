package service

import (
	"context"
	"database/sql"
	"github.com/pkg/errors"
	"gopkg.in/guregu/null.v4"
	commentRepo "sosmed-go-backend/comment/repository"
	"sosmed-go-backend/common"
	feedRepo "sosmed-go-backend/feed/repository"
	"sosmed-go-backend/models"
	"time"
)

type CommentService struct {
	commentRepository commentRepo.CommentRepository
	feedRepository    feedRepo.FeedRepository
}

func NewCommentService(commentRepository commentRepo.CommentRepository, feedRepository feedRepo.FeedRepository) CommentService {
	return CommentService{commentRepository, feedRepository}
}

func (s *CommentService) Create(ctx context.Context, req *models.CreateCommentReq) (models.CommentRes, error) {
	var comment models.Comment
	err := s.commentRepository.Tx.WithTransaction(ctx, func(c context.Context) error {
		profile, err := common.GetCurrentProfileFromContext(ctx)
		if err != nil {
			return err
		}
		comment = models.Comment{
			FeedId:      req.FeedId,
			ParentId:    null.NewInt(req.ParentId, req.ParentId != 0),
			Author:      profile,
			Description: req.Description,
			Medias:      req.Medias,
			CreatedAt:   time.Now(),
		}
		err = s.commentRepository.Create(ctx, &comment)
		if err != nil {
			return err
		}

		err = s.feedRepository.IncrementTotalComments(ctx, req.FeedId)
		if err != nil {
			return err
		}

		if comment.ParentId.Valid {
			err = s.commentRepository.IncrementTotalReplies(ctx, req.ParentId)
			if err != nil {
				return err
			}
		}

		return nil
	})
	if err != nil {
		return models.CommentRes{}, err
	}

	return models.CommentRes{
		Id:             comment.Id,
		FeedId:         comment.FeedId,
		ParentId:       comment.ParentId,
		Path:           comment.Path,
		Author:         comment.Author,
		Description:    comment.Description,
		Medias:         comment.Medias,
		TotalFavorites: comment.TotalFavorites,
		TotalReplies:   comment.TotalReplies,
		CreatedAt:      comment.CreatedAt.UnixMilli(),
	}, nil
}

func (s *CommentService) GetCommentById(ctx context.Context, commentId int64) (models.CommentRes, error) {
	comment, err := s.commentRepository.GetCommentById(ctx, commentId)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return models.CommentRes{}, errors.WithMessagef(models.ErrNotFound, "Comment not found")
		}
		return models.CommentRes{}, err
	}

	return models.CommentRes{
		Id:             comment.Id,
		FeedId:         comment.FeedId,
		ParentId:       comment.ParentId,
		Path:           comment.Path,
		Author:         comment.Author,
		Description:    comment.Description,
		Medias:         comment.Medias,
		TotalFavorites: comment.TotalFavorites,
		TotalReplies:   comment.TotalReplies,
		CreatedAt:      comment.CreatedAt.UnixMilli(),
	}, nil
}

func (s *CommentService) GetRootComment(ctx context.Context, feedId int64, page models.Page) ([]models.CommentRes, error) {
	comments, err := s.commentRepository.GetRootComments(ctx, feedId, page)
	if err != nil {
		return nil, err
	}

	res := make([]models.CommentRes, 0)
	for _, comment := range comments {
		res = append(res, models.CommentRes{
			Id:             comment.Id,
			FeedId:         comment.FeedId,
			ParentId:       comment.ParentId,
			Path:           comment.Path,
			Author:         comment.Author,
			Description:    comment.Description,
			Medias:         comment.Medias,
			TotalFavorites: comment.TotalFavorites,
			TotalReplies:   comment.TotalReplies,
			CreatedAt:      comment.CreatedAt.UnixMilli(),
		})
	}

	return res, nil
}

func (s *CommentService) GetChildComments(ctx context.Context, parentId int64, page models.Page) ([]models.CommentRes, error) {

	comments, err := s.commentRepository.GetChildComments(ctx, parentId, page)
	if err != nil {
		return nil, err
	}

	res := make([]models.CommentRes, 0)
	for _, comment := range comments {
		res = append(res, models.CommentRes{
			Id:             comment.Id,
			FeedId:         comment.FeedId,
			ParentId:       comment.ParentId,
			Path:           comment.Path,
			Author:         comment.Author,
			Description:    comment.Description,
			Medias:         comment.Medias,
			TotalFavorites: comment.TotalFavorites,
			TotalReplies:   comment.TotalReplies,
			CreatedAt:      comment.CreatedAt.UnixMilli(),
		})
	}

	return res, nil
}
