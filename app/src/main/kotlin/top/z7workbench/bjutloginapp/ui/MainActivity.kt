package top.z7workbench.bjutloginapp.ui

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.fragment.NavHostFragment
import top.z7workbench.bjutloginapp.R
import top.z7workbench.bjutloginapp.databinding.*
import top.z7workbench.bjutloginapp.model.UserViewModel
import top.z7workbench.bjutloginapp.util.LogStatus
import top.z7workbench.bjutloginapp.util.nothing
import top.z7workbench.bjutloginapp.util.toast

class MainActivity : BasicActivity() {
    val viewModel by viewModels<UserViewModel>()
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val controller by lazy {
        (supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment).navController
    }
    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.mainContainer)
                ?.childFragmentManager
                ?.fragments
                ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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
            setOnClickListener {
                when (viewModel.currentStatus) {
                    LogStatus.OFFLINE, LogStatus.ERROR -> {
                        viewModel.online()
                    }
                    LogStatus.ONLINE -> {
                        viewModel.offline()
                        context.toast("345")
                    }
                    else -> nothing()
                }
            }

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
    fun makeSnack(text: CharSequence) {
        var snack = Snackbar.make(binding.mainLayout, text, Snackbar.LENGTH_SHORT)
        snack = snack.setAnchorView(binding.fab.id)
        snack.show()
    }
}
