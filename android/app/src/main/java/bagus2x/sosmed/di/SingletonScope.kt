package bagus2x.sosmed.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import bagus2x.sosmed.data.*
import bagus2x.sosmed.data.common.HttpClient
import bagus2x.sosmed.data.local.AuthLocalDataSource
import bagus2x.sosmed.data.local.ChatLocalDataSource
import bagus2x.sosmed.data.local.MessageLocalDataSource
import bagus2x.sosmed.data.local.SosmedDatabase
import bagus2x.sosmed.data.local.entity.TrendingLocalDataSource
import bagus2x.sosmed.data.remote.*
import bagus2x.sosmed.domain.repository.*
import bagus2x.sosmed.presentation.common.connectivity.NetworkTracker
import bagus2x.sosmed.presentation.common.connectivity.NetworkTrackerImpl
import bagus2x.sosmed.presentation.common.contact.ContactManager
import bagus2x.sosmed.presentation.common.contact.ContactManagerImpl
import bagus2x.sosmed.presentation.common.media.DeviceAlbumManager
import bagus2x.sosmed.presentation.common.media.DeviceAlbumManagerImpl
import bagus2x.sosmed.presentation.common.media.DeviceMediaManager
import bagus2x.sosmed.presentation.common.media.DeviceMediaManagerImpl
import bagus2x.sosmed.presentation.common.translation.Translator
import bagus2x.sosmed.presentation.common.translation.TranslatorImpl
import bagus2x.sosmed.presentation.common.uploader.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonScope {

    @Provides
    @Singleton
    fun provideDeviceMediaManager(
        @ApplicationContext
        context: Context
    ): DeviceMediaManager {
        return DeviceMediaManagerImpl(context, Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun provideDeviceAlbumManager(
        @ApplicationContext
        context: Context,
        deviceMediaManager: DeviceMediaManager
    ): DeviceAlbumManager {
        return DeviceAlbumManagerImpl(context, Dispatchers.IO, deviceMediaManager)
    }

    @Provides
    @Singleton
    fun provideContactManager(
        @ApplicationContext
        context: Context,
    ): ContactManager {
        return ContactManagerImpl(context = context, dispatcher = Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun provideFileUploadManager(
        client: HttpClient,
        @ApplicationContext
        context: Context
    ): FileUploader {
        return FileUploaderImpl(Dispatchers.IO, client, context)
    }

    @Provides
    @Singleton
    fun provideHttpClient(authLocalDataSource: AuthLocalDataSource): HttpClient {
        return HttpClient(authLocalDataSource)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SosmedDatabase {
        return Room
            .databaseBuilder(context, SosmedDatabase::class.java, "sosmed")
            .build()
    }

    @Provides
    @Singleton
    fun provideChatLocalDataSource(database: SosmedDatabase): ChatLocalDataSource {
        return database.chatLocalDataSource
    }

    @Provides
    @Singleton
    fun provideMessageLocalDataSource(database: SosmedDatabase): MessageLocalDataSource {
        return database.messageLocalDataSource
    }

    @Provides
    @Singleton
    fun provideFeedRepository(database: SosmedDatabase, httpClient: HttpClient): FeedRepository {
        return FeedRepositoryImpl(
            dispatcher = Dispatchers.IO,
            keyLocalDataSource = database.keyLocalDataSource,
            feedLocalDataSource = database.feedLocalDataSource,
            feedRemoteDataSource = FeedRemoteDataSource(httpClient),
            database = database
        )
    }

    @Provides
    @Singleton
    fun provideAuthLocalDataSource(@ApplicationContext context: Context): AuthLocalDataSource {
        return AuthLocalDataSource(dataStore = context.authStore)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authLocalDataSource: AuthLocalDataSource,
        client: HttpClient,
        database: SosmedDatabase
    ): AuthRepository {
        return AuthRepositoryImpl(
            dispatcher = Dispatchers.IO,
            authLocalDataSource = authLocalDataSource,
            authRemoteDataSource = AuthRemoteDataSource(client),
            database = database
        )
    }

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(client: HttpClient): UserRemoteDataSource {
        return UserRemoteDataSource(client)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        database: SosmedDatabase,
        userRemoteDataSource: UserRemoteDataSource,
        authLocalDataSource: AuthLocalDataSource,
    ): UserRepository {
        return UserRepositoryImpl(
            dispatcher = Dispatchers.IO,
            userLocalDataSource = database.userLocalDataSource,
            userRemoteDataSource = userRemoteDataSource,
            authLocalDataSource = authLocalDataSource
        )
    }

    @Provides
    @Singleton
    fun provideCommentRepository(
        database: SosmedDatabase,
        client: HttpClient
    ): CommentRepository {
        return CommentRepositoryImpl(
            dispatcher = Dispatchers.IO,
            keyLocalDataSource = database.keyLocalDataSource,
            commentLocalDataSource = database.commentLocalDataSource,
            commentRemoteDataSource = CommentRemoteDataSource(client),
            feedLocalDataSource = database.feedLocalDataSource,
            database = database
        )
    }

    @Provides
    @Singleton
    fun provideChatRepository(database: SosmedDatabase, client: HttpClient): ChatRepository {
        return ChatRepositoryImpl(
            keyLocalDataSource = database.keyLocalDataSource,
            chatLocalDataSource = database.chatLocalDataSource,
            chatRemoteDataSource = ChatRemoteDataSource(client),
            database = database
        )
    }

    @Provides
    @Singleton
    fun provideMessageRepository(database: SosmedDatabase, client: HttpClient): MessageRepository {
        return MessageRepositoryImpl(
            keyLocalDataSource = database.keyLocalDataSource,
            messageLocalDataSource = database.messageLocalDataSource,
            messageRemoteDataSource = MessageRemoteDataSource(client),
            database = database,
            dispatcher = Dispatchers.IO
        )
    }

    @Provides
    @Singleton
    fun provideNetworkTracker(@ApplicationContext context: Context): NetworkTracker {
        return NetworkTrackerImpl(context)
    }

    @Provides
    @Singleton
    fun provideTranslator(): Translator {
        return TranslatorImpl()
    }

    @Provides
    @Singleton
    fun provideTrendingLocalDataSource(sosmedDatabase: SosmedDatabase): TrendingLocalDataSource {
        return sosmedDatabase.trendingLocalDataSource
    }

    @Provides
    @Singleton
    fun provideTrendingRemoteDataSource(client: HttpClient): TrendingRemoteDataSource {
        return TrendingRemoteDataSource(client)
    }

    @Provides
    @Singleton
    fun provideTrendingRepository(
        trendingLocalDataSource: TrendingLocalDataSource,
        trendingRemoteDataSource: TrendingRemoteDataSource,
        sosmedDatabase: SosmedDatabase
    ): TrendingRepository {
        return TrendingRepositoryImpl(trendingLocalDataSource, trendingRemoteDataSource, sosmedDatabase)
    }
}

private val Context.authStore by preferencesDataStore("auth")
