package database

import (
	"context"
	"database/sql"
	"github.com/sirupsen/logrus"
)

type Runner interface {
	ExecContext(ctx context.Context, query string, args ...any) (sql.Result, error)
	QueryRowContext(ctx context.Context, query string, args ...any) *sql.Row
	QueryContext(ctx context.Context, query string, args ...any) (*sql.Rows, error)
	Exec(query string, args ...any) (sql.Result, error)
	QueryRow(query string, args ...any) *sql.Row
	Query(query string, args ...any) (*sql.Rows, error)
}

type Tx struct {
	db *sql.DB
}

func NewTx(db *sql.DB) *Tx {
	return &Tx{db}
}

func (t *Tx) WithTransaction(ctx context.Context, fn func(c context.Context) error) (err error) {
	tx, err := t.db.BeginTx(ctx, nil)
	if err != nil {
		return
	}

	_, err = tx.ExecContext(ctx, "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE")
	if err != nil {
		return
	}

	c := context.WithValue(ctx, "TransactionContextKey", tx)
	err = fn(c)
	if err != nil {
		if errTX := tx.Rollback(); errTX != nil {
			logrus.Error("failed to rollback transaction", errTX)
		}
		return
	}

	if errTX := tx.Commit(); errTX != nil {
		logrus.Error("failed to commit transaction", errTX)
	}
	logrus.Debug("Committed")
	return
}

func (t *Tx) WithTransactionV(ctx context.Context, fn func(ctx context.Context) (any, error)) (res any, err error) {
	tx, err := t.db.BeginTx(ctx, nil)
	if err != nil {
		return
	}

	_, err = tx.ExecContext(ctx, "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE")
	if err != nil {
		return
	}

	c := context.WithValue(ctx, "TransactionContextKey", tx)
	res, err = fn(c)
	if err != nil {
		if errTX := tx.Rollback(); errTX != nil {
			logrus.Error("failed to rollback transaction", errTX)
		}
		return
	}

	if errTX := tx.Commit(); errTX != nil {
		logrus.Error("failed to commit transaction", errTX)
	}
	logrus.Debug("Committed")
	return
}

func (t *Tx) GetRunner(ctx context.Context) Runner {
	if tx, ok := ctx.Value("TransactionContextKey").(*sql.Tx); ok && tx != nil {
		logrus.Debug("Tx found")
		return tx
	}
	logrus.Debug("Tx not found")
	return t.db
}
