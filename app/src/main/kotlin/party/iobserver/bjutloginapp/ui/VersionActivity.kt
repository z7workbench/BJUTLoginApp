package party.iobserver.bjutloginapp.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_version.*
import party.iobserver.bjutloginapp.Constants
import party.iobserver.bjutloginapp.R

/**
 * Created by ZeroGo on 2017/11/12.
 */
class VersionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_version)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        web.loadUrl(Constants.VERSION_URL)

        val webSettings = web.settings
        webSettings.javaScriptEnabled = true
        swipe_refresh.setOnRefreshListener {
            web.reload()
            swipe_refresh.isRefreshing = false
        }
    }


    override fun onBackPressed() {
        if (web.canGoBack()) {
            web.goBack()
        } else {
            super.onBackPressed()
        }
    }
}