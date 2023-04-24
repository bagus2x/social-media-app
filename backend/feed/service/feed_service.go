package service

import (
	"context"
	"database/sql"
	"github.com/pkg/errors"
	"github.com/sirupsen/logrus"
	"gopkg.in/guregu/null.v4"
	"sosmed-go-backend/common"
	feedRepo "sosmed-go-backend/feed/repository"
	"sosmed-go-backend/models"
	notificationSvc "sosmed-go-backend/notification/service"
	tagRepo "sosmed-go-backend/tag/repository"
	userRepo "sosmed-go-backend/user/repository"
	"time"
)

type FeedService struct {
	feedRepository         feedRepo.FeedRepository
	followedUserRepository userRepo.FollowedUserRepository
	favoriteFeedRepository feedRepo.FavoriteFeedRepository
	tagRepository          tagRepo.TagRepository
	notificationService    notificationSvc.NotificationService
}

func NewFeedService(
	feedRepository feedRepo.FeedRepository,
	followedUserRepository userRepo.FollowedUserRepository,
	favoriteFeedRepository feedRepo.FavoriteFeedRepository,
	tagRepository tagRepo.TagRepository,
	notificationRepository notificationSvc.NotificationService,
) FeedService {
	return FeedService{
		feedRepository,
		followedUserRepository,
		favoriteFeedRepository,
		tagRepository,
		notificationRepository,
	}
}

func (f *FeedService) Create(ctx context.Context, req *models.CreateFeedReq) (models.FeedRes, error) {
	var feed models.Feed
	err := f.feedRepository.WithTransaction(ctx, func(ctx context.Context) error {
		profile, err := common.GetCurrentProfileFromContext(ctx)
		if err != nil {
			return err
		}

		feed = models.Feed{
			Author:         profile,
			Description:    req.Description,
			Medias:         req.Medias,
			TotalFavorites: 0,
			TotalComments:  0,
			TotalReposts:   0,
			Language:       req.Language,
			CreatedAt:      time.Now(),
			UpdatedAt:      time.Now(),
		}

		if err = f.feedRepository.Create(ctx, &feed); err != nil {
			return err
		}

		tags := f.tagRepository.ExtractTags(feed.Description, feed.Author.Id, feed.Id, null.Int{}, null.String{})

		err = f.tagRepository.Store(ctx, tags)
		if err != nil {
			return err
		}

		return nil
	})
	if err != nil {
		return models.FeedRes{}, err
	}

	res := models.FeedRes{
		Id:             feed.Id,
		Author:         feed.Author,
		Description:    feed.Description,
		Medias:         feed.Medias,
		TotalFavorites: feed.TotalFavorites,
		TotalComments:  feed.TotalComments,
		TotalReposts:   feed.TotalReposts,
		Language:       feed.Language,
		Favorite:       false,
		CreatedAt:      feed.CreatedAt.UnixMilli(),
		UpdatedAt:      feed.UpdatedAt.UnixMilli(),
	}

	return res, nil
}

func (f *FeedService) GetFeedById(ctx context.Context, feedId int64) (models.FeedRes, error) {
	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return models.FeedRes{}, err
	}

	feed, err := f.feedRepository.GetById(ctx, feedId)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			logrus.Error(err)
			return models.FeedRes{}, errors.WithMessagef(models.ErrNotFound, "Feed not found")
		}

		return models.FeedRes{}, err
	}

	favorites, err := f.favoriteFeedRepository.CheckFavoriteByIds(ctx, []int64{feedId}, profile.Id)
	if err != nil {
		return models.FeedRes{}, err
	}

	res := models.FeedRes{
		Id:             feed.Id,
		Author:         feed.Author,
		Description:    feed.Description,
		Medias:         feed.Medias,
		TotalFavorites: feed.TotalFavorites,
		TotalComments:  feed.TotalComments,
		TotalReposts:   feed.TotalReposts,
		Language:       feed.Language,
		Favorite:       favorites[feedId],
		CreatedAt:      feed.CreatedAt.UnixMilli(),
		UpdatedAt:      feed.UpdatedAt.UnixMilli(),
	}

	return res, nil
}

func (f *FeedService) GetFeedsOfFollowedUsers(ctx context.Context, page models.Page) ([]models.FeedRes, error) {
	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return []models.FeedRes{}, err
	}

	followedUsers, err := f.followedUserRepository.GetFollowed(ctx, profile.Id)
	if err != nil {
		return []models.FeedRes{}, err
	}

	var authorIds []int64
	authorIds = append(authorIds, profile.Id)
	for _, followedUser := range followedUsers {
		authorIds = append(authorIds, followedUser.FollowedId)
	}

	feeds, err := f.feedRepository.GetByAuthorIds(ctx, authorIds, page)
	if err != nil {
		return []models.FeedRes{}, err
	}

	var feedIds []int64
	for _, feed := range feeds {
		feedIds = append(feedIds, feed.Id)
	}

	favorites, err := f.favoriteFeedRepository.CheckFavoriteByIds(ctx, feedIds, profile.Id)
	if err != nil {
		return []models.FeedRes{}, err
	}

	res := make([]models.FeedRes, 0)
	for _, feed := range feeds {
		res = append(res, models.FeedRes{
			Id:             feed.Id,
			Author:         feed.Author,
			Description:    feed.Description,
			Medias:         feed.Medias,
			TotalFavorites: feed.TotalFavorites,
			TotalComments:  feed.TotalComments,
			TotalReposts:   feed.TotalReposts,
			Language:       feed.Language,
			Favorite:       favorites[feed.Id],
			CreatedAt:      feed.CreatedAt.UnixMilli(),
			UpdatedAt:      feed.UpdatedAt.UnixMilli(),
		})
	}

	return res, nil
}

func (f *FeedService) SearchFeeds(ctx context.Context, query string, page models.Page) ([]models.FeedRes, error) {
	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return []models.FeedRes{}, err
	}

	feeds, err := f.feedRepository.SearchFeeds(ctx, query, page)
	if err != nil {
		return []models.FeedRes{}, err
	}

	var feedIds []int64
	for _, feed := range feeds {
		feedIds = append(feedIds, feed.Id)
	}

	favorites, err := f.favoriteFeedRepository.CheckFavoriteByIds(ctx, feedIds, profile.Id)
	if err != nil {
		return []models.FeedRes{}, err
	}

	res := make([]models.FeedRes, 0)
	for _, feed := range feeds {
		res = append(res, models.FeedRes{
			Id:             feed.Id,
			Author:         feed.Author,
			Description:    feed.Description,
			Medias:         feed.Medias,
			TotalFavorites: feed.TotalFavorites,
			TotalComments:  feed.TotalComments,
			TotalReposts:   feed.TotalReposts,
			Language:       feed.Language,
			Favorite:       favorites[feed.Id],
			CreatedAt:      feed.CreatedAt.UnixMilli(),
			UpdatedAt:      feed.UpdatedAt.UnixMilli(),
		})
	}

	return res, nil
}

func (f *FeedService) GetFeedsByAuthor(ctx context.Context, authorId int64, page models.Page) ([]models.FeedRes, error) {
	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return []models.FeedRes{}, err
	}

	feeds, err := f.feedRepository.GetByAuthorId(ctx, authorId, page)
	if err != nil {
		return []models.FeedRes{}, err
	}

	var feedIds []int64
	for _, feed := range feeds {
		feedIds = append(feedIds, feed.Id)
	}

	favorites, err := f.favoriteFeedRepository.CheckFavoriteByIds(ctx, feedIds, profile.Id)
	if err != nil {
		return []models.FeedRes{}, err
	}

	res := make([]models.FeedRes, 0)
	for _, feed := range feeds {
		res = append(res, models.FeedRes{
			Id:             feed.Id,
			Author:         feed.Author,
			Description:    feed.Description,
			Medias:         feed.Medias,
			TotalFavorites: feed.TotalFavorites,
			TotalComments:  feed.TotalComments,
			TotalReposts:   feed.TotalReposts,
			Language:       feed.Language,
			Favorite:       favorites[feed.Id],
			CreatedAt:      feed.CreatedAt.UnixMilli(),
			UpdatedAt:      feed.UpdatedAt.UnixMilli(),
		})
	}

	return res, nil
}

func (f *FeedService) GetFeedsByProfile(ctx context.Context, page models.Page) ([]models.FeedRes, error) {
	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return []models.FeedRes{}, err
	}

	feeds, err := f.feedRepository.GetByAuthorId(ctx, profile.Id, page)
	if err != nil {
		return []models.FeedRes{}, err
	}

	var feedIds []int64

	favorites, err := f.favoriteFeedRepository.CheckFavoriteByIds(ctx, feedIds, profile.Id)
	if err != nil {
		return []models.FeedRes{}, err
	}

	res := make([]models.FeedRes, 0)
	for _, feed := range feeds {
		res = append(res, models.FeedRes{
			Id:             feed.Id,
			Author:         feed.Author,
			Description:    feed.Description,
			Medias:         feed.Medias,
			TotalFavorites: feed.TotalFavorites,
			TotalComments:  feed.TotalComments,
			TotalReposts:   feed.TotalReposts,
			Language:       feed.Language,
			Favorite:       favorites[feed.Id],
			CreatedAt:      feed.CreatedAt.UnixMilli(),
			UpdatedAt:      feed.UpdatedAt.UnixMilli(),
		})
	}

	return res, nil
}

func (f *FeedService) Favorite(ctx context.Context, feedId int64) error {
	return f.feedRepository.Tx.WithTransaction(ctx, func(txCtx context.Context) error {
		profile, err := common.GetCurrentProfileFromContext(txCtx)
		if err != nil {
			return err
		}

		favFeed := models.FavoriteFeed{
			LikerId:   profile.Id,
			FeedId:    feedId,
			CreatedAt: time.Now(),
		}

		err = f.favoriteFeedRepository.Create(txCtx, favFeed)
		if err != nil {
			return err
		}

		err = f.feedRepository.IncrementTotalFavorites(txCtx, feedId)
		if err != nil {
			return err
		}

		err = f.notificationService.CreateNotificationWhenFeedIsLiked(ctx, &profile, feedId)
		if err != nil {
			return err
		}

		return nil
	})
}

func (f *FeedService) Unfavorite(ctx context.Context, feedId int64) error {
	return f.feedRepository.Tx.WithTransaction(ctx, func(txCtx context.Context) error {
		profile, err := common.GetCurrentProfileFromContext(txCtx)
		if err != nil {
			return err
		}

		isDeleted, err := f.favoriteFeedRepository.Delete(txCtx, profile.Id, feedId)
		if err != nil {
			return err
		}

		if isDeleted {
			err = f.feedRepository.DecrementTotalFavorites(txCtx, feedId)
			if err != nil {
				return err
			}
		}

		return nil
	})
}
