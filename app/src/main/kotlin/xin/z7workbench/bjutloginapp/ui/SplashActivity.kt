package xin.z7workbench.bjutloginapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.core.os.postDelayed
import xin.z7workbench.bjutloginapp.BuildConfig
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.ActivitySplashBinding

class SplashActivity : BasicActivity() {
    private val delays = 750L
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.splashTvVersion.text = "${getString(R.string.action_version)} ${BuildConfig.VERSION_NAME}"

        Handler().postDelayed(delays) {
            val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
            this@SplashActivity.startActivity(mainIntent)
            this@SplashActivity.finish()
        }
    }
}