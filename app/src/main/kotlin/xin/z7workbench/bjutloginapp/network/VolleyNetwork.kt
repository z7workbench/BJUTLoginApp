package xin.z7workbench.bjutloginapp.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.moshi.Moshi
import org.json.JSONObject
import xin.z7workbench.bjutloginapp.model.User
import xin.z7workbench.bjutloginapp.util.IpMode

class VolleyNetwork(context: Context) {
    val queue = Volley.newRequestQueue(context)

    fun login(user: User, mode: IpMode, block: DataProcessBlock) {
        val success = Response.Listener<JSONObject> {

            block.onFinished()
        }
        val failure = Response.ErrorListener {
            block.onFinished()
        }
        val jsonObject = JSONObject()
        val request = JsonObjectRequest(Request.Method.POST, NetworkGlobalObject.url(mode, true), jsonObject, success, failure)
    }

    fun sync(mode: IpMode, block: DataProcessBlock) {

    }

    fun logout(mode: IpMode, block: DataProcessBlock) {

    }

    companion object {
        private var volley: VolleyNetwork? = null
        fun instant(context: Context) = if (volley == null) {
            VolleyNetwork(context)
        } else volley!!
    }
}