package top.z7workbench.bjutloginapp.model

import android.app.Application
import android.net.wifi.WifiInfo
import android.os.Build
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import top.z7workbench.bjutloginapp.LoginApp
import top.z7workbench.bjutloginapp.network.*
import top.z7workbench.bjutloginapp.network.NetworkGlobalObject.body
import top.z7workbench.bjutloginapp.prefs.AppSettingsOperator
import top.z7workbench.bjutloginapp.util.*
import java.text.SimpleDateFormat
import java.util.*

class UserViewModel(app: Application) : AndroidViewModel(app) {
    private val tag = "MainViewModel"
    private val dao = getApplication<LoginApp>().appDatabase.userDao()
    private val _status = MutableStateFlow(LogStatus.OFFLINE)
    private val _currentId = MutableLiveData<Int>()
    private val _user = MutableLiveData<User>()
    private val _ipMode = MutableLiveData<IpMode>()
    private val _time = MutableLiveData<String>()
    private val _stats = MutableStateFlow(NetData())
    private val _swipe = MutableLiveData<Boolean>()
    private val _ssid = MutableLiveData<String>()
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val dataStore = getApplication<LoginApp>().dataStore

    val status = _status.debounce(200L).asLiveData()
    val currentId: LiveData<Int>
        get() = _currentId
    val users = dao.all()
    val user: LiveData<User>
        get() = _user
    val ipMode: LiveData<IpMode>
        get() = _ipMode
    val time: LiveData<String>
        get() = _time
    val stats = _stats.debounce(200L).asLiveData()
    val swipe: LiveData<Boolean>
        get() = _swipe

    private val mode get() = ipMode.value ?: IpMode.WIRELESS
    private val default = NetData()

    init {
        viewModelScope.launch {
            updateUserSettings(true)
        }
        _swipe.value = true
        _stats.value = default
        _status.value = LogStatus.OFFLINE
    }

    private fun error() {
        _status.value = LogStatus.ERROR
        _stats.value = default
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

    val currentStatus get() = _status.value

    fun online() {
        _status.value = LogStatus.LOGGING
        viewModelScope.launch {
            exception {
                when (mode) {
                    IpMode.WIRELESS -> {
                        val service = WirelessService.service
                        service.login(user.value?.name ?: "", user.value?.password ?: "")
                    }
                    IpMode.WIRED_IPV6 -> {
                        val service = Wired6Service.service
                        service.login(body(mode, user.value ?: User()))
                    }
                    else -> {
                        val service = Wired4Service.service
                        service.login(body(mode, user.value ?: User()))
                    }
                }
            }
            syncing()
        }
    }

    fun sync() {
        _status.value = LogStatus.SYNCING
        _swipe.value = false
        viewModelScope.launch {
            syncing()
            _swipe.postValue(true)
        }
    }

    private suspend fun syncing() {
        exception {
            val returnBody = when (mode) {
                IpMode.WIRED_IPV6 -> {
                    Wired6Service.service.sync()
                }
                else -> {
                    Wired4Service.service.sync()
                }
            }
//        returnBody.collect {
            val bundle = processSyncData(returnBody, user.value?.pack ?: 30)
            val statusCode = bundle.status
            if (statusCode) {
                _stats.value = bundle.data
                _status.value = LogStatus.ONLINE
            } else error()
//        }
        }
    }

    fun offline() {
        _status.value = LogStatus.OFFLINE
        viewModelScope.launch {
            exception {
                when (mode) {
                    IpMode.WIRELESS -> {
                        val service = WirelessService.service
                        service.logout()
                    }
                    IpMode.WIRED_IPV6 -> {
                        val service = Wired6Service.service
                        service.logout()
                    }
                    else -> {
                        val service = Wired4Service.service
                        service.logout()
                    }
                }
            }
        }
    }

    private suspend fun exception(f: suspend () -> Unit) {
        try {
            f()
        } catch (e: Exception) {
            e.printStackTrace()
            error()
        }
    }
}