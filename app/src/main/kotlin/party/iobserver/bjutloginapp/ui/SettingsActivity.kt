package party.iobserver.bjutloginapp.ui

import android.os.Bundle
import android.preference.*
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_prefs.*
import org.jetbrains.anko.startActivity
import party.iobserver.bjutloginapp.BuildConfig
import party.iobserver.bjutloginapp.R
import party.iobserver.bjutloginapp.util.NetworkUtils
import party.iobserver.bjutloginapp.util.UIBlock
import java.io.IOException

/**
 * Created by ZeroGo on 2017.2.22.
 */


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prefs)
        fragmentManager.beginTransaction().replace(R.id.content, SettingsFragment()).commit()
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

    }

    class SettingsFragment : PreferenceFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.prefs_settings)
            val userNamePreference = findPreference("user") as EditTextPreference
            val psdNamePreference = findPreference("password") as EditTextPreference
            val packPreference = findPreference("pack") as ListPreference
            val vcPreference = findPreference("versionControl") as ListPreference
            val versionPreference = findPreference("version")

            versionPreference.summary = resources.getString(R.string.settings_version_loading)

            NetworkUtils.checkNewVersion(object : UIBlock {
                override val context = activity

                override fun onPrepare() {
                }

                override fun onFailure(exception: IOException) {
                    versionPreference.summary = BuildConfig.VERSION_NAME
                }

                override fun onResponse(bodyString: String?) {
                    val regex = """"version":"(.*? ([D,S])E \((.*?)\))","build":"(.*?)"""".toRegex()
                    val result = regex.find(bodyString ?: "")
                    if (result != null) {
                        val newStr = resources.getString(R.string.settings_version_new)
                        val numRegex = """.*? \((.*?)\)""".toRegex()
                        val oldCommit = numRegex.find(BuildConfig.VERSION_NAME)!!.groups[1]!!.value.toInt()
                        val newCommit = result.groups[2]!!.value.toInt()

                        if (oldCommit < newCommit) {
                            val newVersion = result.groups[1]!!.value
                            versionPreference.summary = BuildConfig.VERSION_NAME + newStr + newVersion
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
            bindPreferenceSummaryToValue(vcPreference)
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

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = onPreferenceChangeListener
            onPreferenceChangeListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, ""))
        }
    }
}