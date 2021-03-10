package top.z7workbench.bjutloginapp.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import kotlin.math.min


class MoveUpwardBehavior : CoordinatorLayout.Behavior<View> {
    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is SnackbarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        child.translationY = min(0F, dependency.translationY - dependency.height)
        return true
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: View, dependency: View) {
        ViewCompat.animate(child).translationY(0F).start();
    }

    constructor()
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
}