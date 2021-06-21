package top.z7workbench.bjutloginapp.util

import top.z7workbench.bjutloginapp.R
import java.io.Serializable

/**
 * Created by ZeroGo on 2017/11/6.
 */
enum class LogStatus(val description: Int) : Serializable {
    OFFLINE(R.string.status_offline),
    LOGGING(R.string.status_logging),
    SYNCING(R.string.status_syncing),
    ERROR(R.string.status_error),
    ONLINE(R.string.status_online)
}

enum class IpMode : Serializable {
    WIRED_IPV4, WIRED_IPV6, WIRED_BOTH, WIRELESS;
}

enum class WarningLevel(val info: String) : Serializable {
    FATAL("[F]"), WARNING("[W]"), INFO("[I]")
}

enum class NetFramework : Serializable {
    OKHTTP, VOLLEY, RETROFIT
}

enum class NetworkState : Serializable {
    CELLULAR, WIFI, OTHER
}