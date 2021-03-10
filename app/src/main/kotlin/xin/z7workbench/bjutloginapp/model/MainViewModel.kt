package xin.z7workbench.bjutloginapp.model

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import xin.z7workbench.bjutloginapp.LoginApp
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.network.OkHttpNetwork
import xin.z7workbench.bjutloginapp.network.DataProcessBlock
import xin.z7workbench.bjutloginapp.prefs.AppSettingsOperator
import xin.z7workbench.bjutloginapp.prefs.Keys
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
    private val _usedTime = MutableLiveData<Int>()
    private val _flux = MutableLiveData<String>()
    private val _fee = MutableLiveData<Float>()
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//    val operator = getApplication<LoginApp>().operator
    val dataStore = getApplication<LoginApp>().dataStore

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
    val usedTime: LiveData<Int>
        get() = _usedTime
    val flux: LiveData<String>
        get() = _flux
    val fee: LiveData<Float>
        get() = _fee
    val themeIndies by lazy { getApplication<LoginApp>().resources.getStringArray(R.array.theme_index) }
    val langIndies by lazy { getApplication<LoginApp>().resources.getStringArray(R.array.language_values) }

    init {
        viewModelScope.launch {
            updateUserSettings(true)
        }
        _usedTime.value = -1
        _flux.value = ""
        _fee.value = -1F
        _status.value = LogStatus.OFFLINE
    }

    private fun error() {
        _usedTime.value = -1
        _flux.value = ""
        _fee.value = -1F
        _status.value = LogStatus.ERROR
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
            AppSettingsOperator.setCurrentId(dataStore, id)
            updateUserSettings(true)
        }
    }

    fun changeIpMode(mode: IpMode) {
        viewModelScope.launch {
            AppSettingsOperator.setIpMode(dataStore, mode)
            updateUserSettings()
        }
    }

    private suspend fun updateUserSettings(isRefreshUser: Boolean = false) {
        AppSettingsOperator.userSettings(dataStore).collect {
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

    fun online() {
        _status.value = LogStatus.LOGGING
        val block = object : DataProcessBlock {
            override fun onFailure(exception: IOException) {
                error()
            }

            override fun onResponse(bundle: Bundle) {
                syncing()
            }

            override fun onFinished() = updateSyncedTime()
        }
        OkHttpNetwork.login(_user.value as User, _ipMode.value as IpMode, block)
    }

    fun syncing(context: Context? = null, block: () -> Unit = {}) {
        _status.postValue(LogStatus.SYNCING)
        val bl = object : DataProcessBlock {
            override fun onFailure(exception: IOException) {
                error()
                context?.runOnUiThread { block() }
            }

            override fun onResponse(bundle: Bundle) {
                if (bundle["status"] as Boolean) {
                    _usedTime.postValue(bundle["time"] as Int)
                    _flux.postValue(formatByteSize(bundle["flow"] as Long * 1024))
                    _fee.postValue(bundle["fee"] as Float)
                    _status.postValue(LogStatus.ONLINE)
                } else {
                    _status.postValue(LogStatus.OFFLINE)
                }
                context?.runOnUiThread { block() }
            }

            override fun onFinished() = updateSyncedTime()
        }
        OkHttpNetwork.sync(_ipMode.value as IpMode, bl)
    }

    fun offline() {
        val block = object : DataProcessBlock {
            override fun onFailure(exception: IOException) {
                error()
            }

            override fun onResponse(bundle: Bundle) {
                _status.value = LogStatus.OFFLINE
            }

            override fun onFinished() = updateSyncedTime()
        }
        OkHttpNetwork.sync(_ipMode.value as IpMode, block)
    }
}