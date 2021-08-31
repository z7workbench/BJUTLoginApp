package top.z7workbench.bjutloginapp.util

data class BundledState (
    val state:NetworkState = NetworkState.OTHER,
    val useVPN:Boolean = false,
    val validate: Boolean = false
)