package party.iobserver.bjutloginapp

import android.app.Application
import android.arch.persistence.room.Room
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import party.iobserver.bjutloginapp.database.AppDatabase
import java.util.*


/**
 * Created by ZeroGo on 2017/11/2.
 */
class LoginApp : Application() {
    lateinit var appDatabase: AppDatabase
    lateinit var res: Resources
    lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        res = resources
        appDatabase = Room.databaseBuilder(this, AppDatabase::class.java, "login.db").allowMainThreadQueries().build()

        val dm = resources.displayMetrics
        val config = resources.configuration
        when (prefs.getString("language", "")) {
            "0" -> config.locale = Locale.getDefault()
            "1" -> config.locale = Locale.SIMPLIFIED_CHINESE
            "2" -> config.locale = Locale.ENGLISH
            "" -> {
                prefs.edit().putString("language", "0").apply()
                config.locale = Locale.getDefault()
            }
        }
        resources.updateConfiguration(config, dm)
    }
}