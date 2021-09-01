package top.z7workbench.bjutloginapp.util

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.*
import top.z7workbench.bjutloginapp.Constants

inline val Context.defaultSharedPreferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)

inline val Fragment.defaultSharedPreferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(activity)

fun Context.runOnUiThread(f: Context.() -> Unit) {
    if (ContextHelper.mainThread == Thread.currentThread()) f() else ContextHelper.handler.post { f() }
}

inline fun Fragment.runOnUiThread(crossinline f: () -> Unit) {
    requireActivity().runOnUiThread { f() }
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

fun nothing() = Unit

inline fun <reified T> DataStore<Preferences>.valueFlow(
    key: Preferences.Key<T>,
    defValue: T
): Flow<T> =
    this.data.mapNotNull {
        it[key] ?: defValue
    }

fun Context.toast(text: CharSequence) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.DATASTORE_NAME)

inline fun <reified T : ComponentActivity> Context.startActivity() =
    this.startActivity(Intent(this, T::class.java))