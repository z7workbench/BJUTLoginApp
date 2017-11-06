package party.iobserver.bjutloginapp.util

import android.content.Context
import java.io.IOException

interface UIBlock {
    val context: Context

    fun onPrepare()
    fun onFailure(exception: IOException)
    fun onResponse(bodyString: String?)
    fun onFinished()
}