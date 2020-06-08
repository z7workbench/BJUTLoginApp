package xin.z7workbench.bjutloginapp.model

import androidx.lifecycle.ViewModel
import xin.z7workbench.bjutloginapp.util.LogStatus
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel: ViewModel() {
    val tag = "MainActivity"
    var status = LogStatus.OFFLINE
    var emsg = ""
    var currentId = -1
    var currentName = ""
    var currentPack = -1
}