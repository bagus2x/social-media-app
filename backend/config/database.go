package config

import (
	"database/sql"
	"fmt"
	_ "github.com/lib/pq"
	"github.com/sirupsen/logrus"
)

func OpenDatabase(config AppConfig) (*sql.DB, error) {
	dataSource := fmt.Sprintf("host=%s port=%d user=%s password=%s dbname=%s sslmode=%s", config.DbHost, config.DbPort, config.DbUsername, config.DbPassword, config.DbName, config.DbSslMode)
	logrus.Println(dataSource)
	db, err := sql.Open("postgres", dataSource)
	if err != nil {
		return nil, err
	}

	return db, nil
}
