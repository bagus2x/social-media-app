package bagus2x.sosmed.data.common

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object Misc {

    fun epochMillisToLocalDate(millis: Long): LocalDateTime {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }
}
