package bagus2x.sosmed.presentation.common

import android.text.format.DateUtils
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object Misc {
    private val DateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    fun getAvatar(seed: String): String {
        return "https://api.dicebear.com/5.x/fun-emoji/jpg?seed=$seed"
    }

    fun getIcon(seed: String): String {
        return "https://api.dicebear.com/5.x/shapes/jpg?seed=$seed"
    }

    fun formatRelative(date: LocalDateTime): String {
        val time = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val now = System.currentTimeMillis()
        return DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS).toString()
    }

    fun formatDate(date: LocalDateTime): String {
        return DateFormatter.format(date)
    }

    fun getRandomFileName(): String {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss", Locale.US)
        return "Sosmed_${dateTimeFormatter.format(LocalDateTime.now())}"
    }
}
