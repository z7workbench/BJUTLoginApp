package top.z7workbench.bjutloginapp.util

import top.z7workbench.bjutloginapp.model.NetData

data class BundledState (
    val state:NetworkState = NetworkState.OTHER,
    val useVPN:Boolean = false,
    val validate: Boolean = false
)

data class BundledSyncData  (
    val status: Boolean = false,
    val data: NetData = NetData()
)