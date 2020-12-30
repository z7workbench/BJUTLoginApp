package xin.z7workbench.bjutloginapp.model

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import xin.z7workbench.bjutloginapp.LoginApp
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.network.OkHttpNetwork
import xin.z7workbench.bjutloginapp.network.OkHttpDataBlock
import xin.z7workbench.bjutloginapp.util.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val tag = "MainViewModel"
    private val dao = getApplication<LoginApp>().appDatabase.userDao()
    private val _status = MutableLiveData<LogStatus>()
    private val _currentId = MutableLiveData<Int>()
    private val _user = MutableLiveData<User>()
    private val _ipMode = MutableLiveData<IpMode>()
    private val _time = MutableLiveData<String>()
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val operator = getApplication<LoginApp>().operator

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
    val themeIndies by lazy { getApplication<LoginApp>().resources.getStringArray(R.array.theme_index) }
    val langIndies by lazy { getApplication<LoginApp>().resources.getStringArray(R.array.language_values) }

    init {
        viewModelScope.launch {
            updateUserSettings(true)
        }
        _status.value = LogStatus.OFFLINE
    }

    fun offline() {
        OkHttpNetwork.sync(_ipMode.value as IpMode, object : OkHttpDataBlock {
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
        OkHttpNetwork.login(_user.value as User, _ipMode.value as IpMode, object : OkHttpDataBlock {
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
        OkHttpNetwork.sync(_ipMode.value as IpMode, object : OkHttpDataBlock {
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

    fun insertUser(user: User) {
        viewModelScope.launch {
            dao.insert(user)
            updateUserSettings()
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            dao.update(user)
            updateUserSettings()
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            dao.delete(user)
            updateUserSettings()
        }
    }

    fun changeUserId(id: Int) {
        viewModelScope.launch {
            operator.setCurrentId(id)
            updateUserSettings(true)
        }
    }

    fun changeIpMode(mode: IpMode) {
        viewModelScope.launch {
            operator.setIpMode(mode)
            updateUserSettings()
        }
    }

    private suspend fun updateUserSettings(isRefreshUser: Boolean = false) {
        operator.userSettings.collect {
            _ipMode.postValue(it.ipMode)
            _currentId.postValue(it.current)
            if (isRefreshUser) _user.postValue(dao.find(it.current))
        }
    }

    private fun updateSyncedTime() {
        val time = System.currentTimeMillis()
        val date = Date(time)
        // use MutableLiveData#postValue instead of MutableLiveData#setValue in asynchronous process
        _time.postValue(sdf.format(date))
    }

    val currentStatus = _status.value
    fun doNothing() = nothing()
}