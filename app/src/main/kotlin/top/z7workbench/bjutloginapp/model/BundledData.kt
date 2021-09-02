package top.z7workbench.bjutloginapp.model

import androidx.room.ColumnInfo
import top.z7workbench.bjutloginapp.util.NetworkState

data class BundledState(
    val state: NetworkState = NetworkState.OTHER,
    val useVPN: Boolean = false,
    val validate: Boolean = false
)

data class BundledSyncData(
    val status: Boolean = false,
    val data: NetData = NetData()
)

data class BundledUser(
    @ColumnInfo(name = "id")
    val id: Int = -1,
    @ColumnInfo(name = "name")
    val name: String = ""
)