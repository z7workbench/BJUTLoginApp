package xin.z7workbench.bjutloginapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import xin.z7workbench.bjutloginapp.BuildConfig
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.util.NetworkUtils
import xin.z7workbench.bjutloginapp.util.DataProcessBlock
import java.io.IOException

class SettingsFragment : PreferenceFragmentCompat() {
    private val themeIndex by lazy { prefs.getString("theme_index", null) ?: "ZGP" }
    private val language by lazy { prefs.getString("language", null) ?: "Auto" }
    private val prefs by lazy { (activity as BasicActivity).app.prefs }
    private val userDao by lazy { (activity as BasicActivity).app.appDatabase.userDao() }
    private val languagePreference by lazy { findPreference<ListPreference>("language") }
    private val versionPreference by lazy { findPreference<Preference>("version") }
    private val usersPreference by lazy { findPreference<Preference>("users") }
    private val themesPreference by lazy { findPreference<ListPreference>("theme_index") }
    private val themes by lazy { resources.getStringArray(R.array.themes) }
    private val themeIndexes by lazy { resources.getStringArray(R.array.theme_index) }
    private val langEntities by lazy { resources.getStringArray(R.array.language) }
    private val langValues by lazy { resources.getStringArray(R.array.language_values) }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_settings, rootKey)

        usersPreference?.setOnPreferenceClickListener {
            val intent = Intent(activity as SettingsActivity, UsersActivity::class.java)
            activity?.startActivity(intent)
            true
        }

        versionPreference?.summary = resources.getString(R.string.settings_version_loading)

        themesPreference?.summary = themes[themeIndexes.indexOf(themeIndex)]
        themesPreference?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == themeIndex) return@setOnPreferenceChangeListener false
            restart()
            true
        }

        languagePreference?.summary = langEntities[langValues.indexOf(language)]
        languagePreference?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == language) return@setOnPreferenceChangeListener false
            restart()
            true
        }

        NetworkUtils.checkNewVersion(object : DataProcessBlock {
//            override val context = activity!!
//
//            override fun onPrepare() {
//            }

            override fun onFailure(exception: IOException) {
                versionPreference?.summary = BuildConfig.VERSION_NAME
            }

            override fun onResponse(bodyString: String?) {
                val regex = """"version":"(.*? \((.*?)\))","build":"(.*?)"""".toRegex()
                val result = regex.find(bodyString ?: "")
                if (result != null && activity != null) {
                    val newStr = resources.getString(R.string.settings_version_new)
                    val numRegex = """.*? \((.*?)\)""".toRegex()
                    val oldCommit = numRegex.find(BuildConfig.VERSION_NAME)!!.groups[1]!!.value.toInt()
                    val newCommit = result.groups[2]!!.value.toInt()

                    if (oldCommit < newCommit) {
                        val newVersion = result.groups[1]!!.value
                        versionPreference?.summary = BuildConfig.VERSION_NAME + "\n" + newStr + " " + newVersion
                    } else {
                        versionPreference?.summary = BuildConfig.VERSION_NAME
                    }
                } else {
                    versionPreference?.summary = BuildConfig.VERSION_NAME
                }
            }

            override fun onFinished() {
            }
        })

        versionPreference?.setOnPreferenceClickListener {
            val intent = Intent(activity as SettingsActivity, VersionActivity::class.java)
            activity?.startActivity(intent)
            true
        }

        val currentId = prefs.getInt("current_user", -1)
        val result = userDao.find(currentId)
        if (result == null) {
            usersPreference?.summary = getString(R.string.settings_users_summary) + getString(R.string.unknown)
        } else {
            usersPreference?.summary = getString(R.string.settings_users_summary) + result.name
        }
    }

    override fun onResume() {
        super.onResume()
        val currentId = prefs.getInt("current_user", -1)
        val result = userDao.find(currentId)
        if (result == null) {
            usersPreference?.summary = getString(R.string.settings_users_summary) + getString(R.string.unknown)
        } else {
            usersPreference?.summary = getString(R.string.settings_users_summary) + result.name
        }
    }

    private fun restart() {
        val intents = arrayOf(Intent(activity as SettingsActivity, MainActivity::class.java), Intent(activity as SettingsActivity, SettingsActivity::class.java))
        intents[0].flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        activity?.startActivities(intents)
    }
}
