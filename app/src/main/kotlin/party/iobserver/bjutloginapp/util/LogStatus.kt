package party.iobserver.bjutloginapp.util

/**
 * Created by ZeroGo on 2017/11/6.
 */
enum class LogStatus(val description: String) {
    OFFLINE("Offline"),
    SYNCING("Syncing"),
    ERROR("Error"),
    ONLINE("Online")
}