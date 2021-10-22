package top.z7workbench.bjutloginapp.model

import android.app.Application
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
    private val tag = "UserViewModel"
    private val dao = getApplication<LoginApp>().appDatabase.userDao()
    private val _userData = MutableStateFlow(BundledUser())
    private val _user = MutableLiveData<User>()
    private val _currentId = MutableLiveData<Int>()
    private val dataStore = getApplication<LoginApp>().dataStore

    val currentId: LiveData<Int>
        get() = _currentId
    val users = dao.all()
    val user: LiveData<User>
        get() = _user
    val currentUser get() = user.value ?: User()

    init {
        viewModelScope.launch {
            updateUserSettings(true)
        }
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
            _currentId.postValue(it.current)
            if (isRefreshUser) _user.postValue(dao.find(it.current))
        }
    }

}