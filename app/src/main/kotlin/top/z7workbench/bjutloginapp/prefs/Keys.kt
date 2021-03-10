package top.z7workbench.bjutloginapp.prefs

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Keys {
    val THEME_IDX = stringPreferencesKey("theme_index")
    val LANGUAGE = stringPreferencesKey("language")
    val IP_MODE = stringPreferencesKey("ip_mode")
    val CURRENT = intPreferencesKey("current_user")
    val NET_FRAMEWORK = stringPreferencesKey("net_framework")
}