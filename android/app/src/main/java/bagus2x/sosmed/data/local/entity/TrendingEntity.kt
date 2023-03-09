package bagus2x.sosmed.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import bagus2x.sosmed.domain.model.Trending

@Entity("trending")
data class TrendingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long = 0,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("type")
    val type: String,
    @ColumnInfo("country")
    val country: String?,
    @ColumnInfo("count")
    val count: Int
) {

    fun asDomainModel(): Trending {
        return Trending(
            id = id,
            name = name,
            type = type,
            country = country,
            count = count
        )
    }
}
