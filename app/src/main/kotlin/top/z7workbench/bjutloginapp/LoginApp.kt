package top.z7workbench.bjutloginapp

import android.app.Application
import android.content.*
import android.content.res.Resources
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.room.Room
import top.z7workbench.bjutloginapp.database.AppDatabase
import top.z7workbench.bjutloginapp.util.LocaleUtil
import top.z7workbench.bjutloginapp.util.dataStore

/**
 * Created by ZeroGo on 2017/11/2.
 */
class LoginApp : Application() {
    private lateinit var _appDatabase: AppDatabase
    val appDatabase: AppDatabase
        get() = _appDatabase
    private lateinit var _dataStore: DataStore<Preferences>
    val dataStore : DataStore<Preferences>
    get() = _dataStore
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
        _dataStore = applicationContext.dataStore
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtil.wrap(base))
    }
}