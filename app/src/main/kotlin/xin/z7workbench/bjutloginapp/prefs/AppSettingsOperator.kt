package xin.z7workbench.bjutloginapp.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import xin.z7workbench.bjutloginapp.Constants
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.util.IpMode
import xin.z7workbench.bjutloginapp.util.NetFramework
import java.io.IOException

class AppSettingsOperator private constructor(val context: Context) {
    private val dataStore: DataStore<Preferences> =
            context.createDataStore(Constants.DATASTORE_NAME)

    val userSettings = dataStore.data
            .catch {
                if (it is IOException) {
                    emit(emptyPreferences())
                } else throw it
            }
            .map {
                val current = it[Keys.CURRENT] ?: -1
                val ipMode = IpMode.valueOf(it[Keys.IP_MODE] ?: IpMode.WIRELESS.name)
                UserSettings(current, ipMode)
            }

    val appSettings = dataStore.data
            .catch {
                if (it is IOException) {
                    emit(emptyPreferences())
                } else throw it
            }
            .map {
                val theme = it[Keys.THEME_IDX]
                        ?: context.resources.getStringArray(R.array.theme_index).first()
                val lang = it[Keys.LANGUAGE]
                        ?: context.resources.getStringArray(R.array.language_values).first()
                val net = NetFramework.valueOf(it[Keys.NET_FRAMEWORK] ?: NetFramework.OKHTTP.name)
                AppSettings(theme, lang, net)
            }

    suspend fun setCurrentId(current: Int) = setValue(Keys.CURRENT, current)
    suspend fun setIpMode(ipMode: IpMode) = setValue(Keys.IP_MODE, ipMode.name)
    suspend fun setLanguage(language: String) = setValue(Keys.LANGUAGE, language)
    suspend fun setTheme(theme: String) = setValue(Keys.THEME_IDX, theme)
    suspend fun setNetFramework(net: NetFramework) = setValue(Keys.NET_FRAMEWORK, net.name)

    private suspend inline fun <reified T> setValue(key: Preferences.Key<T>, value: T) {
        dataStore.edit {
            it[key] = value
        }
    }

    companion object {
        private var operator: AppSettingsOperator? = null
        fun instant(context: Context) = if (operator == null) {
            AppSettingsOperator(context)
        } else operator!!
    }
}