package xin.z7workbench.bjutloginapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import xin.z7workbench.bjutloginapp.BuildConfig
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.ActivityPrefsBinding
import xin.z7workbench.bjutloginapp.util.*
import java.io.IOException

/**
 * Created by ZeroGo on 2017.2.22.
 */

class SettingsActivity : BasicActivity() {
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

}