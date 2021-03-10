package top.z7workbench.bjutloginapp.network

import androidx.core.os.bundleOf
import okhttp3.*
import top.z7workbench.bjutloginapp.Constants
import top.z7workbench.bjutloginapp.model.User
import top.z7workbench.bjutloginapp.util.IpMode
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by ZeroGo on 2017.2.28.
 */

object OkHttpNetwork {
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
                        .addHeader("User-Agent", Constants.GECKO_AGENT)
                        .build()
                it.proceed(request)
            }
            .build()

    fun login(user: User, mode: IpMode, block: DataProcessBlock) {
        val request = Request.Builder()
                .post(NetworkGlobalObject.body(mode, user))
                .url(NetworkGlobalObject.url(mode, true))
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failure")
                e.printStackTrace()
                block.onFailure(e)
                block.onFinished()
            }

            override fun onResponse(call: Call, response: Response) {
                block.onResponse(bundleOf())
                block.onFinished()
            }
        })
    }

    fun sync(mode: IpMode, block: DataProcessBlock) {
        val request = Request.Builder()
                .get()
                .url(NetworkGlobalObject.url(mode, true))
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
                val bundle = bundleOf()
                if (string == null) block.onFailure(IOException("No string found"))
                else {
                    val regex = """time='(.*?)';flow='(.*?)';fsele=1;fee='(.*?)'""".toRegex()
                    val result = regex.find(string)
                    if (result == null || result.groups.isEmpty()) {
                        bundle.putBoolean("status", false)
                    } else {
                        bundle.putBoolean("status", true)
                        bundle.putInt("time", result.groups[1]?.value?.toInt() ?: -1)
                        bundle.putLong("flow", result.groups[2]?.value?.toLong() ?: -1L)
                        bundle.putFloat("fee", result.groups[3]?.value?.toFloat() ?: -1F)
                    }
                }
                block.onResponse(bundle)
                block.onFinished()
            }
        })
    }

    fun logout(mode: IpMode, block: DataProcessBlock) {
        val request = Request.Builder()
                .get()
                .url(NetworkGlobalObject.url(mode, false))
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                block.onFailure(e)
                block.onFinished()
            }

            override fun onResponse(call: Call, response: Response) {
                block.onResponse(bundleOf())
                block.onFinished()
            }
        })
    }
}
