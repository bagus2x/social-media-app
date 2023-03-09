package repository

import (
	"context"
	"database/sql"
	sq "github.com/Masterminds/squirrel"
	"github.com/pkg/errors"
	"sosmed-go-backend/common/database"
	"sosmed-go-backend/models"
)

type FollowedUserRepository struct {
	db *sql.DB
	*database.Tx
}

func NewFollowedUserRepository(db *sql.DB) FollowedUserRepository {
	return FollowedUserRepository{
		db: db,
		Tx: database.NewTx(db),
	}
}

var psql = sq.StatementBuilder.PlaceholderFormat(sq.Dollar)

func (r *FollowedUserRepository) Create(ctx context.Context, users *models.FollowedUser) error {
	_, err := psql.
		Insert("followed_user").
		Columns("followed_id", "follower_id", "created_at").
		Values(users.FollowedId, users.FollowerId, users.CreatedAt).
		RunWith(r.Tx.GetRunner(ctx)).
		ExecContext(ctx)
	if err != nil {
		return err
	}

	return nil
}

func (r *FollowedUserRepository) GetFollowed(ctx context.Context, followerId int64) ([]models.FollowedUser, error) {
	rows, err := psql.
		Select("followed_id", "follower_id", "created_at").
		From("followed_user").
		Where(sq.Eq{"follower_id": followerId}).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		return []models.FollowedUser{}, err
	}

	var followedUsers []models.FollowedUser
	for rows.Next() {
		var followedUser models.FollowedUser
		err := rows.Scan(
			&followedUser.FollowedId,
			&followedUser.FollowerId,
			&followedUser.CreatedAt,
		)
		if err != nil {
			return []models.FollowedUser{}, err
		}

		followedUsers = append(followedUsers, followedUser)
	}

	return followedUsers, nil
}

func (r *FollowedUserRepository) GetFollowers(ctx context.Context, followedId int64) ([]models.FollowedUser, error) {
	rows, err := psql.
		Select("followed_id", "follower_id", "created_at").
		From("followed_user").
		Where(sq.Eq{"followed_id": followedId}).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		return []models.FollowedUser{}, err
	}

	var followedUsers []models.FollowedUser
	for rows.Next() {
		var followedUser models.FollowedUser
		err := rows.Scan(
			&followedUser.FollowedId,
			&followedUser.FollowerId,
			&followedUser.CreatedAt,
		)
		if err != nil {
			return []models.FollowedUser{}, err
		}

		followedUsers = append(followedUsers, followedUser)
	}

	return followedUsers, nil
}

func (r *FollowedUserRepository) GetFollowersIds(ctx context.Context, followedId int64) ([]int64, error) {
	rows, err := psql.
		Select("follower_id").
		From("followed_user").
		Where(sq.Eq{"followed_id": followedId}).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		return []int64{}, err
	}

	var followedUsers []int64
	for rows.Next() {
		var followedUser int64
		err := rows.Scan(&followedUser)
		if err != nil {
			return []int64{}, err
		}

		followedUsers = append(followedUsers, followedUser)
	}

	return followedUsers, nil
}

func (r *FollowedUserRepository) GetFollowedIds(ctx context.Context, followerId int64) ([]int64, error) {
	rows, err := psql.
		Select("followed_id").
		From("followed_user").
		Where(sq.Eq{"follower_id": followerId}).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		return []int64{}, err
	}

	var followedUsers []int64
	for rows.Next() {
		var followedUser int64
		err := rows.Scan(&followedUser)
		if err != nil {
			return []int64{}, err
		}

		followedUsers = append(followedUsers, followedUser)
	}

	return followedUsers, nil
}

func (r *FollowedUserRepository) Delete(ctx context.Context, followedId, followerId int64) error {
	res, err := psql.
		Delete("followed_user").
		Where(sq.Eq{"followed_id": followedId, "follower_id": followerId}).
		RunWith(r.Tx.GetRunner(ctx)).
		ExecContext(ctx)
	if err != nil {
		return err
	}

	affected, err := res.RowsAffected()
	if err != nil {
		return err
	}

	if affected != 1 {
		return errors.WithMessage(models.ErrNotFound, "Not following")
	}

	return nil
}

func (r *FollowedUserRepository) CheckFollowingByIds(ctx context.Context, followedIds []int64, followerId int64) (map[int64]bool, error) {
	rows, err := psql.
		Select("followed_id").
		From("followed_user").
		Where(sq.Eq{"follower_id": followerId, "followed_id": followedIds}).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		return map[int64]bool{}, err
	}

	following := make(map[int64]bool, 0)

	for rows.Next() {
		var followedId int64

		if err := rows.Scan(&followedId); err != nil {
			return map[int64]bool{}, err
		}

		following[followedId] = true
	}

	return following, nil
}

func (r *FollowedUserRepository) CheckFollowingById(ctx context.Context, followedId int64, followerId int64) (bool, error) {
	favorites, err := r.CheckFollowingByIds(ctx, []int64{followedId}, followerId)
	return favorites[followerId], err
}
