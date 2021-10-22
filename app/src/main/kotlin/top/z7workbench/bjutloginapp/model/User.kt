package top.z7workbench.bjutloginapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by ZeroGo on 2017/8/20.
 */

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var password: String = "",
    var pack: Int = 30,
    @ColumnInfo(defaultValue = "")
    var secret: String = ""
//        ,
//        var secret: String = ""
) {
    fun asBundledUser() = BundledUser(id, name)
}