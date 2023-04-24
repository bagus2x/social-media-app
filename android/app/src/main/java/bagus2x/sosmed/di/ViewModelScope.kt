package bagus2x.sosmed.di

import bagus2x.sosmed.data.local.AuthLocalDataSource
import bagus2x.sosmed.domain.repository.*
import bagus2x.sosmed.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelScope {

    @Provides
    @ViewModelScoped
    fun provideGetStoriesUseCase(): GetStoriesUseCase {
        return GetStoriesUseCase()
    }

    @Provides
    @ViewModelScoped
    fun provideSignUpUseCase(authRepository: AuthRepository): SignUpUseCase {
        return SignUpUseCase(authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSignInUseCase(authRepository: AuthRepository): SignInUseCase {
        return SignInUseCase(authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAuthUseCase(authRepository: AuthRepository): GetAuthUseCase {
        return GetAuthUseCase(authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideCreateFeedUseCase(feedRepository: FeedRepository): CreateFeedUseCase {
        return CreateFeedUseCase(feedRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetFeedsUseCase(
        feedRepository: FeedRepository,
        authRepository: AuthRepository
    ): GetFeedsUseCase {
        return GetFeedsUseCase(feedRepository, authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetFeedUseCase(feedRepository: FeedRepository): GetFeedUseCase {
        return GetFeedUseCase(feedRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetUserUseCase(
        userRepository: UserRepository,
        authRepository: AuthRepository
    ): GetUserUseCase {
        return GetUserUseCase(userRepository, authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetRootCommentsUseCase(commentRepository: CommentRepository): GetRootCommentsUseCase {
        return GetRootCommentsUseCase(commentRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetChildCommentsUseCase(commentRepository: CommentRepository): GetChildCommentsUseCase {
        return GetChildCommentsUseCase(commentRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetCommentUseCase(commentRepository: CommentRepository): GetCommentUseCase {
        return GetCommentUseCase(commentRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideCreateCommentUseCase(commentRepository: CommentRepository): CreateCommentUseCase {
        return CreateCommentUseCase(commentRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideFavoriteFeedUseCase(feedRepository: FeedRepository): FavoriteFeedUseCase {
        return FavoriteFeedUseCase(feedRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetUsersUseCase(userRepository: UserRepository): SearchUsersUseCase {
        return SearchUsersUseCase(userRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideFollowUserUseCase(userRepository: UserRepository): FollowUserUseCase {
        return FollowUserUseCase(userRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideCreateChatUseCase(chatRepository: ChatRepository): CreateChatUseCase {
        return CreateChatUseCase(chatRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetChatsUseCase(chatRepository: ChatRepository): GetChatsUseCase {
        return GetChatsUseCase(chatRepository)
    }

    @Provides
    @ViewModelScoped
    fun getChatUseCase(
        chatRepository: ChatRepository,
        authLocalDataSource: AuthLocalDataSource
    ): GetChatUseCase {
        return GetChatUseCase(chatRepository, authLocalDataSource)
    }

    @Provides
    @ViewModelScoped
    fun provideSendMessageUseCase(messageRepository: MessageRepository): SendMessageUseCase {
        return SendMessageUseCase(messageRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetMessagesUseCase(messageRepository: MessageRepository): ObserveMessagesUseCase {
        return ObserveMessagesUseCase(messageRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetFollowersUseCase(
        userRepository: UserRepository,
        authRepository: AuthRepository
    ): GetFollowersUseCase {
        return GetFollowersUseCase(userRepository, authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetFollowingUseCase(
        userRepository: UserRepository,
        authRepository: AuthRepository
    ): GetFollowingUseCase {
        return GetFollowingUseCase(userRepository, authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideLoadRepliesUseCase(commentRepository: CommentRepository): LoadRepliesUseCase {
        return LoadRepliesUseCase(commentRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSignOutUseCase(authRepository: AuthRepository): SignOutUseCase {
        return SignOutUseCase(authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateUserUsecase(userRepository: UserRepository): UpdateUserUseCase {
        return UpdateUserUseCase(userRepository)
    }

    @Provides
    @ViewModelScoped
    fun getTrendingUseCase(trendingRepository: TrendingRepository): GetTrendingUseCase {
        return GetTrendingUseCase(trendingRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSearchFeedsUseCase(feedRepository: FeedRepository): SearchFeedsUseCase {
        return SearchFeedsUseCase(feedRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetNotificationsUseCase(notificationRepository: NotificationRepository): GetNotificationUsecase {
        return GetNotificationUsecase(notificationRepository)
    }
}
