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
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * Created by ZeroGo on 2017.2.28.
 */

object NetworkUtils {
    private val client = OkHttpClient.Builder()
            .cache(null)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(7, TimeUnit.SECONDS)
            .addInterceptor {
                val request = it.request()
                        .newBuilder()
                        .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        .addHeader("Accept-Language", "en-US,en;q=0.5")
                        .addHeader("Cache-Control", "max-age=0")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("Content-type", "text/html; charset=gbk")
                        .addHeader("Host", "lgn.bjut.edu.cn")
                        .addHeader("Upgrade-Insecure-Requests", "1")
                        .addHeader("User-Agent", Constants.GECKO_AGENT)
                        .build()
                it.proceed(request)
            }
            .build()
    private const val STATE_NO_NETWORK = 0
    private const val STATE_MOBILE = 1
    private const val STATE_BJUT_WIFI = 2
    private const val STATE_OTHER_WIFI = 3

    private fun body(mode: IpMode, user: User) = when (mode) {
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

    private fun url(ipMode: IpMode, isLogIn: Boolean) = when (ipMode) {
        IpMode.WIRELESS -> {
            if (isLogIn) Constants.WLGN_URL
            else Constants.WLGN_URL + Constants.QUIT_TAIL
        }
        IpMode.WIRED_BOTH, IpMode.WIRED_IPV4, IpMode.WIRED_IPV6 -> {
            if (isLogIn) Constants.LGN_URL
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

    @Deprecated("Lots of classes are deprecated.")
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

    @Deprecated("Some errors would happen.")
    fun getWifiSSID(context: Context): String {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.connectionInfo.ssid
    }

    fun login(user: User, mode: IpMode, block: DataProcessBlock) {
        val request = Request.Builder()
                .post(body(mode, user))
                .url(url(mode, true))
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failure")
                e.printStackTrace()
                block.onFailure(e)
                block.onFinished()
            }

            override fun onResponse(call: Call, response: Response) {
                var string = response.body?.string()
                string = string?.replace(" ", "")
                block.onResponse(string)
                block.onFinished()
            }
        })
    }

    fun sync(mode: IpMode, block: DataProcessBlock) {
        val request = Request.Builder()
                .get()
                .url(url(mode, true))
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failure")
                e.printStackTrace()
                block.onFailure(e)
                block.onFinished()
            }

            override fun onResponse(call: Call, response: Response) {
                var string = response.body?.string()
                string = string?.replace(" ", "")
                block.onResponse(string)
                block.onFinished()
            }
        })
    }

    fun logout(mode: IpMode, block: DataProcessBlock) {
        val request = Request.Builder()
                .get()
                .url(url(mode, false))
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                block.onFailure(e)
                block.onFinished()
            }

            override fun onResponse(call: Call, response: Response) {
                var string = response.body?.string()
                string = string?.replace(" ", "")
                block.onResponse(string)
                block.onFinished()

            }
        })
    }

    @Deprecated("Do not use this.")
    fun checkNewVersion(block: DataProcessBlock) {
        val request = Request.Builder()
                .get()
                .url(Constants.CHECK_URL)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                block.onFailure(e)
                block.onFinished()
            }

            override fun onResponse(call: Call, response: Response) {
                val string = response.body?.string()
                block.onResponse(string)
                block.onFinished()
            }
        })
    }
}
