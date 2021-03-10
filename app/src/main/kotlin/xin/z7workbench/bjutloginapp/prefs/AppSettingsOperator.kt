package xin.z7workbench.bjutloginapp.prefs

import android.content.Context
import android.content.res.Resources
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.util.IpMode
import xin.z7workbench.bjutloginapp.util.NetFramework
import xin.z7workbench.bjutloginapp.util.dataStore
import java.io.IOException

object AppSettingsOperator {
    fun userSettings(dataStore: DataStore<Preferences>) = dataStore.data
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

    fun appSettings(dataStore: DataStore<Preferences>, res: Resources) = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else throw it
        }
        .map {
            val theme = it[Keys.THEME_IDX]
                ?: res.getStringArray(R.array.theme_index).first()
            val lang = it[Keys.LANGUAGE]
                ?: res.getStringArray(R.array.language_values).first()
            val net = NetFramework.valueOf(it[Keys.NET_FRAMEWORK] ?: NetFramework.OKHTTP.name)
            AppSettings(theme, lang, net)
        }

    suspend fun setCurrentId(dataStore: DataStore<Preferences>, current: Int) = setValue(dataStore, Keys.CURRENT, current)
    suspend fun setIpMode(dataStore: DataStore<Preferences>, ipMode: IpMode) = setValue(dataStore, Keys.IP_MODE, ipMode.name)
    suspend fun setLanguage(dataStore: DataStore<Preferences>, language: String) = setValue(dataStore, Keys.LANGUAGE, language)
    suspend fun setTheme(dataStore: DataStore<Preferences>, theme: String) = setValue(dataStore, Keys.THEME_IDX, theme)
    suspend fun setNetFramework(dataStore: DataStore<Preferences>, net: NetFramework) = setValue(dataStore, Keys.NET_FRAMEWORK, net.name)

    private suspend inline fun <reified T> setValue(dataStore: DataStore<Preferences>, key: Preferences.Key<T>, value: T) {
        dataStore.edit {
            it[key] = value
        }
    }

}