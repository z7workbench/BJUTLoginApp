package xin.z7workbench.bjutloginapp.model

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import xin.z7workbench.bjutloginapp.LoginApp
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.util.DataProcessBlock
import xin.z7workbench.bjutloginapp.util.IpMode
import xin.z7workbench.bjutloginapp.util.LogStatus
import xin.z7workbench.bjutloginapp.util.NetworkUtils
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val tag = "MainViewModel"
    private val dao = getApplication<LoginApp>().appDatabase.userDao()
    private val _status = MutableLiveData<LogStatus>()
    private val _currentId = MutableLiveData<Int>().apply {
        value = getApplication<LoginApp>().prefs.getInt("current_user", -1)
    }
    private val _user = MutableLiveData<User>()
    private val _ipMode = MutableLiveData<IpMode>()
    private val _time = MutableLiveData<String>()
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    val status: LiveData<LogStatus>
        get() = _status
    val currentId: LiveData<Int>
        get() = _currentId
    val users = dao.all()
    val user: LiveData<User>
        get() = _user
    val ipMode: LiveData<IpMode>
        get() = _ipMode
    val time: LiveData<String>
        get() = _time

    init {
        _status.value = LogStatus.OFFLINE
        refreshUserId()
        setUpIpMode()
//        updateSyncedTime(true)
        _time.value = app.resources.getString(R.string.main_last) +
                        app.resources.getString(R.string.unknown)
    }

    fun offline() {
        NetworkUtils.sync(_ipMode.value as IpMode, object : DataProcessBlock {
            override fun onFailure(exception: IOException) {
                error()
            }

            override fun onResponse(bodyString: String?) {
                _status.value = LogStatus.OFFLINE
            }

            override fun onFinished() = updateSyncedTime(false)
        })
    }

    private fun error() {
        _status.value = LogStatus.ERROR
    }

    fun online() {
        _status.value = LogStatus.ONLINE
    }

    fun syncing() {
        _status.value = LogStatus.SYNCING
        NetworkUtils.sync(_ipMode.value as IpMode, object : DataProcessBlock {
            override fun onFailure(exception: IOException) {
                _status.postValue(LogStatus.ERROR)
            }

            override fun onResponse(bodyString: String?) {
                _status.postValue(LogStatus.ONLINE)
            }

            override fun onFinished() = updateSyncedTime(false)
        })
    }

    fun insertUser(user: User) = dao.insert(user)

    fun updateUser(user: User) = dao.update(user)

    fun deleteUser(user: User) = dao.delete(user)

    fun refreshUserId() {
        _currentId.value = getApplication<LoginApp>().prefs.getInt("current_user", -1)
        val temp = dao.find(_currentId.value!!)
        // if data in database is null, use a new value to replace it
        if (temp != null) {
            _user.value = temp
        } else _user.value = User()
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

    private fun updateSyncedTime(isUnknown: Boolean) {
        val time = System.currentTimeMillis()
        val date = Date(time)
        // use MutableLiveData#postValue instead of MutableLiveData#setValue in asynchronous process
        if (!isUnknown) _time.postValue(
                getApplication<LoginApp>().resources.getString(R.string.main_last) +
                        sdf.format(date)
        )
//        else _time.postValue(
//                getApplication<LoginApp>().resources.getString(R.string.main_last) +
//                        getApplication<LoginApp>().resources.getString(R.string.unknown)
//        )
    }
}