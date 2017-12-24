package party.iobserver.bjutloginapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash.*
import party.iobserver.bjutloginapp.BuildConfig
import party.iobserver.bjutloginapp.R

class SplashActivity : AppCompatActivity() {
    private val SPLASH_DISPLAY_LENGTH = 750

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        splash_tv_version.text = "Version " + BuildConfig.VERSION_NAME

        Handler().postDelayed({
            val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
            this@SplashActivity.startActivity(mainIntent)
            this@SplashActivity.finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())

    }
}