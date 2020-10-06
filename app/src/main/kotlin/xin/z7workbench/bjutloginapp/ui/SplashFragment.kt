package xin.z7workbench.bjutloginapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.fragment.app.commit
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import xin.z7workbench.bjutloginapp.BuildConfig
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.ActivitySplashBinding
import xin.z7workbench.bjutloginapp.databinding.FragmentSplashBinding

class SplashFragment : BasicFragment<FragmentSplashBinding>() {
    private val delays = 750L

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
            FragmentSplashBinding.inflate(inflater, container, false)

    override fun initView() {
        binding.splashTvVersion.text = "${getString(R.string.action_version)} ${BuildConfig.VERSION_NAME}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.myLooper()!!).postDelayed(delays) {
            childFragmentManager.commit {
                findNavController().navigate(R.id.action_splash_to_main)
            }
        }
    }
}