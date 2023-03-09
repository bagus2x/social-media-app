package bagus2x.sosmed.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import bagus2x.sosmed.data.local.entity.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Database(
    version = 1,
    entities = [
        KeyEntity::class,
        UserEntity::class,
        FeedEntity::class,
        CommentEntity::class,
        ChatEntity::class,
        MessageEntity::class,
        TrendingEntity::class
    ]
)
@TypeConverters(Converters::class)
abstract class SosmedDatabase : RoomDatabase() {
    abstract val keyLocalDataSource: KeyLocalDataSource
    abstract val feedLocalDataSource: FeedLocalDataSource
    abstract val commentLocalDataSource: CommentLocalDataSource
    abstract val userLocalDataSource: UserLocalDataSource
    abstract val chatLocalDataSource: ChatLocalDataSource
    abstract val messageLocalDataSource: MessageLocalDataSource
    abstract val trendingLocalDataSource: TrendingLocalDataSource
}

class Converters {

    @TypeConverter
    fun fromMessagesToString(value: List<MessageEntity>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun fromStringToMessages(value: String): List<MessageEntity> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromProfilesToString(value: List<ProfileEntity>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun fromStringToProfiles(value: String): List<ProfileEntity> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromMediasToString(value: List<MediaEntity>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun fromStringToMedias(value: String): List<MediaEntity> {
        return Json.decodeFromString(value)
    }
}
