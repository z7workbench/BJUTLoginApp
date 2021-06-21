package top.z7workbench.bjutloginapp

import android.app.Application
import android.content.*
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.os.Build
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.room.Room
import top.z7workbench.bjutloginapp.database.AppDatabase
import top.z7workbench.bjutloginapp.network.NetworkGlobalObject
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
    val dataStore: DataStore<Preferences>
        get() = _dataStore
    val res: Resources by lazy { resources }
    lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        _appDatabase = Room.databaseBuilder(this, AppDatabase::class.java, "login.db")
            .allowMainThreadQueries()
            .build()
        prefs = getDefaultSharedPreferences(this)
        if (prefs.getInt("theme_index", -1) < 0) {
            prefs.edit { putInt("theme_index", 0) }
        }
        if (prefs.getInt("language", -1) < 0) {
            prefs.edit { putInt("language", 0) }
        }
        _dataStore = applicationContext.dataStore
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtil.wrap(base))
    }
}