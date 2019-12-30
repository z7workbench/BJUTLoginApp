package xin.z7workbench.bjutloginapp.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.startActivity
import xin.z7workbench.bjutloginapp.BuildConfig
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.ActivityPrefsBinding
import xin.z7workbench.bjutloginapp.util.*
import java.io.IOException
import kotlin.system.exitProcess

/**
 * Created by ZeroGo on 2017.2.22.
 */

class SettingsActivity : AppCompatActivity() {
    lateinit var language: String
    lateinit var binding: ActivityPrefsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrefsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = getString(R.string.action_settings)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        language = app.prefs.getString("language", "0") ?: "Auto"
        supportFragmentManager.beginTransaction()
                .replace(R.id.pref_content, SettingsFragment())
                .commit()
    }

    override fun onBackPressed() {
        val langString = app.prefs.getString("language", "Auto")
        if ((language == langString) ||
                (langString == "Auto" && Resources.getSystem().configuration.locales[0].language == language) ||
                (language == "Auto" && Resources.getSystem().configuration.locales[0].language == langString))
            super.onBackPressed()
        else {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleUtil.wrap(newBase))
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration) {
        super.applyOverrideConfiguration(baseContext.resources.configuration)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        lateinit var language: String
        private val prefs by lazy { activity?.app?.prefs }
        private val userDao by lazy { activity?.app?.appDatabase?.userDao() }
        private val languagePreference by lazy { findPreference<ListPreference>("language") }
        private val versionPreference by lazy { findPreference<Preference>("version") }
        private val usersPreference by lazy { findPreference<Preference>("users") }
        private val langEntities by lazy { resources.getStringArray(R.array.language) }
        private val values by lazy { resources.getStringArray(R.array.language_values) }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.prefs_settings, rootKey)

            usersPreference?.setOnPreferenceClickListener {
                activity?.startActivity<UsersActivity>()
                true
            }

            versionPreference?.summary = resources.getString(R.string.settings_version_loading)

            languagePreference?.summary = langEntities[values.indexOf(prefs?.getString("language", null)
                    ?: values[0])]
            language = prefs?.getString("language", null) ?: "Auto"

            languagePreference?.setOnPreferenceChangeListener { preference, newValue ->
                if (language != newValue) {
                    preference.summary = "*${langEntities[values.indexOf(newValue as String)]}"
                    Snackbar.make((activity as SettingsActivity)
                            .binding.prefsLayout, R.string.save_changes, 3000).show()
                } else preference.summary = langEntities[values.indexOf(newValue as String)]
                true
            }

            NetworkUtils.checkNewVersion(object : UIBlock {
                override val context = activity!!

                override fun onPrepare() {
                }

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
                activity?.startActivity<VersionActivity>()
                true
            }

            val currentId = prefs?.getInt("current_user", -1) ?: -1
            val result = userDao?.find(currentId) ?: mutableListOf()
            if (result.isEmpty()) {
                usersPreference?.summary = getString(R.string.settings_users_summary) + getString(R.string.unknown)
            } else {
                usersPreference?.summary = getString(R.string.settings_users_summary) + result.first().name
            }
        }

        override fun onResume() {
            super.onResume()
            val currentId = prefs?.getInt("current_user", -1) ?: -1
            val result = userDao?.find(currentId) ?: mutableListOf()
            if (result.isEmpty()) {
                usersPreference?.summary = getString(R.string.settings_users_summary) + getString(R.string.unknown)
            } else {
                usersPreference?.summary = getString(R.string.settings_users_summary) + result.first().name
            }
        }
    }
}
