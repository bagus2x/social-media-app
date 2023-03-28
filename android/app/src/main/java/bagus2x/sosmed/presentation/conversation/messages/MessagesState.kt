package bagus2x.sosmed.presentation.conversation.messages

import bagus2x.sosmed.domain.model.Chat
import bagus2x.sosmed.domain.model.Profile
import bagus2x.sosmed.presentation.common.media.DeviceMedia

data class ChatState(
    val chat: Chat? = null,
    val loading: Boolean = false,
)

data class MessageState(
    val description: String = "",
    val medias: List<DeviceMedia> = emptyList(),
    val loading: Boolean = false,
) {
    val isFulfilled: Boolean
        get() = description.isNotBlank() || medias.isNotEmpty()
}

data class ProfileState(
    val profile: Profile? = null,
    val loading: Boolean = false
)

data class MessagesState(
    val chatState: ChatState = ChatState(),
    val messageState: MessageState = MessageState(),
    val profileState: ProfileState = ProfileState(),
    val snackbar: String = ""
)
