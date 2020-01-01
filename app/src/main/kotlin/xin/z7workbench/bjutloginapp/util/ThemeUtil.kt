package xin.z7workbench.bjutloginapp.util

import android.content.Context
import org.jetbrains.anko.defaultSharedPreferences
import xin.z7workbench.bjutloginapp.R

object ThemeUtil {
    fun setCurrentTheme(context: Context) {
        val themeIndexes = context.resources.getStringArray(R.array.theme_index)
        when (context.defaultSharedPreferences.getString("theme_index", null)) {
            themeIndexes[0] -> context.setTheme(R.style.AppTheme_Dark_ZeroGoPurple)
            themeIndexes[1] -> context.setTheme(R.style.AppTheme_Light_White)
            themeIndexes[2] -> context.setTheme(R.style.AppTheme_Light_Amber)
            themeIndexes[3] -> context.setTheme(R.style.AppTheme_Dark_Pure)
            themeIndexes[4] -> context.setTheme(R.style.AppTheme_Dark_Teal)
            else -> {
                context.defaultSharedPreferences.edit().putString("theme_index", "ZGP").apply()
                context.setTheme(R.style.AppTheme_Dark_ZeroGoPurple)
            }
        }
    }
}