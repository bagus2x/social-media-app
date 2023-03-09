package bagus2x.sosmed.domain.model

sealed class Media {
    data class Image(
        val imageUrl: String,
        val unseen: Boolean = false
    ) : Media()

    data class Video(
        val videoUrl: String,
        val thumbnailUrl: String,
        val unseen: Boolean = false
    ) : Media()
}
