package top.z7workbench.bjutloginapp.model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import top.z7workbench.bjutloginapp.LoginApp
import top.z7workbench.bjutloginapp.network.NetworkGlobalObject
import top.z7workbench.bjutloginapp.network.Wired4Service
import top.z7workbench.bjutloginapp.network.Wired6Service
import top.z7workbench.bjutloginapp.network.WirelessService
import top.z7workbench.bjutloginapp.prefs.AppSettingsOperator
import top.z7workbench.bjutloginapp.util.IpMode
import top.z7workbench.bjutloginapp.util.LogStatus
import top.z7workbench.bjutloginapp.util.NetworkState
import top.z7workbench.bjutloginapp.util.processSyncData
import java.text.SimpleDateFormat
import java.util.*

class StatusViewModel(val app: Application) : AndroidViewModel(app) {
    private val default = NetData()
    private val _validatedStatus = MutableStateFlow(BundledState())
    private val _status = MutableStateFlow(LogStatus.OFFLINE)
    private val _ipMode = MutableStateFlow<IpMode>(IpMode.default)
    private val _time = MutableStateFlow<String>("")
    private val _stats = MutableStateFlow(default)
    private val _swipe = MutableStateFlow<Boolean>(false)
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val dataStore = getApplication<LoginApp>().dataStore

    val validatedStatus = _validatedStatus.debounce(200L)
        .asLiveData()
    val status = _status.debounce(200L).asLiveData()
    val ipMode = _ipMode.debounce(200L).asLiveData()
    val time = _time.debounce(200L).asLiveData()
    val stats = _stats.debounce(200L).asLiveData()
    val swipe = _swipe.debounce(200L).asLiveData()
    private val mode get() = _ipMode.value
    val currentStatus get() = _status.value

    fun networkState(context: Context) {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = manager.activeNetwork
        val capabilities = manager.getNetworkCapabilities(network)
        if (capabilities != null) {
            val state = when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkState.WIFI
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkState.CELLULAR
                else -> NetworkState.OTHER
            }
            val vpn = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
            val validate = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            _validatedStatus.value = BundledState(state, vpn, validate)
        } else _validatedStatus.value = BundledState()
    }

    init {
        viewModelScope.launch {
            AppSettingsOperator.userSettings(dataStore).collect {
                _ipMode.value = it.ipMode
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

    private fun updateSyncedTime() {
        val time = System.currentTimeMillis()
        val date = Date(time)
        // use MutableLiveData#postValue instead of MutableLiveData#setValue in asynchronous process
        _time.value = sdf.format(date)
    }

    fun online(user: User) {
        _status.value = LogStatus.LOGGING
        viewModelScope.launch {
            exception {
                when (mode) {
                    IpMode.WIRELESS -> {
                        val service = WirelessService.service
                        service.login(user.name, user.password)
                    }
                    IpMode.WIRED_IPV6 -> {
                        val service = Wired6Service.service
                        service.login(NetworkGlobalObject.body(mode, user))
                    }
                    else -> {
                        val service = Wired4Service.service
                        service.login(NetworkGlobalObject.body(mode, user))
                    }
                }
            }
            syncing(user)
        }
    }

    fun sync(user: User) {
        _status.value = LogStatus.SYNCING
        _swipe.value = true
        viewModelScope.launch {
            syncing(user)
            _swipe.value = false
        }
    }

    private suspend fun syncing(user: User) {
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
            val bundle = processSyncData(returnBody, user.pack)
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


    private fun error() {
        _status.value = LogStatus.ERROR
        _stats.value = default
    }
}