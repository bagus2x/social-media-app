package repository

import (
	"context"
	"database/sql"
	sq "github.com/Masterminds/squirrel"
	"gopkg.in/guregu/null.v4"
	"sosmed-go-backend/common/database"
	"sosmed-go-backend/models"
)

type NotificationRepository struct {
	db *sql.DB
	*database.Tx
	psql sq.StatementBuilderType
}

func NewNotificationRepository(db *sql.DB) NotificationRepository {
	return NotificationRepository{
		db:   db,
		Tx:   database.NewTx(db),
		psql: sq.StatementBuilder.PlaceholderFormat(sq.Dollar),
	}
}

func (n *NotificationRepository) Create(ctx context.Context, notification *models.Notification) error {
	err := n.psql.
		Insert("notification").
		Columns("owner_id", "issuer_id", "feed_id", "comment_id", "type", "seen", "created_at").
		Values(
			notification.OwnerId,
			null.NewInt(notification.Issuer.Id, notification.Issuer.Id != 0),
			notification.FeedId,
			notification.CommentId,
			notification.Type,
			notification.Seen,
			notification.CreatedAt,
		).
		Suffix("RETURNING id").
		RunWith(n.GetRunner(ctx)).
		QueryRowContext(ctx).
		Scan(&notification.Id)
	if err != nil {
		return err
	}

	return nil
}

func (n *NotificationRepository) GetByUserId(ctx context.Context, ownerId int64, page models.Page) ([]models.Notification, error) {
	rows, err := n.psql.
		Select(
			"notification.id", "notification.owner_id", "issuer.id", "issuer.username", "issuer.name",
			"issuer.photo", "notification.feed_id", "notification.comment_id", "notification.type", "notification.seen",
			"notification.created_at",
		).
		From("notification").
		RightJoin(`"user" issuer ON issuer.id = notification.issuer_id`).
		Where(
			sq.And{
				sq.Eq{"notification.owner_id": ownerId},
				sq.Lt{"notification.id": page.NextId},
				sq.Gt{"notification.id": page.PreviousId},
			},
		).
		Limit(page.Size).
		OrderBy("notification.created_at DESC").
		RunWith(n.GetRunner(ctx)).
		QueryContext(ctx)
	if err != nil {
		return nil, err
	}

	notifications := make([]models.Notification, 0)

	for rows.Next() {
		var notification models.Notification

		err := rows.Scan(
			&notification.Id,
			&notification.OwnerId,
			&notification.Issuer.Id,
			&notification.Issuer.Username,
			&notification.Issuer.Name,
			&notification.Issuer.Photo,
			&notification.FeedId,
			&notification.CommentId,
			&notification.Type,
			&notification.Seen,
			&notification.CreatedAt,
		)
		if err != nil {
			return nil, err
		}

		notifications = append(notifications, notification)
	}

	return notifications, nil
}

func (n *NotificationRepository) MarkAsSeen(ctx context.Context, notificationIds []int64) error {
	_, err := n.psql.
		Update("notification").
		Set("seen", true).
		Where(sq.Eq{"id": notificationIds}).
		RunWith(n.GetRunner(ctx)).
		ExecContext(ctx)
	if err != nil {
		return err
	}

	return nil
}
