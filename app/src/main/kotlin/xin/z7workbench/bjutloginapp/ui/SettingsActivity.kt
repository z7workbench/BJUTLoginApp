package xin.z7workbench.bjutloginapp.ui

import android.content.Intent
import android.os.Bundle
import android.preference.ListPreference
import android.preference.PreferenceFragment
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_prefs.*
import org.jetbrains.anko.startActivity
import xin.z7workbench.bjutloginapp.BuildConfig
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.util.NetworkUtils
import xin.z7workbench.bjutloginapp.util.UIBlock
import xin.z7workbench.bjutloginapp.util.app
import java.io.IOException

/**
 * Created by ZeroGo on 2017.2.22.
 */

class SettingsActivity : AppCompatActivity() {
    lateinit var language: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prefs)
        fragmentManager.beginTransaction().replace(R.id.content, SettingsFragment()).commit()
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        language = app.prefs.getString("language", "0")
    }

    override fun onBackPressed() {
        if (language == app.prefs.getString("language", "0"))
            super.onBackPressed()
        else {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }

    class SettingsFragment : PreferenceFragment() {
        lateinit var language: String
        private val prefs by lazy { activity.app.prefs }
        private val userDao by lazy { activity.app.appDatabase.userDao() }
        private val languagePreference by lazy { findPreference("language") as ListPreference }
        private val versionPreference by lazy { findPreference("version") }
        private val usersPreference by lazy { findPreference("users") }
        private val array by lazy { resources.getStringArray(R.array.language) }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.prefs_settings)

            usersPreference.setOnPreferenceClickListener {
                startActivity<UsersActivity>()
                true
            }

            versionPreference.summary = resources.getString(R.string.settings_version_loading)

            languagePreference.summary = array[prefs.getString("language", "0").toInt()]
            language = prefs.getString("language", "0")

            languagePreference.setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = array[newValue.toString().toInt()]
                if (language == prefs.getString("language", language)){
                    Snackbar.make(activity.prefs_layout, R.string.save_changes, 3000).show()
                }
                true
            }

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

            val currentId = prefs.getInt("current_user", -1)
            val result = userDao.find(currentId)
            if (result.isEmpty()) {
                usersPreference.summary = getString(R.string.settings_users_summary) + getString(R.string.unknown)
            } else {
                usersPreference.summary = getString(R.string.settings_users_summary) + result.first().name
            }
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

    }
}
