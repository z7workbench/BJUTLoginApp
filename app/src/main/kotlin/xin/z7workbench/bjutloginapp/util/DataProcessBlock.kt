package xin.z7workbench.bjutloginapp.util

import java.io.IOException

interface DataProcessBlock {
    fun onFailure(exception: IOException)
    fun onResponse(bodyString: String?)
    fun onFinished()
}