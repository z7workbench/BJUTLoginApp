package xin.z7workbench.bjutloginapp.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xin.z7workbench.bjutloginapp.LoginApp
import xin.z7workbench.bjutloginapp.database.UserDao
import xin.z7workbench.bjutloginapp.util.LogStatus
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

    val status: LiveData<LogStatus>
        get() = _status
    val currentId: LiveData<Int>
        get() = _currentId
    val users = dao.all()
    val user: LiveData<User>
        get() = _user

    init {
        _status.value = LogStatus.OFFLINE
        _user.value = dao.find(_currentId.value!!)
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
        _user.value = dao.find(_currentId.value!!)
    }
}