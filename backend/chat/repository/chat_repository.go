package repository

import (
	"context"
	"database/sql"
	sq "github.com/Masterminds/squirrel"
	"github.com/pkg/errors"
	"sosmed-go-backend/common/database"
	"sosmed-go-backend/models"
	"time"
)

type ChatRepository struct {
	psql sq.StatementBuilderType
	db   *sql.DB
	*database.Tx
}

func NewChatRepository(db *sql.DB) ChatRepository {
	return ChatRepository{
		psql: sq.StatementBuilder.PlaceholderFormat(sq.Dollar),
		db:   db,
		Tx:   database.NewTx(db),
	}
}

func (r *ChatRepository) Create(ctx context.Context, chat *models.Chat) error {
	err := r.psql.
		Insert("chat").
		Columns("creator_id", "name", "photo", "type", "private_chat_id", "created_at", "updated_at", "last_message_sent_at").
		Values(chat.Creator.Id, chat.Name, chat.Photo, chat.Type, chat.PrivateChatId, chat.CreatedAt, chat.UpdatedAt, chat.LastMessageSentAt).
		RunWith(r.Tx.GetRunner(ctx)).
		Suffix("RETURNING id").
		QueryRowContext(ctx).
		Scan(&chat.Id)
	if err != nil {
		return err
	}

	return nil
}

func (r *ChatRepository) GetByIds(ctx context.Context, chatIds []int64, page models.Page) ([]models.Chat, error) {
	rows, err := r.psql.
		Select(
			"chat.id", "creator.id", "creator.username", "creator.name", "creator.photo", "chat.name",
			"chat.photo", "chat.type", "chat.private_chat_id", "chat.created_at", "chat.updated_at", "chat.last_message_sent_at",
		).
		From("chat").
		Join(`"user" creator ON chat.creator_id = creator.id`).
		Where(
			sq.And{
				sq.Lt{"chat.last_message_sent_at": time.UnixMilli(page.NextId)},
				sq.Gt{"chat.last_message_sent_at": time.UnixMilli(page.PreviousId)},
				sq.Eq{"chat.id": chatIds},
			},
		).
		Limit(page.Size).
		OrderBy("last_message_sent_at DESC, created_at DESC").
		RunWith(r.Tx.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		return nil, err
	}

	chats := make([]models.Chat, 0)
	for rows.Next() {
		var chat models.Chat
		err := rows.Scan(
			&chat.Id,
			&chat.Creator.Id,
			&chat.Creator.Username,
			&chat.Creator.Name,
			&chat.Creator.Photo,
			&chat.Name,
			&chat.Photo,
			&chat.Type,
			&chat.PrivateChatId,
			&chat.CreatedAt,
			&chat.UpdatedAt,
			&chat.LastMessageSentAt,
		)
		if err != nil {
			return nil, err
		}

		chats = append(chats, chat)
	}

	return chats, nil
}

func (r *ChatRepository) GetById(ctx context.Context, chatId int64) (models.Chat, error) {
	var chat models.Chat
	err := r.psql.
		Select(
			"chat.id", "creator.id", "creator.username", "creator.name", "creator.photo", "chat.name",
			"chat.photo", "chat.type", "chat.private_chat_id", "chat.created_at", "chat.updated_at", "chat.last_message_sent_at",
		).
		From("chat").
		Join(`"user" creator ON chat.creator_id = creator.id`).
		Where(sq.Eq{"chat.id": chatId}).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryRowContext(ctx).
		Scan(
			&chat.Id,
			&chat.Creator.Id,
			&chat.Creator.Username,
			&chat.Creator.Name,
			&chat.Creator.Photo,
			&chat.Name,
			&chat.Photo,
			&chat.Type,
			&chat.PrivateChatId,
			&chat.CreatedAt,
			&chat.UpdatedAt,
			&chat.LastMessageSentAt,
		)
	if err != nil {
		return models.Chat{}, err
	}

	return chat, nil
}

func (r *ChatRepository) GetByPrivateChatId(ctx context.Context, privateChatId string) (models.Chat, error) {
	var chat models.Chat
	err := r.psql.
		Select(
			"chat.id", "creator.id", "creator.username", "creator.name", "creator.photo", "chat.name",
			"chat.photo", "chat.type", "chat.private_chat_id", "chat.created_at", "chat.updated_at", "chat.last_message_sent_at",
		).
		From("chat").
		Join(`"user" creator ON chat.creator_id = creator.id`).
		Where(sq.Eq{"chat.private_chat_id": privateChatId}).
		RunWith(r.Tx.GetRunner(ctx)).
		QueryRowContext(ctx).
		Scan(
			&chat.Id,
			&chat.Creator.Id,
			&chat.Creator.Username,
			&chat.Creator.Name,
			&chat.Creator.Photo,
			&chat.Name,
			&chat.Photo,
			&chat.Type,
			&chat.PrivateChatId,
			&chat.CreatedAt,
			&chat.UpdatedAt,
			&chat.LastMessageSentAt,
		)
	if err != nil {
		return models.Chat{}, err
	}

	return chat, nil
}

func (r *ChatRepository) UpdateLastMessageSent(ctx context.Context, chatId int64) error {
	result, err := r.psql.
		Update("chat").
		Set("last_message_sent_at", time.Now()).
		Where(sq.Eq{"id": chatId}).
		RunWith(r.GetRunner(ctx)).ExecContext(ctx)
	if err != nil {
		return err
	}

	affected, err := result.RowsAffected()
	if err != nil {
		return err
	}

	if affected != 1 {
		return errors.WithMessage(models.ErrNotFound, "Message not found")
	}

	return nil
}
