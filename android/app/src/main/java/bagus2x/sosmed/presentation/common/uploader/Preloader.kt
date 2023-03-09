package bagus2x.sosmed.presentation.common.uploader

import android.content.Context
import coil.imageLoader
import coil.request.ImageRequest

interface Preloader {

    fun preload(url: String)
}

class PreloaderImpl(
    private val context: Context
) : Preloader {
    override fun preload(url: String) {
        val request = ImageRequest.Builder(context)
            .data(url)
            .build()
        context.imageLoader.enqueue(request)
    }
}
