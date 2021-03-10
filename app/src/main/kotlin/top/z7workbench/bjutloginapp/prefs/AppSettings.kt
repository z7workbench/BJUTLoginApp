package top.z7workbench.bjutloginapp.prefs

import top.z7workbench.bjutloginapp.util.NetFramework

data class AppSettings(
        val themeIndex: String,
        val languageIndex: String,
        val networkFramework: NetFramework
)