package party.iobserver.bjutloginapp.ui

import android.os.Bundle
import android.preference.*
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_prefs.*
import org.jetbrains.anko.startActivity
import party.iobserver.bjutloginapp.BuildConfig
import party.iobserver.bjutloginapp.R

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
            val versionPreference = findPreference("version")

            versionPreference.summary = BuildConfig.VERSION_NAME
            versionPreference.setOnPreferenceClickListener {
                startActivity<VersionActivity>()
                true
            }

            bindPreferenceSummaryToValue(userNamePreference)
            bindPreferenceSummaryToValue(psdNamePreference)
            bindPreferenceSummaryToValue(packPreference)
        }

        private val onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            val value = newValue.toString()
            when {
                value.isEmpty() -> preference.summary = activity.resources.getString(R.string.unknown)
                preference.key == "password" -> preference.summary = "●●●●●●●●"
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