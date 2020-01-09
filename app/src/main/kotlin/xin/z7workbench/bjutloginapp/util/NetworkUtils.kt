package xin.z7workbench.bjutloginapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import okhttp3.*
import xin.z7workbench.bjutloginapp.Constants
import xin.z7workbench.bjutloginapp.model.User
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by ZeroGo on 2017.2.28.
 */

object NetworkUtils {
    private val client = OkHttpClient.Builder()
            .cache(null).readTimeout(5, TimeUnit.SECONDS).writeTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(3, TimeUnit.SECONDS).build()
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

    fun login(user: User, isWireless: Boolean, uiBlock: UIBlock) {
        uiBlock.onPrepare()
        val body = when (isWireless) {
            true -> FormBody.Builder()
                    .add("DDDDD", user.name)
                    .add("upass", user.password)
                    .add("6MKKey", "123")
                    .build()

            false -> FormBody.Builder()
                    .add("DDDDD", user.name)
                    .add("upass", user.password)
                    .add("v46s", "1")
                    .add("v6ip", "")
                    .add("f4serip", "lgn.bjut.edu.cn")
                    .add("0MKKey", "")
                    .build()
        }
        val request = when (isWireless) {
            true -> Request.Builder()
                    .post(body)
                    .url(Constants.WLGN_URL + Constants.LOGIN_TAIL)
                    .build()
            false -> Request.Builder()
                    .post(body)
                    .url(Constants.LGN_URL + Constants.LOGIN_TAIL)
                    .build()
        }
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failure")
                e.printStackTrace()
                uiBlock.context.runOnUiThread {
                    uiBlock.onFailure(e)
                    uiBlock.onFinished()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                var string = response.body?.string()
                string = string?.replace(" ", "")
                uiBlock.context.runOnUiThread {
                    uiBlock.onResponse(string)
                    uiBlock.onFinished()
                }
            }
        })
    }

    fun sync(isWireless: Boolean, uiBlock: UIBlock) {
        uiBlock.onPrepare()
        val request = when (isWireless) {
            true -> Request.Builder()
                    .get()
                    .url(Constants.WLGN_URL)
                    .build()
            false -> Request.Builder()
                    .get()
                    .url(Constants.LGN_URL)
                    .build()
        }
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failure")
                e.printStackTrace()
                uiBlock.context.runOnUiThread {
                    uiBlock.onFailure(e)
                    uiBlock.onFinished()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                var string = response.body?.string()
                string = string?.replace(" ", "")
                uiBlock.context.runOnUiThread {
                    uiBlock.onResponse(string)
                    uiBlock.onFinished()
                }
            }
        })
    }

    fun logout(isWireless: Boolean, uiBlock: UIBlock) {
        uiBlock.onPrepare()
        val request = when (isWireless) {
            true -> Request.Builder()
                    .get()
                    .url(Constants.WLGN_URL + Constants.QUIT_TAIL)
                    .build()
            false -> Request.Builder()
                    .get()
                    .url(Constants.LGN_URL + Constants.QUIT_TAIL)
                    .build()
        }

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                uiBlock.context.runOnUiThread {
                    uiBlock.onFailure(e)
                    uiBlock.onFinished()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                var string = response.body?.string()
                string = string?.replace(" ", "")
                uiBlock.context.runOnUiThread {
                    uiBlock.onResponse(string)
                    uiBlock.onFinished()
                }
            }
        })
    }

    fun checkNewVersion(uiBlock: UIBlock) {
        uiBlock.onPrepare()
        val request = Request.Builder()
                .get()
                .url(Constants.CHECK_URL)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                uiBlock.context.runOnUiThread {
                    uiBlock.onFailure(e)
                    uiBlock.onFinished()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val string = response.body?.string()
                uiBlock.context.runOnUiThread {
                    uiBlock.onResponse(string)
                    uiBlock.onFinished()
                }
            }
        })
    }
}
