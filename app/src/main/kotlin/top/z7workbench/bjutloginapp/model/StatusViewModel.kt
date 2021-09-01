package top.z7workbench.bjutloginapp.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import top.z7workbench.bjutloginapp.util.NetworkState

class StatusViewModel : ViewModel() {
    private val _status = MutableStateFlow(BundledState())

    val status = _status.debounce(200L)
        .asLiveData()

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
            _status.value = BundledState(state, vpn, validate)
        } else _status.value = BundledState()
    }
}