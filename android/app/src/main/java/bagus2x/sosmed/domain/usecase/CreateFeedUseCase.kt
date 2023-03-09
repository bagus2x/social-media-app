package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.repository.FeedRepository

class CreateFeedUseCase(
    private val feedRepository: FeedRepository
) {

    suspend operator fun invoke(description: String, medias: List<Media>, language: String?): Feed {
        return feedRepository.save(description, medias, language)
    }
}
