package xin.z7workbench.bjutloginapp.model

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import xin.z7workbench.bjutloginapp.LoginApp
import xin.z7workbench.bjutloginapp.util.IpMode
import xin.z7workbench.bjutloginapp.util.LogStatus

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val tag = "MainViewModel"
    private val dao = getApplication<LoginApp>().appDatabase.userDao()
    private val _status = MutableLiveData<LogStatus>()
    private val _currentId = MutableLiveData<Int>().apply {
        value = getApplication<LoginApp>().prefs.getInt("current_user", -1)
    }
    private val _user = MutableLiveData<User>()
    private val _ipMode = MutableLiveData<IpMode>()
    val data = MutableLiveData<Int>()

    val status: LiveData<LogStatus>
        get() = _status
    val currentId: LiveData<Int>
        get() = _currentId
    val users = dao.all()
    val user: LiveData<User>
        get() = _user
    val ipMode: LiveData<IpMode>
        get() = _ipMode

    init {
        _status.value = LogStatus.OFFLINE
        refreshUserId()
        setUpIpMode()
    }

    fun offline() {
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

    fun insertUser(user: User) = dao.insert(user)

    fun updateUser(user: User) = dao.update(user)

    fun deleteUser(user: User) = dao.delete(user)

    fun refreshUserId() {
        _currentId.value = getApplication<LoginApp>().prefs.getInt("current_user", -1)
        val temp = dao.find(_currentId.value!!)
        if (temp != null) {
            _user.value = temp
        } else _user.value = User()
//        _user.postValue(temp)
    }

    fun changeIpMode(mode: Int) {
        getApplication<LoginApp>().prefs.edit { putInt("ip_mode", mode) }
        setUpIpMode()
    }

    private fun setUpIpMode() {
        val option = getApplication<LoginApp>().prefs.getInt("ip_mode", 0)
        _ipMode.value = when (option) {
            1 -> IpMode.WIRED_IPV4
            2 -> IpMode.WIRED_IPV6
            3 -> IpMode.WIRED_BOTH
            else -> IpMode.WIRELESS
        }
    }
}