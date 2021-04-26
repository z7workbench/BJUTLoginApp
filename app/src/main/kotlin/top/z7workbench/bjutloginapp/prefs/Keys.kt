package top.z7workbench.bjutloginapp.prefs

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Keys {
    val THEME_IDX = intPreferencesKey("theme_index")
    val LANGUAGE = intPreferencesKey("language")
    val IP_MODE = stringPreferencesKey("ip_mode")
    val CURRENT = intPreferencesKey("current_user")
    val NET_FRAMEWORK = stringPreferencesKey("net_framework")
}