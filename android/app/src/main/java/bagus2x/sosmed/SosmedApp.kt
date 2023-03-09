package bagus2x.sosmed

import android.app.Application
import android.content.ComponentName
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import bagus2x.sosmed.data.local.AuthLocalDataSource
import bagus2x.sosmed.data.remote.UserRemoteDataSource
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterNotNull
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltAndroidApp
class SosmedApp : Application(), ImageLoaderFactory {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    var customTabsSession: CustomTabsSession? = null
        private set

    @Inject
    lateinit var authLocalDataSource: AuthLocalDataSource

    @Inject
    lateinit var userRemoteDataSource: UserRemoteDataSource

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        setupCustomTabSession()
        setupFcmToken()
    }

    private fun setupCustomTabSession() {
        val mCustomTabsServiceConnection: CustomTabsServiceConnection =
            object : CustomTabsServiceConnection() {
                override fun onCustomTabsServiceConnected(
                    componentName: ComponentName,
                    customTabsClient: CustomTabsClient
                ) {
                    //Pre-warming
                    customTabsClient.warmup(0L)
                    customTabsClient.newSession(null)
                    customTabsSession = customTabsClient.newSession(null)
                }

                override fun onServiceDisconnected(name: ComponentName) {

                }
            }
        CustomTabsClient.bindCustomTabsService(
            this,
            "com.android.chrome",
            mCustomTabsServiceConnection
        )
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .error(R.drawable.placeholder_error)
            .fetcherDispatcher(Dispatchers.IO)
            .logger(if (BuildConfig.DEBUG) DebugLogger() else null)
            .decoderDispatcher(Dispatchers.Default)
            .transformationDispatcher(Dispatchers.IO)
            .interceptorDispatcher(Dispatchers.Default)
            .transformationDispatcher(Dispatchers.Default)
            .crossfade(1000)
            .crossfade(true)
            .build()
    }

    private fun setupFcmToken() {
        applicationScope.launch {
            withContext(Dispatchers.IO) {
                authLocalDataSource.getAuth().filterNotNull().collect {
                    try {
                        userRemoteDataSource.updateFcmToken(token = getToken())
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        applicationScope.cancel()
    }
}

private suspend fun getToken() = suspendCancellableCoroutine<String> { cont ->
    Firebase.messaging.token
        .addOnSuccessListener { cont.resume(it) }
        .addOnFailureListener { cont.resumeWithException(it) }
}
