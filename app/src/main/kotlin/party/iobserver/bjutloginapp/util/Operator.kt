package party.iobserver.bjutloginapp.util

import android.app.Activity
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.TextView
import okhttp3.*
import party.iobserver.bjutloginapp.R
import java.io.IOException
import java.util.regex.Pattern

/**
 * Created by ZeroGo on 2017.2.23.
 */

class Operator(private val TAG: String, private val activity: Activity) {
    private val okHttpClient = OkHttpClient()

    fun login(view: View, user: String?, password: String) {
        if (user != null && !user.isEmpty()) {
            val requestBody = FormBody.Builder()
                    .add("DDDDD", user)
                    .add("upass", password)
                    .add("6MKKey", "123")
                    .build()
            val request = Request.Builder()
                    .post(requestBody)
                    .url("http://wlgn.bjut.edu.cn/")
                    .build()
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Snackbar.make(view, "Login Failed! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    Log.e(TAG, "Failed! ", e)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.body()!!.string().indexOf("In use") > 0) {
                        Snackbar.make(view, "Login Failed! " + "This account is in use. ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                    } else {
                        Snackbar.make(view, "Login Succeeded! ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                    }
                }
            })
        }
    }

    fun refresh(view: View, fluxView: TextView, statusView: TextView) {
        statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_yellow))
        val request = Request.Builder()
                .get()
                .url("http://lgn.bjut.edu.cn/")
                .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Snackbar.make(view, "Refresh Failed! ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                Log.e(TAG, "Failed! ", e)
                activity.runOnUiThread {
                    try {
                        statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_red))
                    } catch (e1: Exception) {
                        Log.e(TAG, "", e1)
                    }
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val pattern = Pattern.compile("flow='(\\d+)")
                val matcher = pattern.matcher(response.body()!!.string())

                if (matcher.find()) {
                    val flux = (java.lang.Double.parseDouble(matcher.group(1)) / 1024 * 100).toInt().toDouble() / 100
                    activity.runOnUiThread {
                        try {
                            fluxView.text = flux.toString() + "MB"
                            statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_green))
                        } catch (e: Exception) {
                            Log.e(TAG, "", e)
                        }
                    }
                    Snackbar.make(view, "Refresh successfully completed! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                } else {
                    statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_red))
                    Snackbar.make(view, "Can't get data! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                }
            }
        })
    }

    fun logout(view: View, statusView: TextView) {
        val request = Request.Builder()
                .get()
                .url("http://wlgn.bjut.edu.cn/F.htm")
                .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Snackbar.make(view, "Logout Failed! ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                Log.e(TAG, "Failed!", e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.body()!!.string().indexOf("注销成功") > 0) {
                    Snackbar.make(view, "Logout Succeeded! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                } else {
                    Snackbar.make(view, "Logout Failed! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                }
            }
        })
        statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_yellow))

    }

}
