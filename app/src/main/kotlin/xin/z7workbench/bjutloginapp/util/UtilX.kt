package xin.z7workbench.bjutloginapp.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager

inline val Context.defaultSharedPreferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)

inline val Fragment.defaultSharedPreferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(activity)

fun Context.runOnUiThread(f: Context.() -> Unit) {
    if (ContextHelper.mainThread == Thread.currentThread()) f() else ContextHelper.handler.post { f() }
}

inline fun Fragment.runOnUiThread(crossinline f: () -> Unit) {
    activity?.runOnUiThread { f() }
}

private object ContextHelper {
    val handler = Handler(Looper.getMainLooper())
    val mainThread = Looper.getMainLooper().thread
}

fun String.buildString(vararg strings: String): String {
    var string = this
    for (s in strings) {
        string += s
    }
    return string
}