package repository

import (
	"context"
	"database/sql"
	"encoding/json"
	sq "github.com/Masterminds/squirrel"
	"github.com/sirupsen/logrus"
	"sosmed-go-backend/common/database"
	"sosmed-go-backend/models"
)

type CommentRepository struct {
	db *sql.DB
	Tx *database.Tx
}

func NewCommentRepository(db *sql.DB) CommentRepository {
	return CommentRepository{
		db: db,
		Tx: database.NewTx(db),
	}
}

var psql = sq.StatementBuilder.PlaceholderFormat(sq.Dollar)

func (r *CommentRepository) Create(ctx context.Context, comment *models.Comment) error {
	query := `
		WITH inserted_comment AS (
			INSERT INTO
				comment
				(feed_id, parent_id, path, author_id, description, medias, total_favorites, total_replies, created_at)
			VALUES
				($1, $2, create_path($3), $4, $5, $6, $7, $8, $9)
			RETURNING
				id, feed_id, parent_id, path, author_id, description, medias, total_favorites, total_replies, created_at
		)
		SELECT 
			comment.id, comment.feed_id, comment.parent_id, comment.path, author.id, author.username, 
			author.name, author.photo, comment.description, comment.medias, comment.total_favorites, 
			comment.total_replies, comment.created_at
		FROM
			inserted_comment comment
		JOIN
			"user" author
		ON
			comment.author_id = author.id
	`
	medias, err := json.Marshal(comment.Medias)
	if err != nil {
		return err
	}

	err = r.Tx.GetRunner(ctx).QueryRowContext(
		ctx,
		query,
		comment.FeedId,
		comment.ParentId,
		comment.ParentId,
		comment.Author.Id,
		comment.Description,
		medias,
		comment.TotalFavorites,
		comment.TotalReplies,
		comment.CreatedAt,
	).Scan(
		&comment.Id,
		&comment.FeedId,
		&comment.ParentId,
		&comment.Path,
		&comment.Author.Id,
		&comment.Author.Username,
		&comment.Author.Name,
		&comment.Author.Photo,
		&comment.Description,
		&medias,
		&comment.TotalFavorites,
		&comment.TotalReplies,
		&comment.CreatedAt,
	)
	if err != nil {
		return err
	}

	return nil
}

func (r *CommentRepository) GetCommentById(ctx context.Context, commentId int64) (models.Comment, error) {
	var comment models.Comment
	medias := make([]byte, 0)
	err := psql.
		Select(
			"comment.id", "comment.feed_id", "comment.parent_id", "comment.path", "author.id", "author.username",
			"author.name", "author.photo", "comment.description", "comment.medias", "comment.total_favorites",
			"comment.total_replies", "comment.created_at",
		).
		From("comment").
		Join(`"user" author ON comment.author_id = author.id`).
		Where(sq.Eq{"comment.id": commentId}).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryRowContext(ctx).
		Scan(
			&comment.Id,
			&comment.FeedId,
			&comment.ParentId,
			&comment.Path,
			&comment.Author.Id,
			&comment.Author.Username,
			&comment.Author.Name,
			&comment.Author.Photo,
			&comment.Description,
			&medias,
			&comment.TotalFavorites,
			&comment.TotalReplies,
			&comment.CreatedAt,
		)
	if err != nil {
		return models.Comment{}, err
	}

	err = json.Unmarshal(medias, &comment.Medias)
	if err != nil {
		return models.Comment{}, err
	}

	return comment, nil
}

func (r *CommentRepository) GetRootComments(ctx context.Context, feedId int64, page models.Page) ([]models.Comment, error) {
	rows, err := psql.
		Select(
			"comment.id", "comment.feed_id", "comment.parent_id", "comment.path", "author.id", "author.username",
			"author.name", "author.photo", "comment.description", "comment.medias", "comment.total_favorites",
			"comment.total_replies", "comment.created_at",
		).
		From("comment").
		Join(`"user" author ON comment.author_id = author.id`).
		Where(
			sq.And{
				sq.Eq{
					"comment.feed_id":   feedId,
					"comment.parent_id": nil,
				},
				sq.Lt{"comment.id": page.NextId},
				sq.Gt{"comment.id": page.PreviousId},
			},
		).
		OrderBy("comment.id DESC").
		Limit(page.Size).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		return nil, err
	}

	comments := make([]models.Comment, 0)

	for rows.Next() {
		var comment models.Comment
		medias := make([]byte, 0)
		err := rows.Scan(
			&comment.Id,
			&comment.FeedId,
			&comment.ParentId,
			&comment.Path,
			&comment.Author.Id,
			&comment.Author.Username,
			&comment.Author.Name,
			&comment.Author.Photo,
			&comment.Description,
			&medias,
			&comment.TotalFavorites,
			&comment.TotalReplies,
			&comment.CreatedAt,
		)
		if err != nil {
			return nil, err
		}

		err = json.Unmarshal(medias, &comment.Medias)
		if err != nil {
			return nil, err
		}

		comments = append(comments, comment)
	}

	return comments, nil
}

func (r *CommentRepository) GetChildComments(ctx context.Context, parentId int64, page models.Page) ([]models.Comment, error) {
	rows, err := psql.
		Select(
			"comment.id", "comment.feed_id", "comment.parent_id", "comment.path", "author.id", "author.username",
			"author.name", "author.photo", "comment.description", "comment.medias", "comment.total_favorites",
			"comment.total_replies", "comment.created_at",
		).
		From("comment").
		Join(`"user" author ON comment.author_id = author.id`).
		Where(
			sq.And{
				sq.Eq{"comment.parent_id": parentId},
				sq.Lt{"comment.id": page.NextId},
				sq.Gt{"comment.id": page.PreviousId},
			},
		).
		OrderBy("comment.id DESC").
		Limit(page.Size).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		return nil, err
	}

	comments := make([]models.Comment, 0)

	for rows.Next() {
		var comment models.Comment
		medias := make([]byte, 0)
		err := rows.Scan(
			&comment.Id,
			&comment.FeedId,
			&comment.ParentId,
			&comment.Path,
			&comment.Author.Id,
			&comment.Author.Username,
			&comment.Author.Name,
			&comment.Author.Photo,
			&comment.Description,
			&medias,
			&comment.TotalFavorites,
			&comment.TotalReplies,
			&comment.CreatedAt,
		)
		if err != nil {
			return nil, err
		}

		err = json.Unmarshal(medias, &comment.Medias)
		if err != nil {
			return nil, err
		}

		comments = append(comments, comment)
	}

	return comments, nil
}

func (r *CommentRepository) IncrementTotalReplies(ctx context.Context, commentId int64) error {
	_, err := psql.Update("comment").
		Set("total_replies", sq.Expr("total_replies + 1")).
		Where(sq.Eq{"id": commentId}).
		RunWith(r.Tx.GetRunner(ctx)).
		ExecContext(ctx)
	if err != nil {
		logrus.Error(err)
		return err
	}

	return nil
}

func (r *CommentRepository) DecrementTotalReplies(ctx context.Context, commentId int64) error {
	_, err := psql.Update("comment").
		Set("total_replies", sq.Expr("total_comments - 1")).
		Where(sq.Eq{"id": commentId}).
		RunWith(r.Tx.GetRunner(ctx)).
		ExecContext(ctx)
	if err != nil {
		logrus.Error(err)
		return err
	}

	return nil
}
