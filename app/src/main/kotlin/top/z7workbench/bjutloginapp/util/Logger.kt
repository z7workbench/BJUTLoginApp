package top.z7workbench.bjutloginapp.util

import top.z7workbench.bjutloginapp.model.Info

object Logger {
    private val _logs = mutableListOf<Info>()
    val logs: List<Info>
        get() = _logs

    fun f(where: String, message: String) {
        val time = System.currentTimeMillis()
//        _logs.add(Info(time, where, message, WarningLevel.FATAL))
    }

    fun w(where: String, message: String) {
        val time = System.currentTimeMillis()
//        _logs.add(Info(time, where, message, WarningLevel.WARNING))
    }

    fun i(where: String, message: String) {
        val time = System.currentTimeMillis()
//        _logs.add(Info(time, where, message, WarningLevel.INFO))
    }
}