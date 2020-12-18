package xin.z7workbench.bjutloginapp.model

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import xin.z7workbench.bjutloginapp.LoginApp
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.util.*
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
    val themeIndies: List<String>
    val localeIndies: List<String>

    init {
        _status.value = LogStatus.OFFLINE
        themeIndies = app.resources.getStringArray(R.array.theme_index).toList()
        localeIndies = app.resources.getStringArray(R.array.language_values).toList()
        refreshUserId()
        setUpIpMode()
    }

    fun offline() {
        NetworkUtils.sync(_ipMode.value as IpMode, object : DataProcessBlock {
            override fun onFailure(exception: IOException) {
                error()
            }

            override fun onResponse(bodyString: String?) {
                _status.value = LogStatus.OFFLINE
            }

            override fun onFinished() = updateSyncedTime()
        })
    }

    private fun error() {
        _status.value = LogStatus.ERROR
    }

    fun online() {
        NetworkUtils.login(_user.value as User, _ipMode.value as IpMode, object : DataProcessBlock {
            override fun onFailure(exception: IOException) {
                _status.postValue(LogStatus.ERROR)
            }

            override fun onResponse(bodyString: String?) {
                syncing()
            }

            override fun onFinished() = updateSyncedTime()
        })
        _status.value = LogStatus.ONLINE
    }

    fun syncing(context: Context? = null, block: () -> Unit = {}) {
        _status.postValue(LogStatus.SYNCING)
        NetworkUtils.sync(_ipMode.value as IpMode, object : DataProcessBlock {
            override fun onFailure(exception: IOException) {
                _status.postValue(LogStatus.ERROR)
                context?.runOnUiThread { block() }
            }

            override fun onResponse(bodyString: String?) {
                if (bodyString == null) _status.postValue(LogStatus.ERROR)
                else {
                    val regex = """time='(.*?)';flow='(.*?)';fsele=1;fee='(.*?)'""".toRegex()
                    val result = regex.find(bodyString)
                    if (result == null || result.groups.isEmpty()) {
                        _status.postValue(LogStatus.OFFLINE)
                    } else _status.postValue(LogStatus.ONLINE)
                }
                context?.runOnUiThread { block() }
            }

            override fun onFinished() = updateSyncedTime()
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

    private fun updateSyncedTime() {
        val time = System.currentTimeMillis()
        val date = Date(time)
        // use MutableLiveData#postValue instead of MutableLiveData#setValue in asynchronous process
        _time.postValue(sdf.format(date))
    }

    val currentStatus = _status.value
}