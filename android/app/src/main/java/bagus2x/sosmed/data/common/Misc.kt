package bagus2x.sosmed.data.common

import bagus2x.sosmed.BuildConfig
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object Misc {
    const val HTTP_BASE_URL = BuildConfig.HTTP_BASE_URL
    const val WS_BASE_URL = BuildConfig.WS_BASE_URL

    fun epochMillisToLocalDate(millis: Long): LocalDateTime {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }
}
