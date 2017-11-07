package party.iobserver.bjutloginapp.util

import party.iobserver.bjutloginapp.R

/**
 * Created by ZeroGo on 2017/11/6.
 */
enum class LogStatus(val description: Int) {
    OFFLINE(R.string.status_offline),
    SYNCING(R.string.status_syncing),
    ERROR(R.string.status_error),
    ONLINE(R.string.status_online)
}