package top.z7workbench.bjutloginapp.model

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
    val currentID: Int = -1,
    val username: String = ""
)