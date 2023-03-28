package repository

import (
	"context"
	"database/sql"
	sq "github.com/Masterminds/squirrel"
	"sosmed-go-backend/common/database"
	"sosmed-go-backend/models"
)

type VideoRepository struct {
	db *sql.DB
	*database.Tx
	psql sq.StatementBuilderType
}

func NewVideoRepository(db *sql.DB) VideoRepository {
	return VideoRepository{
		db:   db,
		Tx:   database.NewTx(db),
		psql: sq.StatementBuilder.PlaceholderFormat(sq.Dollar),
	}
}

func (v *VideoRepository) Create(ctx context.Context, video *models.Video) {
	
}
