package party.iobserver.bjutloginapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.*
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_prefs.*
import org.jetbrains.anko.startActivity
import party.iobserver.bjutloginapp.BuildConfig
import party.iobserver.bjutloginapp.R
import party.iobserver.bjutloginapp.util.NetworkUtils
import party.iobserver.bjutloginapp.util.UIBlock
import party.iobserver.bjutloginapp.util.app
import java.io.IOException

/**
 * Created by ZeroGo on 2017.2.22.
 */


class SettingsActivity : AppCompatActivity() {
    val prefs by lazy { app.prefs }
    val userDao by lazy { app.appDatabase.userDao() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prefs)
        fragmentManager.beginTransaction().replace(R.id.content, SettingsFragment()).commit()
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

    }

    @SuppressLint("ValidFragment")
    inner class SettingsFragment : PreferenceFragment() {
        val userNamePreference by lazy { findPreference("user") as EditTextPreference }
        val psdNamePreference by lazy { findPreference("password") as EditTextPreference }
        val packPreference by lazy { findPreference("pack") as ListPreference }
        val versionPreference by lazy { findPreference("version") }
        val usersPreference by lazy { findPreference("users") }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.prefs_settings)

            usersPreference.setOnPreferenceClickListener {
                startActivity<UsersActivity>()
                true
            }

            versionPreference.summary = resources.getString(R.string.settings_version_loading)

            NetworkUtils.checkNewVersion(object : UIBlock {
                override val context = activity

                override fun onPrepare() {
                }

                override fun onFailure(exception: IOException) {
                    versionPreference.summary = BuildConfig.VERSION_NAME
                }

                override fun onResponse(bodyString: String?) {
                    val regex = """"version":"(.*? \((.*?)\))","build":"(.*?)"""".toRegex()
                    val result = regex.find(bodyString ?: "")
                    if (result != null) {
                        val newStr = resources.getString(R.string.settings_version_new)
                        val numRegex = """.*? \((.*?)\)""".toRegex()
                        val oldCommit = numRegex.find(BuildConfig.VERSION_NAME)!!.groups[1]!!.value.toInt()
                        val newCommit = result.groups[2]!!.value.toInt()

                        if (oldCommit < newCommit) {
                            val newVersion = result.groups[1]!!.value
                            versionPreference.summary = BuildConfig.VERSION_NAME + "\n" + newStr + " " + newVersion
                        } else {
                            versionPreference.summary = BuildConfig.VERSION_NAME
                        }
                    } else {
                        versionPreference.summary = BuildConfig.VERSION_NAME
                    }
                }

                override fun onFinished() {
                }
            })

            versionPreference.setOnPreferenceClickListener {
                startActivity<VersionActivity>()
                true
            }

            bindPreferenceSummaryToValue(userNamePreference)
            bindPreferenceSummaryToValue(psdNamePreference)
            bindPreferenceSummaryToValue(packPreference)

            val currentId = prefs.getInt("current_user", -1)
            val result = userDao.find(currentId)
            if (result.isEmpty()) {
                usersPreference.summary = getString(R.string.settings_users_summary) + getString(R.string.unknown)
            } else {
                usersPreference.summary = getString(R.string.settings_users_summary) + result.first().name
            }
        }

        private val onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            val value = newValue.toString()
            when {
                value.isEmpty() -> preference.summary = activity.resources.getString(R.string.unknown)
                preference.key == "password" -> preference.summary = "●●●●●●●●"
                preference.key == "version" -> startActivity<VersionActivity>()
                else -> preference.summary = value
            }
            true
        }

        override fun onResume() {
            super.onResume()
            val currentId = prefs.getInt("current_user", -1)
            val result = userDao.find(currentId)
            if (result.isEmpty()) {
                usersPreference.summary = getString(R.string.settings_users_summary) + getString(R.string.unknown)
            } else {
                usersPreference.summary = getString(R.string.settings_users_summary) + result.first().name
            }
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = onPreferenceChangeListener
            onPreferenceChangeListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, ""))
        }
    }
}
