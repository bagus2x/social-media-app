package config

import (
	"github.com/gofiber/fiber/v2"
	"github.com/pkg/errors"
	"github.com/spf13/viper"
	"sosmed-go-backend/models"
	"strings"
)

type AppConfig struct {
	Port                        int    `mapstructure:"PORT"`
	DbUsername                  string `mapstructure:"DB_USERNAME"`
	DbPassword                  string `mapstructure:"DB_PASSWORD"`
	DbPort                      int    `mapstructure:"DB_PORT"`
	DbHost                      string `mapstructure:"DB_HOST"`
	DbName                      string `mapstructure:"DB_NAME"`
	DbSslMode                   string `mapstructure:"DB_SSL_MODE"`
	CloudinaryCloudName         string `mapstructure:"CLOUDINARY_CLOUD_NAME"`
	CloudinaryApiKey            string `mapstructure:"CLOUDINARY_API_KEY"`
	CloudinaryApiSecret         string `mapstructure:"CLOUDINARY_API_SECRET"`
	AccessTokenKey              string `mapstructure:"ACCESS_TOKEN_KEY"`
	AccessTokenLifeTimeMinutes  int64  `mapstructure:"ACCESS_TOKEN_LIFETIME_MINUTES"`
	RefreshTokenKey             string `mapstructure:"REFRESH_TOKEN_KEY"`
	RefreshTokenLifeTimeMinutes int64  `mapstructure:"REFRESH_TOKEN_LIFETIME_MINUTES"`
}

func LoadAppConfig(path string) (AppConfig, error) {
	viper.SetConfigType("env")
	viper.SetConfigName("app")
	viper.AddConfigPath(path)
	viper.AutomaticEnv()
	err := viper.ReadInConfig()
	if err != nil {
		return AppConfig{}, err
	}

	var config AppConfig
	err = viper.Unmarshal(&config)
	if err != nil {
		return AppConfig{}, err
	}

	return config, nil
}

var Fiber = fiber.Config{
	ErrorHandler: func(ctx *fiber.Ctx, err error) error {
		if errors.Is(err, models.ErrBadRequest) ||
			errors.Is(err, models.ErrUnauthorized) ||
			errors.Is(err, models.ErrForbidden) ||
			errors.Is(err, models.ErrNotFound) ||
			errors.Is(err, models.ErrConflict) ||
			errors.Is(err, models.ErrServerError) {
			error := errors.Cause(err).(*models.Error)
			messages := strings.Split(err.Error(), ":")
			message := err.Error()
			if len(messages) > 0 {
				message = messages[0]
			}
			error.Message = message
			return ctx.Status(error.Code).JSON(error)
		}
		return ctx.Status(500).JSON(models.NewError("Internal server error", 500))
	},
}
