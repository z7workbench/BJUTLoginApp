package top.z7workbench.bjutloginapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.fragment.NavHostFragment
import top.z7workbench.bjutloginapp.R
import top.z7workbench.bjutloginapp.databinding.*
import top.z7workbench.bjutloginapp.model.StatusViewModel
import top.z7workbench.bjutloginapp.model.UserViewModel
import top.z7workbench.bjutloginapp.util.LogStatus
import top.z7workbench.bjutloginapp.util.nothing

class MainActivity : BasicActivity() {
    private val viewModel by viewModels<UserViewModel>()
    private val status by viewModels<StatusViewModel>()
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val controller by lazy {
        (supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_RESUME) status.networkState(this@MainActivity)
            }
        })
        binding.run {
            controller.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.themeFragment, R.id.localeFragment -> {
//                        hideBottomAppBar()
                        fab.hide()
                    }
                    R.id.userFragment -> {
//                        hideBottomAppBar()
                        fab.hide()
                    }
                    R.id.mainFragment -> {
//                        showBottomAppBar()
                        fab.show()
                    }
                }
            }
        }

        binding.fab.run {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
//            setOnClickListener { toast(getSSID()) }
            setOnClickListener { fabAction() }

            viewModel.status.observe(this@MainActivity) {
                when (viewModel.currentStatus) {
                    LogStatus.OFFLINE, LogStatus.ERROR -> {
                        icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_online, theme)
                        text = resources.getString(R.string.connect)
                    }
                    LogStatus.ONLINE -> {
                        icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_offline, theme)
                        text = resources.getString(R.string.disconnect)
                    }
                    else -> {
                        icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_loading, theme)
                        text = resources.getString(R.string.status_logging)
                    }
                }
            }
        }

    }

    fun makeSnack(text: CharSequence) =
        Snackbar.make(binding.mainLayout, text, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.fab.id)
            .show()

    private fun fabAction() {
        when (viewModel.currentStatus) {
            LogStatus.OFFLINE, LogStatus.ERROR -> {
                viewModel.online()
            }
            LogStatus.ONLINE -> {
                viewModel.offline()
            }
            else -> nothing()
        }
    }
}
