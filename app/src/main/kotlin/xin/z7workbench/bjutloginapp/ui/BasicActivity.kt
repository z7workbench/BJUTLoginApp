package xin.z7workbench.bjutloginapp.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import xin.z7workbench.bjutloginapp.LoginApp
import xin.z7workbench.bjutloginapp.util.LocaleUtil
import xin.z7workbench.bjutloginapp.util.ThemeUtil


open class BasicActivity: AppCompatActivity() {
    val app by lazy { application as LoginApp }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.setCurrentTheme(this)

    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleUtil.wrap(newBase))
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration) {
        super.applyOverrideConfiguration(baseContext.resources.configuration)
    }

}