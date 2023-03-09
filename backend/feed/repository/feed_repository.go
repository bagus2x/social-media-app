package repository

import (
	"context"
	"database/sql"
	"encoding/json"
	"github.com/sirupsen/logrus"
	"sosmed-go-backend/common/database"
	"sosmed-go-backend/models"
	"strings"
)
import sq "github.com/Masterminds/squirrel"

type FeedRepository struct {
	db *sql.DB
	*database.Tx
}

func NewFeedRepository(db *sql.DB) FeedRepository {
	return FeedRepository{
		db: db,
		Tx: database.NewTx(db),
	}
}

var psql = sq.StatementBuilder.PlaceholderFormat(sq.Dollar)

func (r *FeedRepository) Create(ctx context.Context, feed *models.Feed) error {
	medias, err := json.Marshal(feed.Medias)
	if err != nil {
		return err
	}

	query := `
		WITH inserted_feed as (
			INSERT INTO 
				feed 
				(id, author_id, description, medias, total_favorites, total_comments, total_reposts, language, created_at, updated_at)
			VALUES (
				DEFAULT, $1, $2, $3, $4, $5, $6, $7, $8, $9
			)
			RETURNING
				id, author_id, description, medias, total_favorites, total_comments, total_reposts, language, created_at, updated_at
		)
		SELECT 
			feed.id, author.id, author.username, author.name, author.photo, feed.description, feed.medias, 
			feed.total_favorites, feed.total_comments, feed.total_reposts, feed.language, feed.created_at, feed.updated_at
		From
			inserted_feed feed
		JOIN 
			"user" author
		ON
			feed.author_id = author.id
	`

	var mediasByte []byte

	return r.Tx.GetRunner(ctx).
		QueryRowContext(
			ctx,
			query,
			feed.Author.Id,
			feed.Description,
			medias,
			feed.TotalFavorites,
			feed.TotalComments,
			feed.TotalReposts,
			feed.Language,
			feed.CreatedAt,
			feed.UpdatedAt,
		).
		Scan(
			&feed.Id,
			&feed.Author.Id,
			&feed.Author.Username,
			&feed.Author.Name,
			&feed.Author.Photo,
			&feed.Description,
			&mediasByte,
			&feed.TotalFavorites,
			&feed.TotalComments,
			&feed.TotalReposts,
			&feed.Language,
			&feed.CreatedAt,
			&feed.UpdatedAt,
		)
}

func (r *FeedRepository) GetById(ctx context.Context, feedId int64) (models.Feed, error) {
	var feed models.Feed
	medias := make([]byte, 0)
	err := psql.
		Select(
			"feed.id", "author.id", "author.username", "author.name", "author.photo", "feed.description",
			"feed.medias", "feed.total_favorites", "feed.total_comments", "feed.total_reposts", "feed.language",
			"feed.created_at", "feed.updated_at",
		).
		From("feed").
		InnerJoin(`"user" author ON feed.author_id = author.id`).
		Where(sq.Eq{"feed.id": feedId}).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryRowContext(ctx).
		Scan(
			&feed.Id,
			&feed.Author.Id,
			&feed.Author.Username,
			&feed.Author.Name,
			&feed.Author.Photo,
			&feed.Description,
			&medias,
			&feed.TotalFavorites,
			&feed.TotalComments,
			&feed.TotalReposts,
			&feed.Language,
			&feed.CreatedAt,
			&feed.UpdatedAt,
		)
	if err != nil {
		logrus.Error(err)
		return models.Feed{}, err
	}

	err = json.Unmarshal(medias, &feed.Medias)
	if err != nil {
		return models.Feed{}, err
	}

	return feed, nil
}

func (r *FeedRepository) GetByAuthorIds(ctx context.Context, authorIds []int64, page models.Page) ([]models.Feed, error) {
	rows, err := psql.
		Select(
			"feed.id, author.id", "author.username", "author.name", "author.photo", "feed.description",
			"feed.medias", "feed.total_favorites", "feed.total_comments", "feed.total_reposts", "feed.language",
			"feed.created_at", "feed.updated_at",
		).
		From("feed").
		InnerJoin(`"user" author ON feed.author_id = author.id`).
		Where(
			sq.And{
				sq.Eq{"feed.author_id": authorIds},
				sq.Lt{"feed.id": page.NextId},
				sq.Gt{"feed.id": page.PreviousId},
			},
		).
		OrderBy("feed.id DESC").
		Limit(page.Size).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		logrus.Error(err)
		return []models.Feed{}, err
	}

	feeds := make([]models.Feed, 0)
	for rows.Next() {
		var feed models.Feed
		medias := make([]byte, 0)
		err := rows.Scan(
			&feed.Id,
			&feed.Author.Id,
			&feed.Author.Username,
			&feed.Author.Name,
			&feed.Author.Photo,
			&feed.Description,
			&medias,
			&feed.TotalFavorites,
			&feed.TotalComments,
			&feed.TotalReposts,
			&feed.Language,
			&feed.CreatedAt,
			&feed.UpdatedAt,
		)
		if err != nil {
			logrus.Error(err)
			return nil, err
		}

		err = json.Unmarshal(medias, &feed.Medias)
		if err != nil {
			logrus.Error(err)
			return nil, err
		}

		feeds = append(feeds, feed)
	}

	return feeds, nil
}

func (r *FeedRepository) SearchFeeds(ctx context.Context, query string, page models.Page) ([]models.Feed, error) {
	rows, err := psql.
		Select(
			"feed.id, author.id", "author.username", "author.name", "author.photo", "feed.description",
			"feed.medias", "feed.total_favorites", "feed.total_comments", "feed.total_reposts", "feed.language",
			"feed.created_at", "feed.updated_at",
		).
		From("feed").
		InnerJoin(`"user" author ON feed.author_id = author.id`).
		Where(
			sq.And{
				sq.Like{"LOWER(feed.description)": "%" + strings.ToLower(query) + "%"},
				sq.Lt{"feed.id": page.NextId},
				sq.Gt{"feed.id": page.PreviousId},
			},
		).
		OrderBy("feed.id DESC").
		Limit(page.Size).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		logrus.Error(err)
		return []models.Feed{}, err
	}

	feeds := make([]models.Feed, 0)
	for rows.Next() {
		var feed models.Feed
		medias := make([]byte, 0)
		err := rows.Scan(
			&feed.Id,
			&feed.Author.Id,
			&feed.Author.Username,
			&feed.Author.Name,
			&feed.Author.Photo,
			&feed.Description,
			&medias,
			&feed.TotalFavorites,
			&feed.TotalComments,
			&feed.TotalReposts,
			&feed.Language,
			&feed.CreatedAt,
			&feed.UpdatedAt,
		)
		if err != nil {
			logrus.Error(err)
			return nil, err
		}

		err = json.Unmarshal(medias, &feed.Medias)
		if err != nil {
			logrus.Error(err)
			return nil, err
		}

		feeds = append(feeds, feed)
	}

	return feeds, nil
}

func (r *FeedRepository) GetByAuthorId(ctx context.Context, authorId int64, page models.Page) ([]models.Feed, error) {
	return r.GetByAuthorIds(ctx, []int64{authorId}, page)
}

func (r *FeedRepository) Update(ctx context.Context, feed *models.Feed) error {
	medias, err := json.Marshal(feed.Medias)
	if err != nil {
		return err
	}

	_, err = psql.Update("feed").
		Set("description", feed.Description).
		Set("medias", medias).
		Set("total_favorites", feed.TotalFavorites).
		Set("total_comments", feed.TotalComments).
		Set("total_reposts", feed.TotalReposts).
		Set("language", feed.Language).
		Set("created_at", feed.CreatedAt).
		Set("updated_at", feed.UpdatedAt).
		Where(sq.Eq{"id": feed.Id}).
		RunWith(r.Tx.GetRunner(ctx)).
		ExecContext(ctx)
	if err != nil {
		logrus.Error(err)
		return err
	}

	return nil
}

func (r *FeedRepository) IncrementTotalFavorites(ctx context.Context, feedId int64) error {
	_, err := psql.Update("feed").
		Set("total_favorites", sq.Expr("total_favorites + 1")).
		Where(sq.Eq{"id": feedId}).
		RunWith(r.Tx.GetRunner(ctx)).
		ExecContext(ctx)
	if err != nil {
		logrus.Error(err)
		return err
	}

	return nil
}

func (r *FeedRepository) DecrementTotalFavorites(ctx context.Context, feedId int64) error {
	_, err := psql.Update("feed").
		Set("total_favorites", sq.Expr("total_favorites - 1")).
		Where(sq.Eq{"id": feedId}).
		RunWith(r.Tx.GetRunner(ctx)).
		ExecContext(ctx)
	if err != nil {
		logrus.Error(err)
		return err
	}

	return nil
}

func (r *FeedRepository) IncrementTotalComments(ctx context.Context, feedId int64) error {
	_, err := psql.Update("feed").
		Set("total_comments", sq.Expr("total_comments + 1")).
		Where(sq.Eq{"id": feedId}).
		RunWith(r.Tx.GetRunner(ctx)).
		ExecContext(ctx)
	if err != nil {
		logrus.Error(err)
		return err
	}

	return nil
}

func (r *FeedRepository) DecrementTotalComments(ctx context.Context, feedId int64) error {
	_, err := psql.Update("feed").
		Set("total_comments", sq.Expr("total_comments - 1")).
		Where(sq.Eq{"id": feedId}).
		RunWith(r.Tx.GetRunner(ctx)).
		ExecContext(ctx)
	if err != nil {
		logrus.Error(err)
		return err
	}

	return nil
}
