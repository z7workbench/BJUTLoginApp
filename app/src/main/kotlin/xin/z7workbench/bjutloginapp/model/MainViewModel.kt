package xin.z7workbench.bjutloginapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xin.z7workbench.bjutloginapp.util.LogStatus
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel: ViewModel() {
    val tag = "MainActivity"
    private val _status = MutableLiveData<LogStatus>()
    val status : LiveData<LogStatus>
        get() = _status
    init {
        _status.value = LogStatus.OFFLINE
    }

    fun offline(){
        _status.value = LogStatus.OFFLINE
    }

    fun error() {
        _status.value = LogStatus.ERROR
    }

    fun online() {
        _status.value = LogStatus.ONLINE
    }

    fun syncing() {
        _status.value = LogStatus.SYNCING
    }
}