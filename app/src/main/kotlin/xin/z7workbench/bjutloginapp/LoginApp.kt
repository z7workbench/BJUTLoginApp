package xin.z7workbench.bjutloginapp

import android.app.Application
import android.content.*
import android.content.res.Resources
import androidx.core.content.edit
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.room.Room
import xin.z7workbench.bjutloginapp.database.AppDatabase
import xin.z7workbench.bjutloginapp.prefs.AppSettingsOperator
import xin.z7workbench.bjutloginapp.util.LocaleUtil

/**
 * Created by ZeroGo on 2017/11/2.
 */
class LoginApp : Application() {
    private lateinit var _appDatabase: AppDatabase
    val appDatabase: AppDatabase
        get() = _appDatabase
    val operator: AppSettingsOperator
        get() = AppSettingsOperator.instant(applicationContext)
    val res: Resources by lazy { resources }
    lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        _appDatabase = Room.databaseBuilder(this, AppDatabase::class.java, "login.db")
                .allowMainThreadQueries()
                .build()
        prefs = getDefaultSharedPreferences(this)
        if (prefs.getString("theme_index", null).isNullOrEmpty()) {
            prefs.edit { putString("theme_index", "ZGP") }
        }
        if (prefs.getString("language", null).isNullOrEmpty()) {
            prefs.edit { putString("language", "Auto") }
        }
        if (prefs.getInt("ip_mode", -1) < 0) {
            prefs.edit { putInt("ip_mode", 0) }
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtil.wrap(base))
    }
}