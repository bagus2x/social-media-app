package repository

import (
	"context"
	"database/sql"
	sq "github.com/Masterminds/squirrel"
	"github.com/pkg/errors"
	"sosmed-go-backend/common/database"
	"sosmed-go-backend/models"
)

type MemberRepository struct {
	psql sq.StatementBuilderType
	db   *sql.DB
	*database.Tx
}

func NewMemberRepository(db *sql.DB) MemberRepository {
	return MemberRepository{
		psql: sq.StatementBuilder.PlaceholderFormat(sq.Dollar),
		db:   db,
		Tx:   database.NewTx(db),
	}
}

func (r *MemberRepository) Create(ctx context.Context, member *models.Member) error {
	_, err := r.psql.
		Insert("member").
		Columns("user_id", "chat_id", "created_at").
		Values(member.UserId, member.ChatId, member.CreatedAt).
		RunWith(r.Tx.GetRunner(ctx)).
		ExecContext(ctx)
	if err != nil {
		return err
	}

	return nil
}

func (r *MemberRepository) Delete(ctx context.Context, userId, chatId int64) (bool, error) {
	res, err := r.psql.
		Delete("member").
		Where(sq.Eq{"user_id": userId, "chat_id": chatId}).
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

func (r *MemberRepository) GetAllChatIds(ctx context.Context, userId int64) ([]int64, error) {
	rows, err := r.psql.
		Select("chat_id").
		From("member").
		Where(sq.Eq{"user_id": userId}).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		return nil, err
	}

	chatIds := make([]int64, 0)

	for rows.Next() {
		var id int64
		err := rows.Scan(&id)
		if err != nil {
			return nil, err
		}

		chatIds = append(chatIds, id)
	}

	return chatIds, nil
}

func (r *MemberRepository) GetAllMemberIds(ctx context.Context, chatId int64) ([]int64, error) {
	rows, err := r.psql.
		Select("user_id").
		From("member").
		Where(sq.Eq{"chat_id": chatId}).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		return nil, err
	}

	chatIds := make([]int64, 0)

	for rows.Next() {
		var id int64
		err := rows.Scan(&id)
		if err != nil {
			return nil, err
		}

		chatIds = append(chatIds, id)
	}

	return chatIds, nil
}

func (r *MemberRepository) GetMember(ctx context.Context, chatId, userId int64) (models.Member, error) {
	var member models.Member
	err := r.psql.
		Select("user_id", "chat_id", "created_at").
		From("member").
		Where(sq.Eq{"user_id": userId, "chat_id": chatId}).
		RunWith(r.GetRunner(ctx)).
		QueryRowContext(ctx).
		Scan(&member.UserId, &member.ChatId, &member.CreatedAt)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return models.Member{}, errors.WithMessagef(models.ErrNotFound, "Member not found")
		}

		return models.Member{}, err
	}

	return member, nil
}
