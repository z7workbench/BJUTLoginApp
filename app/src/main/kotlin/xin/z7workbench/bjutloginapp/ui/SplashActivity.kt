package xin.z7workbench.bjutloginapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import xin.z7workbench.bjutloginapp.BuildConfig
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.ActivitySplashBinding

class SplashActivity : LoginAppActivity() {
    private val SPLASH_DISPLAY_LENGTH = 750
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.splashTvVersion.text = "${getString(R.string.action_version)} ${BuildConfig.VERSION_NAME}"

        Handler().postDelayed({
            val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
            this@SplashActivity.startActivity(mainIntent)
            this@SplashActivity.finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())

    }
}