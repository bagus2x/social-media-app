package repository

import (
	"context"
	"database/sql"
	"encoding/json"
	sq "github.com/Masterminds/squirrel"
	"sosmed-go-backend/common/database"
	"sosmed-go-backend/models"
)

type MessageRepository struct {
	db *sql.DB
	*database.Tx
	psql sq.StatementBuilderType
}

func NewMessageRepository(db *sql.DB) MessageRepository {
	return MessageRepository{
		db:   db,
		Tx:   database.NewTx(db),
		psql: sq.StatementBuilder.PlaceholderFormat(sq.Dollar),
	}
}

func (m *MessageRepository) Create(ctx context.Context, message *models.Message) error {
	if message.Medias == nil {
		message.Medias = make([]models.Media, 0)
	}

	medias, err := json.Marshal(message.Medias)
	if err != nil {
		return err
	}

	err = m.psql.
		Insert("message").
		Columns("chat_id", "sender_id", "description", "medias", "created_at").
		Values(message.ChatId, message.Sender.Id, message.Description, medias, message.CreatedAt).
		Suffix("RETURNING id").
		RunWith(m.GetRunner(ctx)).
		QueryRowContext(ctx).
		Scan(&message.Id)
	if err != nil {
		return err
	}

	return nil
}

func (m *MessageRepository) GetByChatId(ctx context.Context, chatId int64, page models.Page) ([]models.Message, error) {
	rows, err := m.psql.
		Select(
			"message.id", "message.chat_id", "sender.id", "sender.username", "sender.name", "sender.photo",
			"message.description", "message.medias", "message.created_at",
		).
		From("message").
		Join(`"user" sender ON message.sender_id = sender.id`).
		Where(
			sq.And{
				sq.Eq{"message.chat_id": chatId},
				sq.Lt{"message.id": page.NextId},
				sq.Gt{"message.id": page.PreviousId},
			},
		).
		OrderBy("message.created_at DESC").
		Limit(page.Size).
		RunWith(m.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		return nil, err
	}

	messages := make([]models.Message, 0)
	for rows.Next() {
		var message models.Message
		medias := make([]byte, 0)

		err := rows.Scan(
			&message.Id,
			&message.ChatId,
			&message.Sender.Id,
			&message.Sender.Username,
			&message.Sender.Name,
			&message.Sender.Photo,
			&message.Description,
			&medias,
			&message.CreatedAt,
		)
		if err != nil {
			return nil, err
		}

		err = json.Unmarshal(medias, &message.Medias)
		if err != nil {
			return nil, err
		}

		messages = append(messages, message)
	}

	return messages, nil
}
