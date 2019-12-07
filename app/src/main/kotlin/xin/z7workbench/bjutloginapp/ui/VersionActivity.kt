package xin.z7workbench.bjutloginapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import xin.z7workbench.bjutloginapp.Constants
import xin.z7workbench.bjutloginapp.databinding.ActivityVersionBinding

/**
 * Created by ZeroGo on 2017/11/12.
 */
class VersionActivity : AppCompatActivity() {
    lateinit var binding: ActivityVersionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVersionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.web.loadUrl(Constants.VERSION_URL)

        val webSettings = binding.web.settings
        webSettings.javaScriptEnabled = true
        binding.swipeRefresh.setOnRefreshListener {
            binding.web.reload()
            binding.swipeRefresh.isRefreshing = false
        }
    }


    override fun onBackPressed() {
        if (binding.web.canGoBack()) {
            binding.web.goBack()
        } else {
            super.onBackPressed()
        }
    }
}