package top.z7workbench.bjutloginapp.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.os.LocaleList
import androidx.core.content.edit
import top.z7workbench.bjutloginapp.Constants
import java.util.*

object LocaleUtil {
    private fun isAutoLanguageChanged(context: Context): Boolean {
        val appLocales = context.resources.configuration.locales
        val sysLocales = Resources.getSystem().configuration.locales
        return if (context.defaultSharedPreferences.getInt("language", 0) != 0) {
            appLocales != sysLocales
        } else false
    }

    fun wrap(context: Context): ContextWrapper {
        val config = context.resources.configuration
        var type = context.defaultSharedPreferences.getInt("language", -1)
        if (type < 0) {
            context.defaultSharedPreferences.edit { putInt("language", 0) }
            type = 0
        }
        val locales = when (type) {
            0 -> Resources.getSystem().configuration.locales
            1 -> LocaleList(Locale.SIMPLIFIED_CHINESE)
            2 -> LocaleList(Locale.US)
            3 -> LocaleList(Locale.TRADITIONAL_CHINESE)
            else -> {
                context.defaultSharedPreferences.edit { putInt("language", 0).apply() }
                context.resources.configuration.locales
            }
        }

        config.setLocales(locales)
        config.setLayoutDirection(locales[0])
        val newContext = context.createConfigurationContext(config)
        return ContextWrapper(newContext)
    }
}