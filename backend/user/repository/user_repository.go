package repository

import (
	"context"
	"database/sql"
	sq "github.com/Masterminds/squirrel"
	"github.com/pkg/errors"
	"gopkg.in/guregu/null.v4"
	"sosmed-go-backend/common/database"
	"sosmed-go-backend/models"
	"strings"
)

type UserRepository struct {
	db *sql.DB
	*database.Tx
}

func NewUserRepository(db *sql.DB) UserRepository {
	return UserRepository{
		db: db,
		Tx: database.NewTx(db),
	}
}

func (r *UserRepository) Create(ctx context.Context, user *models.User) error {
	err := psql.
		Insert(`"user"`).
		Columns(
			"username", "name", "email", "password", "photo", "header", "bio", "location", "website", "verified",
			"date_of_birth", "total_followers", "total_following", "fcm_token", "created_at", "updated_at",
		).
		Values(
			user.Username, user.Name, user.Email, user.Password, user.Photo, user.Header, user.Bio, user.Location, user.Website,
			user.Verified, user.DateOfBirth, user.TotalFollowers, user.TotalFollowing, user.FcmToken, user.CreatedAt, user.UpdatedAt,
		).
		Suffix("RETURNING id").
		RunWith(r.db).
		QueryRowContext(ctx).
		Scan(&user.Id)
	if err != nil {
		return err
	}

	return nil
}

func (r *UserRepository) GetById(ctx context.Context, userId int64) (models.User, error) {
	var user models.User
	err := psql.
		Select(
			"id", "username", "name", "email", "password", "photo", "header", "bio", "location", "website", "verified",
			"date_of_birth", "total_followers", "total_following", "fcm_token", "created_at", "updated_at",
		).
		From(`"user"`).
		Where(sq.Eq{"id": userId}).
		RunWith(r.db).
		QueryRowContext(ctx).
		Scan(
			&user.Id, &user.Username, &user.Name, &user.Email, &user.Password, &user.Photo, &user.Header, &user.Bio, &user.Location, &user.Website,
			&user.Verified, &user.DateOfBirth, &user.TotalFollowers, &user.TotalFollowing, &user.FcmToken, &user.CreatedAt, &user.UpdatedAt,
		)

	if err != nil {
		return models.User{}, err
	}

	return user, nil
}

func (r *UserRepository) Search(ctx context.Context, query string, page models.Page) ([]models.User, error) {
	rows, err := psql.
		Select(
			"id", "username", "name", "email", "password", "photo", "header", "bio", "location", "website", "verified",
			"date_of_birth", "total_followers", "total_following", "fcm_token", "created_at", "updated_at",
		).
		From(`"user"`).
		Where(
			sq.And{
				sq.Or{
					sq.Like{"LOWER(name)": "%" + strings.ToLower(query) + "%"},
					sq.Like{"LOWER(username)": "%" + strings.ToLower(query) + "%"},
				},
				sq.Lt{"id": page.NextId},
				sq.Gt{"id": page.PreviousId},
			},
		).
		OrderBy("id DESC").
		RunWith(r.db).
		QueryContext(ctx)

	users := make([]models.User, 0)

	for rows.Next() {
		var user models.User
		err := rows.Scan(
			&user.Id, &user.Username, &user.Name, &user.Email, &user.Password, &user.Photo, &user.Header, &user.Bio, &user.Location, &user.Website,
			&user.Verified, &user.DateOfBirth, &user.TotalFollowers, &user.TotalFollowing, &user.FcmToken, &user.CreatedAt, &user.UpdatedAt,
		)
		if err != nil {
			return []models.User{}, err
		}

		users = append(users, user)
	}

	if err != nil {
		return []models.User{}, err
	}

	return users, nil
}

func (r *UserRepository) GetByIds(ctx context.Context, ids []int64, page models.Page) ([]models.User, error) {
	rows, err := psql.
		Select(
			"id", "username", "name", "email", "password", "photo", "header", "bio", "location", "website", "verified",
			"date_of_birth", "total_followers", "total_following", "fcm_token", "created_at", "updated_at",
		).
		From(`"user"`).
		Where(
			sq.And{
				sq.Eq{"id": ids},
				sq.Lt{"id": page.NextId},
				sq.Gt{"id": page.PreviousId},
			},
		).
		OrderBy("id DESC").
		RunWith(r.db).
		QueryContext(ctx)

	users := make([]models.User, 0)

	for rows.Next() {
		var user models.User
		err := rows.Scan(
			&user.Id, &user.Username, &user.Name, &user.Email, &user.Password, &user.Photo, &user.Header, &user.Bio, &user.Location, &user.Website,
			&user.Verified, &user.DateOfBirth, &user.TotalFollowers, &user.TotalFollowing, &user.FcmToken, &user.CreatedAt, &user.UpdatedAt,
		)
		if err != nil {
			return []models.User{}, err
		}

		users = append(users, user)
	}

	if err != nil {
		return []models.User{}, err
	}

	return users, nil
}

func (r *UserRepository) GetByUsernameOrEmail(ctx context.Context, usernameOrEmail string) (models.User, error) {
	var user models.User
	err := psql.
		Select(
			"id", "username", "name", "email", "password", "photo", "header", "bio", "location", "website", "verified",
			"date_of_birth", "total_followers", "total_following", "fcm_token", "created_at", "updated_at",
		).
		From(`"user"`).
		Where(sq.Or{sq.Eq{"email": usernameOrEmail}, sq.Eq{"username": usernameOrEmail}}).
		RunWith(r.db).
		QueryRowContext(ctx).
		Scan(
			&user.Id, &user.Username, &user.Name, &user.Email, &user.Password, &user.Photo, &user.Header, &user.Bio, &user.Location, &user.Website,
			&user.Verified, &user.DateOfBirth, &user.TotalFollowers, &user.TotalFollowing, &user.FcmToken, &user.CreatedAt, &user.UpdatedAt,
		)

	if err != nil {
		return models.User{}, err
	}

	return user, nil
}

func (r *UserRepository) Update(ctx context.Context, user *models.User) error {
	_, err := psql.
		Update(`"user"`).
		Set("username", user.Username).
		Set("name", user.Name).
		Set("email", user.Email).
		Set("password", user.Password).
		Set("photo", user.Photo).
		Set("header", user.Header).
		Set("bio", user.Bio).
		Set("location", user.Location).
		Set("website", user.Website).
		Set("verified", user.Verified).
		Set("date_of_birth", user.DateOfBirth).
		Set("total_followers", user.TotalFollowers).
		Set("total_following", user.TotalFollowing).
		Set("fcm_token", user.FcmToken).
		Set("created_at", user.CreatedAt).
		Set("updated_at", user.UpdatedAt).
		Where(sq.Eq{"id": user.Id}).
		RunWith(r.db).
		ExecContext(ctx)
	if err != nil {
		return err
	}

	return nil
}

func (r *UserRepository) UpdateFcmToken(ctx context.Context, userId int64, token null.String) error {
	affected, err := psql.
		Update(`"user"`).
		Set("fcm_token", token).
		Where(sq.Eq{"id": userId}).
		RunWith(r.db).
		ExecContext(ctx)
	if err != nil {
		return err
	}

	rowsAffected, err := affected.RowsAffected()
	if err != nil {
		return err
	}

	if rowsAffected != 1 {
		return errors.WithMessage(models.ErrNotFound, "User not found")
	}

	return nil
}

func (r *UserRepository) IncrementTotalFollowers(ctx context.Context, userId int64) error {
	res, err := psql.
		Update(`"user"`).
		Set("total_followers", sq.Expr("total_followers + 1")).
		Where(sq.Eq{"id": userId}).
		RunWith(r.db).
		ExecContext(ctx)
	if err != nil {
		return err
	}

	affected, err := res.RowsAffected()
	if err != nil {
		return err
	}

	if affected != 1 {
		return errors.WithMessagef(models.ErrNotFound, "User not found")
	}

	return nil
}

func (r *UserRepository) DecrementTotalFollowers(ctx context.Context, userId int64) error {
	res, err := psql.
		Update(`"user"`).
		Set("total_followers", sq.Expr("total_followers - 1")).
		Where(sq.Eq{"id": userId}).
		RunWith(r.db).
		ExecContext(ctx)
	if err != nil {
		return err
	}

	affected, err := res.RowsAffected()
	if err != nil {
		return err
	}

	if affected != 1 {
		return errors.WithMessagef(models.ErrNotFound, "User not found")
	}

	return nil
}

func (r *UserRepository) IncrementTotalFollowing(ctx context.Context, userId int64) error {
	res, err := psql.
		Update(`"user"`).
		Set("total_following", sq.Expr("total_following + 1")).
		Where(sq.Eq{"id": userId}).
		RunWith(r.db).
		ExecContext(ctx)
	if err != nil {
		return err
	}

	affected, err := res.RowsAffected()
	if err != nil {
		return err
	}

	if affected != 1 {
		return errors.WithMessagef(models.ErrNotFound, "User not found")
	}

	return nil
}

func (r *UserRepository) DecrementTotalFollowing(ctx context.Context, userId int64) error {
	res, err := psql.
		Update(`"user"`).
		Set("total_following", sq.Expr("total_following - 1")).
		Where(sq.Eq{"id": userId}).
		RunWith(r.db).
		ExecContext(ctx)
	if err != nil {
		return err
	}

	affected, err := res.RowsAffected()
	if err != nil {
		return err
	}

	if affected != 1 {
		return errors.WithMessagef(models.ErrNotFound, "User not found")
	}

	return nil
}
