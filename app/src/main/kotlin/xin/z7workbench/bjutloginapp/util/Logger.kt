package xin.z7workbench.bjutloginapp.util

import java.text.SimpleDateFormat
import java.util.*

object Logger {
    private val _logs = mutableListOf<Info>()
    val logs: List<Info>
        get() = _logs

    fun f(where: String, message: String) {
        val time = System.currentTimeMillis()
        _logs.add(Info(time, where, message, WarningLevel.FATAL))
    }

    fun w(where: String, message: String) {
        val time = System.currentTimeMillis()
        _logs.add(Info(time, where, message, WarningLevel.WARNING))
    }

    fun i(where: String, message: String) {
        val time = System.currentTimeMillis()
        _logs.add(Info(time, where, message, WarningLevel.INFO))
    }

    data class Info(
            val time: Long,
            val where: String,
            val message: String,
            val level: WarningLevel
    ) {
        private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        override fun toString() = level.info.buildString(
                sdf.format(Date(time)),
                ": in(",
                where,
                ")-{",
                message,
                "}"
        )
    }
}