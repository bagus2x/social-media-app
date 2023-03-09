package repository

import (
	"context"
	"database/sql"
	sq "github.com/Masterminds/squirrel"
	"sosmed-go-backend/common/database"
	"sosmed-go-backend/models"
)

type FavoriteFeedRepository struct {
	db *sql.DB
	*database.Tx
}

func NewFavoriteFeedRepository(db *sql.DB) FavoriteFeedRepository {
	return FavoriteFeedRepository{
		db: db,
		Tx: database.NewTx(db),
	}
}

func (r *FavoriteFeedRepository) Create(ctx context.Context, favFeed models.FavoriteFeed) error {
	_, err := psql.
		Insert("favorite_feed").
		Columns("liker_id", "feed_id", "created_at").
		Values(favFeed.LikerId, favFeed.FeedId, favFeed.CreatedAt).
		RunWith(r.Tx.GetRunner(ctx)).
		ExecContext(ctx)
	if err != nil {
		return err
	}

	return nil
}

func (r *FavoriteFeedRepository) Delete(ctx context.Context, likerId, feedId int64) (bool, error) {
	res, err := psql.
		Delete("favorite_feed").
		Where(sq.Eq{"liker_id": likerId, "feed_id": feedId}).
		RunWith(r.Tx.GetRunner(ctx)).
		ExecContext(ctx)
	if err != nil {
		return false, err
	}

	affected, err := res.RowsAffected()
	if err != nil {
		return false, err
	}

	return affected == 1, nil
}

func (r *FavoriteFeedRepository) CheckFavoriteByIds(ctx context.Context, feedIds []int64, likerId int64) (map[int64]bool, error) {
	rows, err := psql.
		Select("feed_id").
		From("favorite_feed").
		Where(sq.Eq{"liker_id": likerId, "feed_id": feedIds}).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		return map[int64]bool{}, err
	}

	favorites := make(map[int64]bool, 0)

	for rows.Next() {
		var feedId int64

		if err := rows.Scan(&feedId); err != nil {
			return map[int64]bool{}, err
		}

		favorites[feedId] = true
	}

	return favorites, nil
}

func (r *FavoriteFeedRepository) CheckFavoriteById(ctx context.Context, feedId int64, likerId int64) (bool, error) {
	favorites, err := r.CheckFavoriteByIds(ctx, []int64{feedId}, likerId)
	return favorites[likerId], err
}
