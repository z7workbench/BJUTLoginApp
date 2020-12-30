package xin.z7workbench.bjutloginapp.network

import java.io.IOException

interface OkHttpDataBlock {
    fun onFailure(exception: IOException)
    fun onResponse(bodyString: String?)
    fun onFinished()
}