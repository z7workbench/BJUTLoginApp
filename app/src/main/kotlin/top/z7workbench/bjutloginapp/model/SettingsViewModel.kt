package top.z7workbench.bjutloginapp.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import top.z7workbench.bjutloginapp.R

class SettingsViewModel(app: Application): AndroidViewModel(app) {
    val themeIndies = app.resources.getStringArray(R.array.theme_index)
    val langIndies = app.resources.getStringArray(R.array.language_values)
}