package party.iobserver.bjutloginapp.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by ZeroGo on 2017/8/20.
 */

@Entity(tableName = "user")
data class User(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        var name: String = "",
        var password: String = "",
        var pack: Int = 8)