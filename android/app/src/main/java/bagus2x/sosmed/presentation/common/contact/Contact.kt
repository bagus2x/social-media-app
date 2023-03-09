package bagus2x.sosmed.presentation.common.contact

data class Contact(
    val id: Long,
    val name: String,
    val phoneNumbers: List<String> = emptyList()
)
