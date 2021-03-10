package top.z7workbench.bjutloginapp.network

import android.os.Bundle
import java.io.IOException

interface DataProcessBlock {
    fun onFailure(exception: IOException)
    fun onResponse(bundle: Bundle)
    fun onFinished()
}