package xin.z7workbench.bjutloginapp.ui

import android.os.Bundle
import xin.z7workbench.bjutloginapp.Constants
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.ActivityVersionBinding

/**
 * Created by ZeroGo on 2017/11/12.
 */
class VersionActivity : BasicActivity() {
    private lateinit var binding: ActivityVersionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVersionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = getString(R.string.action_version)
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