package xin.z7workbench.bjutloginapp.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.transition.MaterialSharedAxis
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.*
import xin.z7workbench.bjutloginapp.model.MainViewModel
import xin.z7workbench.bjutloginapp.util.LogStatus
import xin.z7workbench.bjutloginapp.util.NetworkUtils
import xin.z7workbench.bjutloginapp.util.nothing
import xin.z7workbench.bjutloginapp.view.bottomappbar.cradle.BottomAppBarCutCradleTopEdge

class MainActivity : BasicActivity() {
    val viewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
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
                        hideBottomAppBar()
                    }
                    R.id.userFragment -> {
                        hideBottomAppBar()
                        fab.hide()
                    }
                    R.id.mainFragment -> {
                        showBottomAppBar()
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
                    }
                    else -> nothing()
                }
            }
        }

        binding.bottomAppBar.run {
            val topEdge = BottomAppBarCutCradleTopEdge(
                    fabCradleMargin, fabCradleRoundedCornerRadius, cradleVerticalOffset
            )
            val background = background as MaterialShapeDrawable
            background.shapeAppearanceModel = background.shapeAppearanceModel.toBuilder()
                    .setTopEdge(topEdge).build()

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_theme -> {
                        currentNavigationFragment?.run {
                            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
                        }
                        controller.navigate(R.id.action_global_themeFragment)
                    }
                    R.id.action_lang -> {
                        currentNavigationFragment?.run {
                            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
                        }
                        controller.navigate(R.id.action_global_localeFragment)
                    }
                    R.id.action_debug -> {
                        makeSnack(NetworkUtils.getIpv6Address())
                    }
                }
                true
            }
        }
    }

    private fun showBottomAppBar() {
        binding.bottomAppBar.run {
            visibility = View.VISIBLE
            performShow()
        }
    }

    private fun hideBottomAppBar() {
        binding.run {
            bottomAppBar.performHide()
            // Get a handle on the animator that hides the bottom app bar so we can wait to hide
            // the fab and bottom app bar until after it's exit animation finishes.
            bottomAppBar.animate().setListener(object : AnimatorListenerAdapter() {
                var isCanceled = false
                override fun onAnimationEnd(animation: Animator?) {
                    if (isCanceled) return

                    // Hide the BottomAppBar to avoid it showing above the keyboard
                    // when composing a new email.
                    bottomAppBar.visibility = View.GONE
                    fab.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator?) {
                    isCanceled = true
                }
            })
        }
    }

    fun makeSnack(text: CharSequence) {
        var snack = Snackbar.make(binding.mainLayout, text, Snackbar.LENGTH_SHORT)
        snack = snack.setAnchorView(binding.fab.id)
        snack.show()
    }

    override fun onResume() {
        super.onResume()
    }
}
