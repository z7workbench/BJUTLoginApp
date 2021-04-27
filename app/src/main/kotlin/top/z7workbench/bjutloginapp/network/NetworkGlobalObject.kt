package top.z7workbench.bjutloginapp.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.util.Log
import okhttp3.FormBody
import okhttp3.OkHttpClient
import top.z7workbench.bjutloginapp.Constants
import top.z7workbench.bjutloginapp.model.User
import top.z7workbench.bjutloginapp.util.IpMode
import java.net.Inet6Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object NetworkGlobalObject {
    val client = OkHttpClient.Builder()
        .cache(null)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .addInterceptor {
            val request = it.request()
                .newBuilder()
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "en-US,en;q=0.5")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-type", "text/html; charset=gbk")
                .addHeader("User-Agent", Constants.GECKO_WIN_AGENT)
                .build()
            it.proceed(request)
        }
        .build()

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


    private const val STATE_NO_NETWORK = 0
    private const val STATE_MOBILE = 1
    private const val STATE_BJUT_WIFI = 2
    private const val STATE_OTHER_WIFI = 3

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
}