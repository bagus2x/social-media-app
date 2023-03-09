package main

import (
	"context"
	firebase "firebase.google.com/go"
	"fmt"
	"github.com/cloudinary/cloudinary-go/v2"
	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"github.com/gofiber/fiber/v2/middleware/monitor"
	recover2 "github.com/gofiber/fiber/v2/middleware/recover"
	"github.com/sirupsen/logrus"
	"google.golang.org/api/option"
	authHandler "sosmed-go-backend/auth/handler"
	authSvc "sosmed-go-backend/auth/service"
	chatHandler "sosmed-go-backend/chat/handler"
	chatRepo "sosmed-go-backend/chat/repository"
	chatSvc "sosmed-go-backend/chat/service"
	commentHandler "sosmed-go-backend/comment/handler"
	commentRepo "sosmed-go-backend/comment/repository"
	commentSvc "sosmed-go-backend/comment/service"
	"sosmed-go-backend/config"
	feedHandler "sosmed-go-backend/feed/handler"
	feedRepo "sosmed-go-backend/feed/repository"
	feedSvc "sosmed-go-backend/feed/service"
	messagingSvc "sosmed-go-backend/firebase/service"
	messageHandler "sosmed-go-backend/message/handler"
	messageRepo "sosmed-go-backend/message/repository"
	messageSvc "sosmed-go-backend/message/service"
	tagHandler "sosmed-go-backend/tag/handler"
	tagRepo "sosmed-go-backend/tag/repository"
	tagSvc "sosmed-go-backend/tag/service"
	uploaderHandler "sosmed-go-backend/uploader/handler"
	uploaderRepo "sosmed-go-backend/uploader/repository"
	uploaderSvc "sosmed-go-backend/uploader/service"
	userHandler "sosmed-go-backend/user/handler"
	userRepo "sosmed-go-backend/user/repository"
	userSvc "sosmed-go-backend/user/service"
	"time"
)

func main() {
	run()
}

func run() {
	logrus.SetReportCaller(true)
	logrus.SetLevel(logrus.DebugLevel)

	appConfig, err := config.LoadAppConfig(".")
	if err != nil {
		logrus.Fatal(err)
	}

	fiberApp := fiber.New(config.Fiber)
	fiberApp.Use(logger.New())
	fiberApp.Use(recover2.New())

	fiberApp.Get("/metrics", monitor.New(monitor.Config{Title: "Metrics"}))

	db, err := config.OpenDatabase(appConfig)
	if err != nil {
		logrus.Fatal(err)
	}

	cld, err := cloudinary.NewFromParams(appConfig.CloudinaryCloudName, appConfig.CloudinaryApiKey, appConfig.CloudinaryApiSecret)
	if err != nil {
		logrus.Fatal(err)
	}

	opt := option.WithCredentialsFile("firebase-service-account-key.json")
	firebaseApp, err := firebase.NewApp(context.Background(), nil, opt)
	if err != nil {
		logrus.Fatal(err)
		return
	}

	firebaseClient, err := firebaseApp.Messaging(context.Background())
	if err != nil {
		logrus.Fatal(err)
		return
	}

	// Perform dependency injection (singleton) in top level function
	userRepository := userRepo.NewUserRepository(db)
	followedUserRepository := userRepo.NewFollowedUserRepository(db)
	uploaderRepository := uploaderRepo.NewUploaderRepository(cld)
	feedRepository := feedRepo.NewFeedRepository(db)
	favoriteFeedRepository := feedRepo.NewFavoriteFeedRepository(db)
	commentRepository := commentRepo.NewCommentRepository(db)
	chatRepository := chatRepo.NewChatRepository(db)
	memberRepository := chatRepo.NewMemberRepository(db)
	messageRepository := messageRepo.NewMessageRepository(db)
	tagRepository := tagRepo.NewTagRepository(db)

	authManager := authSvc.NewAuthManager(time.Hour*24*365, "jhahahahahahah", time.Hour*24*365, "jhahahahahahah")
	userService := userSvc.NewUserService(userRepository, followedUserRepository)
	authService := authSvc.NewAuthService(userRepository, authManager)
	uploaderService := uploaderSvc.NewUploaderService(uploaderRepository)
	feedService := feedSvc.NewFeedService(feedRepository, followedUserRepository, favoriteFeedRepository, tagRepository)
	commentService := commentSvc.NewCommentService(commentRepository, feedRepository)
	chatService := chatSvc.NewChatService(chatRepository, memberRepository, messageRepository, userRepository)
	messageService := messageSvc.NewMessageRepository(messageRepository, chatRepository)
	messagingService := messagingSvc.NewMessagingService(firebaseClient, userRepository, memberRepository, chatService)
	tagService := tagSvc.NewTagService(tagRepository)

	authMiddleware := authHandler.NewAuthMiddleware(authManager)

	authHandler.NewAuthHandler(fiberApp, authService)
	userHandler.NewUserHandler(fiberApp, userService, authMiddleware)
	uploaderHandler.NewUploaderHandler(fiberApp, authMiddleware, uploaderService)
	feedHandler.NewFeedHandler(fiberApp, feedService, authMiddleware)
	commentHandler.NewCommentHandler(fiberApp, authMiddleware, commentService)
	chatHandler.NewChatHandler(fiberApp, authMiddleware, chatService, messagingService)
	messageHandler.NewMessageHandler(fiberApp, authMiddleware, messageService, chatService, userService, messagingService)
	tagHandler.NewTagService(fiberApp, tagService)

	logrus.Fatal(fiberApp.Listen(fmt.Sprintf(":%d", appConfig.Port)))
}
