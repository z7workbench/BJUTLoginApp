package xin.z7workbench.bjutloginapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.util.Log
import okhttp3.*
import xin.z7workbench.bjutloginapp.Constants
import xin.z7workbench.bjutloginapp.model.User
import java.io.IOException
import java.net.Inet6Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

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

    fun body(mode: IpMode, user: User) = when (mode) {
        IpMode.WIRED_IPV4, IpMode.WIRELESS -> FormBody.Builder()
                .add("DDDDD", user.name)
                .add("upass", user.password)
                .add("v46s", "1")
                .add("v6ip", "")
                .add("f4serip", "172.30.201.10")
                .add("0MKKey", "")
                .build()
        IpMode.WIRED_IPV6 -> FormBody.Builder()
                .add("DDDDD", user.name)
                .add("upass", user.password)
                .add("v46s", "2")
                .add("v6ip", "")
                .add("f4serip", "172.30.201.10")
                .add("0MKKey", "")
                .build()
        IpMode.WIRED_BOTH -> FormBody.Builder()
                .add("DDDDD", user.name)
                .add("upass", user.password)
                .add("v6ip", getIpv6Address())
                .build()
    }

    fun url(ipMode: IpMode, isLogIn: Boolean) = when (ipMode) {
        IpMode.WIRELESS -> {
            if (isLogIn) Constants.WLGN_URL + Constants.LOGIN_TAIL
            else Constants.WLGN_URL + Constants.QUIT_TAIL
        }
        IpMode.WIRED_BOTH, IpMode.WIRED_IPV4, IpMode.WIRED_IPV6 -> {
            if (isLogIn) Constants.LGN_URL + Constants.LOGIN_TAIL
            else Constants.LGN_URL + Constants.QUIT_TAIL
        }
    }

    fun getIpv6Address(): String {
        val pattern = Pattern.compile("fe80")
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val netInterface = interfaces.nextElement()
                val inet = netInterface.inetAddresses
                while (inet.hasMoreElements()) {
                    val address = inet.nextElement()
                    val matcher = pattern.matcher(address.hostAddress)
                    if (!address.isLoopbackAddress && address is Inet6Address && !matcher.find()) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: SocketException) {
            Log.e("Network", "Can't get ipv6 addr")
            e.printStackTrace()
        }
        return ""
    }

    fun getNetworkState(context: Context): Int {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = manager.activeNetworkInfo
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
