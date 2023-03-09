package service

import (
	"context"
	"database/sql"
	"fmt"
	"github.com/pkg/errors"
	"github.com/sirupsen/logrus"
	"gopkg.in/guregu/null.v4"
	"math"
	chatRepository "sosmed-go-backend/chat/repository"
	"sosmed-go-backend/common"
	messageRepository "sosmed-go-backend/message/repository"
	"sosmed-go-backend/models"
	userRepository "sosmed-go-backend/user/repository"
	"time"
)

type ChatService struct {
	chatRepository    chatRepository.ChatRepository
	memberRepository  chatRepository.MemberRepository
	messageRepository messageRepository.MessageRepository
	userRepository    userRepository.UserRepository
}

func NewChatService(
	chatRepository chatRepository.ChatRepository,
	memberRepository chatRepository.MemberRepository,
	messageRepository messageRepository.MessageRepository,
	userRepository userRepository.UserRepository,
) ChatService {
	return ChatService{
		chatRepository:    chatRepository,
		memberRepository:  memberRepository,
		messageRepository: messageRepository,
		userRepository:    userRepository,
	}
}

func (c *ChatService) Create(ctx context.Context, req *models.CreateChatReq) (any, error) {
	if !req.Type.Valid() {
		return nil, errors.WithMessagef(models.ErrBadRequest, "Type is not valid")
	}

	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return nil, err
	}

	// Make sure group chat contains at least 2 members and must be unique
	members := removeDuplicateValues(append(req.Members, profile.Id))

	if len(members) < 2 {
		return nil, errors.WithMessagef(models.ErrBadRequest, "Members must be at least 2 members and unique")
	}

	if req.Type == models.TypePrivate && len(members) != 2 {
		return nil, errors.WithMessagef(models.ErrBadRequest, "Private chat must have 2 members")
	}

	res, err := c.chatRepository.WithTransactionV(ctx, func(ctx context.Context) (any, error) {
		if req.Type == models.TypePrivate {
			chat, err := c.chatRepository.GetByPrivateChatId(ctx, privateChatId(members[0], members[1]))
			if err != nil {
				if errors.Is(err, sql.ErrNoRows) {
					goto CreateNewChat
				}
				return nil, err
			}

			var pairId int64
			for _, id := range members {
				if id != profile.Id {
					pairId = id
				}
			}

			user, err := c.userRepository.GetById(ctx, pairId)
			if err != nil {
				logrus.Error(err)
				logrus.Infof("Failed to load user with id: %d", pairId)
				return nil, err
			}

			recentMessages, err := c.messageRepository.GetByChatId(ctx, chat.Id, models.Page{NextId: math.MaxInt64, Size: 2})
			if err != nil {
				return nil, err
			}

			res := models.ChatPrivateRes{
				Id:            chat.Id,
				PrivateChatId: chat.PrivateChatId.String,
				Pair: models.Profile{
					Id:       user.Id,
					Name:     user.Name,
					Username: user.Username,
					Photo:    user.Photo,
				},
				TypeChat:          chat.Type,
				RecentMessages:    models.MessagesToResponses(recentMessages),
				CreatedAt:         chat.CreatedAt.UnixMilli(),
				LastMessageSentAt: chat.LastMessageSentAt.UnixMilli(),
			}

			return res, nil
		}

	CreateNewChat:

		chat := models.Chat{
			Creator:           profile,
			Name:              req.Name,
			Photo:             req.Photo,
			Type:              req.Type,
			CreatedAt:         time.Now(),
			UpdatedAt:         time.Now(),
			LastMessageSentAt: time.Now(),
		}
		if chat.Type == models.TypePrivate {
			chat.PrivateChatId = null.NewString(privateChatId(members[0], members[1]), true)
		}

		err = c.chatRepository.Create(ctx, &chat)
		if err != nil {
			return nil, err
		}

		for _, memberId := range members {
			member := models.Member{
				UserId:    memberId,
				ChatId:    chat.Id,
				CreatedAt: time.Now(),
			}

			err := c.memberRepository.Create(ctx, &member)
			if err != nil {
				return nil, err
			}
		}

		if req.Type == models.TypeGroup {
			memberIds, err := c.memberRepository.GetAllMemberIds(ctx, chat.Id)
			if err != nil {
				return nil, err
			}

			users, err := c.userRepository.GetByIds(ctx, memberIds, models.PageDefault())
			if err != nil {
				return nil, err
			}

			members := make([]models.Profile, 0)

			for _, user := range users {
				members = append(members, models.Profile{
					Id:       user.Id,
					Name:     user.Name,
					Username: user.Username,
					Photo:    user.Photo,
				})
			}

			res := models.ChatGroupRes{
				Id:                chat.Id,
				Creator:           chat.Creator,
				Name:              chat.Name,
				Photo:             chat.Photo,
				Type:              chat.Type,
				RecentMessages:    make([]models.MessageRes, 0),
				Members:           members,
				CreatedAt:         chat.CreatedAt.UnixMilli(),
				UpdatedAt:         chat.UpdatedAt.UnixMilli(),
				LastMessageSentAt: chat.LastMessageSentAt.UnixMilli(),
			}

			return res, nil
		} else {
			var pairId int64
			for _, id := range members {
				if id != profile.Id {
					pairId = id
				}
			}

			user, err := c.userRepository.GetById(ctx, pairId)
			if err != nil {
				logrus.Error(err)
				logrus.Infof("Failed to load user with id: %d", pairId)
				return nil, err
			}

			res := models.ChatPrivateRes{
				Id:            chat.Id,
				PrivateChatId: chat.PrivateChatId.String,
				Pair: models.Profile{
					Id:       user.Id,
					Name:     user.Name,
					Username: user.Username,
					Photo:    user.Photo,
				},
				TypeChat:          chat.Type,
				RecentMessages:    make([]models.MessageRes, 0),
				CreatedAt:         chat.CreatedAt.UnixMilli(),
				LastMessageSentAt: chat.LastMessageSentAt.UnixMilli(),
			}

			return res, nil
		}
	})

	if err != nil {
		return nil, err
	}

	return res, nil
}

func privateChatId(userId1, userId2 int64) string {
	if userId1 < userId2 {
		return fmt.Sprintf("%d-%d", userId1, userId2)
	} else {
		return fmt.Sprintf("%d-%d", userId2, userId1)
	}
}

func removeDuplicateValues(intSlice []int64) []int64 {
	keys := make(map[int64]bool)
	list := make([]int64, 0)

	for _, entry := range intSlice {
		if _, value := keys[entry]; !value {
			keys[entry] = true
			list = append(list, entry)
		}
	}
	return list
}

func (c *ChatService) GetChats(ctx context.Context, page models.Page) ([]any, error) {
	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return []any{}, nil
	}

	chatIds, err := c.memberRepository.GetAllChatIds(ctx, profile.Id)
	if err != nil {
		return nil, err
	}

	chats, err := c.chatRepository.GetByIds(ctx, chatIds, page)
	if err != nil {
		return nil, err
	}

	res := make([]any, 0)

	for _, chat := range chats {
		if !chat.Type.Valid() {
			return nil, errors.WithMessagef(models.ErrServerError, "Expected type private or group. But the type is %s", chat.Type)
		}

		memberIds, err := c.memberRepository.GetAllMemberIds(ctx, chat.Id)
		if err != nil {
			return nil, err
		}

		recentMessages, err := c.messageRepository.GetByChatId(ctx, chat.Id, models.Page{NextId: time.Now().UnixMilli(), Size: 2})
		if err != nil {
			return nil, err
		}

		if chat.Type == models.TypeGroup {
			users, err := c.userRepository.GetByIds(ctx, memberIds, models.PageDefault())
			if err != nil {
				return nil, err
			}

			members := make([]models.Profile, 0)

			for _, user := range users {
				members = append(members, models.Profile{
					Id:       user.Id,
					Name:     user.Name,
					Username: user.Username,
					Photo:    user.Photo,
				})
			}

			res = append(res, models.ChatGroupRes{
				Id:                chat.Id,
				Creator:           chat.Creator,
				Name:              chat.Name,
				Photo:             chat.Photo,
				Type:              chat.Type,
				RecentMessages:    models.MessagesToResponses(recentMessages),
				Members:           members,
				CreatedAt:         chat.CreatedAt.UnixMilli(),
				UpdatedAt:         chat.UpdatedAt.UnixMilli(),
				LastMessageSentAt: chat.LastMessageSentAt.UnixMilli(),
			})
		} else {
			var pairId int64
			for _, id := range memberIds {
				if id != profile.Id {
					pairId = id
				}
			}

			user, err := c.userRepository.GetById(ctx, pairId)
			if err != nil {
				logrus.Error(err)
				logrus.Infof("Failed to load use with id: %d", pairId)
				return nil, err
			}

			res = append(res, models.ChatPrivateRes{
				Id:            chat.Id,
				PrivateChatId: chat.PrivateChatId.String,
				Pair: models.Profile{
					Id:       user.Id,
					Name:     user.Name,
					Username: user.Username,
					Photo:    user.Photo,
				},
				TypeChat:          chat.Type,
				RecentMessages:    models.MessagesToResponses(recentMessages),
				CreatedAt:         chat.CreatedAt.UnixMilli(),
				LastMessageSentAt: chat.LastMessageSentAt.UnixMilli(),
			})
		}
	}

	return res, nil
}

func (c *ChatService) GetChatById(ctx context.Context, chatId int64) (any, error) {
	profile, err := common.GetCurrentProfileFromContext(ctx)
	if err != nil {
		return nil, err
	}

	return c.GetChatByChatIdAndUserId(ctx, chatId, profile.Id)
}

func (c *ChatService) GetChatByChatIdAndUserId(ctx context.Context, chatId int64, userId int64) (any, error) {
	chat, err := c.chatRepository.GetById(ctx, chatId)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, errors.WithMessagef(models.ErrNotFound, "Chat not found")
		}
		return nil, err
	}

	memberIds, err := c.memberRepository.GetAllMemberIds(ctx, chat.Id)
	if err != nil {
		return nil, err
	}

	if !chat.Type.Valid() {
		return nil, errors.WithMessagef(models.ErrServerError, "Expected type private or group. But the type is %s", chat.Type)
	}

	recentMessages, err := c.messageRepository.GetByChatId(ctx, chat.Id, models.Page{NextId: time.Now().UnixMilli(), Size: 2})
	if err != nil {
		return nil, err
	}

	var res any
	if chat.Type == models.TypeGroup {
		users, err := c.userRepository.GetByIds(ctx, memberIds, models.PageDefault())
		if err != nil {
			return nil, err
		}

		members := make([]models.Profile, 0)

		for _, user := range users {
			members = append(members, models.Profile{
				Id:       user.Id,
				Name:     user.Name,
				Username: user.Username,
				Photo:    user.Photo,
			})
		}

		res = models.ChatGroupRes{
			Id:                chat.Id,
			Creator:           chat.Creator,
			Name:              chat.Name,
			Photo:             chat.Photo,
			Type:              chat.Type,
			RecentMessages:    models.MessagesToResponses(recentMessages),
			Members:           members,
			CreatedAt:         chat.CreatedAt.UnixMilli(),
			UpdatedAt:         chat.UpdatedAt.UnixMilli(),
			LastMessageSentAt: chat.LastMessageSentAt.UnixMilli(),
		}
	} else {
		var pairId int64
		for _, id := range memberIds {
			if id != userId {
				pairId = id
			}
		}

		user, err := c.userRepository.GetById(ctx, pairId)
		if err != nil {
			logrus.Error(err)
			logrus.Infof("Failed to load use with id: %d", pairId)
			return nil, err
		}

		res = models.ChatPrivateRes{
			Id:            chat.Id,
			PrivateChatId: chat.PrivateChatId.String,
			Pair: models.Profile{
				Id:       user.Id,
				Name:     user.Name,
				Username: user.Username,
				Photo:    user.Photo,
			},
			TypeChat:          chat.Type,
			RecentMessages:    models.MessagesToResponses(recentMessages),
			CreatedAt:         chat.CreatedAt.UnixMilli(),
			LastMessageSentAt: chat.LastMessageSentAt.UnixMilli(),
		}
	}

	return res, nil
}

func (c *ChatService) IsMember(ctx context.Context, chatId, userId int64) (bool, error) {
	member, err := c.memberRepository.GetMember(ctx, chatId, userId)
	if err != nil {
		if errors.Is(err, models.ErrNotFound) {
			return false, nil
		}

		return false, err
	}

	return member != (models.Member{}), nil
}
