package party.iobserver.bjutloginapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import okhttp3.*
import org.jetbrains.anko.runOnUiThread
import party.iobserver.bjutloginapp.Constants
import party.iobserver.bjutloginapp.model.User
import java.io.IOException

/**
 * Created by ZeroGo on 2017.2.28.
 */

object NetworkUtils {
    private val client = OkHttpClient()
    val STATE_NO_NETWORK = 0
    val STATE_MOBILE = 1
    val STATE_BJUT_WIFI = 2
    val STATE_OTHER_WIFI = 3

    fun getNetworkState(context: Context): Int {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connManager.activeNetworkInfo
        return if (info != null && info.isConnectedOrConnecting) {
            when (info.type) {
                ConnectivityManager.TYPE_MOBILE -> STATE_MOBILE
                ConnectivityManager.TYPE_WIFI -> {
                    if (getWifiSSID(context).replace("\"", "") == "bjut_wifi")
                        STATE_BJUT_WIFI
                    else
                        STATE_OTHER_WIFI
                }
                else -> STATE_NO_NETWORK
            }
        } else
            STATE_NO_NETWORK
    }

    fun getWifiSSID(context: Context): String {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.connectionInfo.ssid
    }


    fun login(user: User, uiBlock: UIBlock) {
        uiBlock.onPrepare()
        val body = FormBody.Builder()
                .add("DDDDD", user.name)
                .add("upass", user.password)
                .add("6MKKey", "123")
                .build()
        val request = Request.Builder()
                .post(body)
                .url(Constants.WLGN_URL)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(p0: Call, p1: IOException) {
                println("Failure")
                p1.printStackTrace()
                uiBlock.context.runOnUiThread {
                    uiBlock.onFailure(p1)
                }
            }

            override fun onResponse(p0: Call, p1: Response) {
                uiBlock.context.runOnUiThread {
                    uiBlock.onResponse(p1)
                }
            }
        })
    }

    fun sync(uiBlock: UIBlock) {
        uiBlock.onPrepare()
        val request = Request.Builder()
                .get()
                .url(Constants.WLGN_URL)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(p0: Call, p1: IOException) {
                println("Failure")
                p1.printStackTrace()
                uiBlock.context.runOnUiThread {
                    uiBlock.onFailure(p1)
                }
            }

            override fun onResponse(p0: Call, p1: Response) {
                uiBlock.context.runOnUiThread {
                    uiBlock.onResponse(p1)
                }
            }
        })
    }

    fun logout(uiBlock: UIBlock) {
        uiBlock.onPrepare()
        val request = Request.Builder()
                .get()
                .url(Constants.WLGN_URL + Constants.QUIT_TAIL)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(p0: Call, p1: IOException) {
                println("Failure")
                p1.printStackTrace()
                uiBlock.context.runOnUiThread {
                    uiBlock.onFailure(p1)
                }
            }

            override fun onResponse(p0: Call, p1: Response) {
                uiBlock.context.runOnUiThread {
                    uiBlock.onResponse(p1)
                }
            }
        })
    }
}
