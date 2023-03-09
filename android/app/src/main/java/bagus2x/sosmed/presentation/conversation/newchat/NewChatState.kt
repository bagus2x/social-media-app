package bagus2x.sosmed.presentation.conversation.newchat

import bagus2x.sosmed.domain.model.Chat
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.presentation.common.media.DeviceMedia

data class NewChatState(
    val name: String = "",
    val photo: DeviceMedia.Image? = null,
    val members: Set<User> = emptySet(),
    val authUser: User? = null,
    val loading: Boolean = false,
    val createdChat: Chat? = null,
    val snackbar: String = ""
) {
    val isPrivateChat: Boolean
        get() = members.isEmpty()

    val isGroupChat: Boolean
        get() = !isPrivateChat

    val membersWithoutAuthUser: Set<User>
        get() = members.filter { it.id != authUser?.id }.toSet()

    val isFulfilled: Boolean
        get() = membersWithoutAuthUser.isNotEmpty() && name.isNotBlank()
}
