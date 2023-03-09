package bagus2x.sosmed.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "key")
data class KeyEntity(
    @PrimaryKey
    @ColumnInfo("label")
    val label: String,
    @ColumnInfo("prev_key")
    val prevKey: Long? = null,
    @ColumnInfo("next_key")
    val nextKey: Long?
)
