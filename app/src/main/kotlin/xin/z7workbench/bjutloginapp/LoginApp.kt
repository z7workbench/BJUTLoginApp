package xin.z7workbench.bjutloginapp

import android.app.Application
import android.content.*
import android.content.res.Configuration
import android.content.res.Resources
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.room.Room
import xin.z7workbench.bjutloginapp.database.AppDatabase
import xin.z7workbench.bjutloginapp.util.LocaleUtil

/**
 * Created by ZeroGo on 2017/11/2.
 */
class LoginApp : Application() {
    lateinit var appDatabase: AppDatabase
    lateinit var res: Resources
    lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        prefs = getDefaultSharedPreferences(this)
        res = resources
        appDatabase = Room.databaseBuilder(this, AppDatabase::class.java, "login.db").allowMainThreadQueries().build()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtil.wrap(base))
    }
}