package xin.z7workbench.bjutloginapp.prefs

import androidx.datastore.preferences.core.preferencesKey

object Keys {
    val THEME_IDX = preferencesKey<String>("theme_index")
    val LANGUAGE = preferencesKey<String>("language")
    val IP_MODE = preferencesKey<String>("ip_mode")
    val CURRENT = preferencesKey<Int>("current_user")
    val NET_FRAMEWORK = preferencesKey<String>("net_framework")
}