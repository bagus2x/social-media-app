package repository

import (
	"context"
	"database/sql"
	sq "github.com/Masterminds/squirrel"
	"gopkg.in/guregu/null.v4"
	"regexp"
	"sosmed-go-backend/common/database"
	"sosmed-go-backend/models"
	"strings"
	"time"
)

type TagRepository struct {
	db *sql.DB
	*database.Tx
	psql sq.StatementBuilderType
}

func NewTagRepository(db *sql.DB) TagRepository {
	return TagRepository{
		db:   db,
		Tx:   database.NewTx(db),
		psql: sq.StatementBuilder.PlaceholderFormat(sq.Dollar),
	}
}

func (h *TagRepository) ExtractTags(text string, userId, feedId int64, commentId null.Int, country null.String) []models.Tag {
	rx := regexp.MustCompile(`(#\w+)`)
	matches := rx.FindAllStringSubmatch(text, -1)
	res := make([]models.Tag, 0)

	set := make(map[string]bool, 0)

	for _, hashtag := range matches {
		if len(hashtag) > 0 {
			name := strings.ToLower(hashtag[0])
			if !set[name] {
				res = append(res, models.Tag{
					UserId:    userId,
					FeedId:    feedId,
					CommentId: commentId,
					Name:      name,
					Type:      "hashtag",
					Country:   country,
					CreatedAt: time.Now(),
				})
			}

			set[name] = true
		}
	}

	return res
}

func (h *TagRepository) Store(ctx context.Context, tags []models.Tag) error {
	for _, tag := range tags {
		_, err := h.psql.
			Insert("tag").
			Columns("user_id", "feed_id", "comment_id", "name", "type", "country", "created_at").
			Values(tag.UserId, tag.FeedId, tag.CommentId, tag.Name, tag.Type, tag.Country, tag.CreatedAt).
			RunWith(h.GetRunner(ctx)).
			ExecContext(ctx)
		if err != nil {
			return err
		}
	}

	return nil
}

func (h *TagRepository) GetTrendingIn(ctx context.Context, duration time.Duration) ([]models.TrendingTags, error) {
	rows, err := h.psql.
		Select("name", "type", "country", "COUNT(*) as total").
		From("tag").
		GroupBy("name", "type", "country").
		Where(
			sq.And{
				sq.Gt{"created_at": time.UnixMilli(time.Now().UnixMilli() - duration.Milliseconds())},
				sq.Lt{"created_at": time.Now()},
			},
		).
		OrderBy("total DESC").
		Limit(5).
		RunWith(h.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		return []models.TrendingTags{}, err
	}

	trendingTags := make([]models.TrendingTags, 0)

	for rows.Next() {
		var trending models.TrendingTags
		err := rows.Scan(&trending.Name, &trending.Type, &trending.Country, &trending.Count)
		if err != nil {
			return []models.TrendingTags{}, err
		}

		trendingTags = append(trendingTags, trending)
	}

	return trendingTags, nil
}
