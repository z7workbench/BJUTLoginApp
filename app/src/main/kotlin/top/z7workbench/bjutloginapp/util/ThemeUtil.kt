package top.z7workbench.bjutloginapp.util

import android.content.Context
import androidx.core.content.edit
import top.z7workbench.bjutloginapp.R

object ThemeUtil {
    fun setCurrentTheme(context: Context) {
        when (context.defaultSharedPreferences.getInt("theme_index", -1)) {
            0 -> context.setTheme(R.style.AppTheme_Dark_ZeroGoPurple)
            1 -> context.setTheme(R.style.AppTheme_Light_White)
            2 -> context.setTheme(R.style.AppTheme_Light_Amber)
            3 -> context.setTheme(R.style.AppTheme_Light_Yun)
            4 -> context.setTheme(R.style.AppTheme_Dark_Pure)
            5 -> context.setTheme(R.style.AppTheme_Dark_Teal)
            6 -> context.setTheme(R.style.AppTheme_Dark_Pink)
            else -> {
                context.defaultSharedPreferences.edit { putInt("theme_index", 0).apply() }
                context.setTheme(R.style.AppTheme_Dark_ZeroGoPurple)
            }
        }
    }
}