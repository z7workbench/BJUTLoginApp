package party.iobserver.bjutloginapp.util

import android.content.Context
import okhttp3.Response
import java.io.IOException

interface UIBlock {
    val context: Context

    fun onPrepare()
    fun onFailure(exception: IOException)
    fun onResponse(response: Response)
}