package top.z7workbench.bjutloginapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import top.z7workbench.bjutloginapp.util.WarningLevel
import top.z7workbench.bjutloginapp.util.buildString
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "info")
data class Info(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        var time: Long = 0L,
        var place: String = "",
        var message: String = "",
        var level: WarningLevel = WarningLevel.INFO
) {
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    override fun toString() = level.info.buildString(
            sdf.format(Date(time)),
            ": in(",
            place,
            ")-{",
            message,
            "}"
    )
}

class LevelConverter {
    @TypeConverter
    fun revertLevel(value: String) = WarningLevel.valueOf(value)
    @TypeConverter
    fun convertLevel(level: WarningLevel) = level.name
}