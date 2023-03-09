package bagus2x.sosmed.presentation.firebase

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import bagus2x.sosmed.R
import bagus2x.sosmed.data.local.AuthLocalDataSource
import bagus2x.sosmed.data.local.ChatLocalDataSource
import bagus2x.sosmed.data.remote.UserRemoteDataSource
import bagus2x.sosmed.data.remote.dto.ChatDTO
import bagus2x.sosmed.presentation.common.Misc
import coil.imageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AppFirebaseMessagingService : FirebaseMessagingService() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    @Inject
    lateinit var authLocalDataSource: AuthLocalDataSource

    @Inject
    lateinit var userRemoteDataSource: UserRemoteDataSource

    @Inject
    lateinit var chatLocalDataSource: ChatLocalDataSource

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        scope.launch(Dispatchers.IO) {
            authLocalDataSource.getAuth().filterNotNull().collect {
                try {
                    userRemoteDataSource.updateFcmToken(token)
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        receiveNewChatMessage(message)
    }

    private fun receiveNewChatMessage(message: RemoteMessage) {
        val data = message.data
        if (data.isNotEmpty()) {
            if (data["type"] == "receive_new_chat_message") {
                // Store latest chat in local database
                val content = data["content"] ?: return
                val userId = data["user_id"]?.toLong() ?: return
                scope.launch(Dispatchers.IO) {
                    val authUser = authLocalDataSource.getAuth().filterNotNull().first()
                    if (authUser.profile.id == userId) {
                        val chatDTO = Json.decodeFromString<ChatDTO>(content)
                        chatLocalDataSource.save(chatDTO.asEntity())
                    }
                }
            }
        }
    }

    private suspend fun showChatNotification(chatDTO: ChatDTO) {
        createNotificationChannel(
            id = CHANNEL_ID_NEW_MESSAGE,
            name = getString(R.string.channel_new_message),
            description = getString(R.string.channel_description_new_message),
        )

        withContext(Dispatchers.IO) {
            val builder = NotificationCompat
                .Builder(this@AppFirebaseMessagingService, CHANNEL_ID_NEW_MESSAGE)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(loadImage(chatDTO.photo))
                .setContentTitle(chatDTO.title)
                .setContentText(chatDTO.description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            if (ActivityCompat.checkSelfPermission(
                    this@AppFirebaseMessagingService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@withContext
            }

            NotificationManagerCompat
                .from(this@AppFirebaseMessagingService)
                .notify(chatDTO.id.toInt(), builder.build())
        }
    }

    private suspend fun loadImage(url: String): Bitmap? {
        val loader = application.imageLoader
        val request = ImageRequest.Builder(application).data(url).build()
        return when (val result = loader.execute(request)) {
            is SuccessResult -> {
                (result.drawable as BitmapDrawable).bitmap
            }
            is ErrorResult -> {
                Timber.e(result.throwable, "Failed to load image")
                null
            }
        }
    }

    private fun createNotificationChannel(
        id: String,
        name: String,
        description: String,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, name, importance).apply {
                this.description = description
            }
            // Register the channel with the system
            val notificationManager = getSystemService<NotificationManager>()
            notificationManager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        private const val CHANNEL_ID_NEW_MESSAGE = "new_message"
    }
}

private val ChatDTO.id: Long
    get() = when (this) {
        is ChatDTO.Group -> id
        is ChatDTO.Private -> id
    }

private val ChatDTO.title: String
    get() = when (this) {
        is ChatDTO.Group -> name.ifBlank { members.joinToString(", ") { it.username } }
        is ChatDTO.Private -> pair.username
    }

private val ChatDTO.description: String?
    get() = when (this) {
        is ChatDTO.Group -> recentMessages.getOrNull(0)?.description
        is ChatDTO.Private -> recentMessages.getOrNull(0)?.description
    }

private val ChatDTO.photo: String
    get() = when (this) {
        is ChatDTO.Group -> photo ?: Misc.getIcon("group-$id")
        is ChatDTO.Private -> pair.photo ?: Misc.getIcon(pair.name)
    }
