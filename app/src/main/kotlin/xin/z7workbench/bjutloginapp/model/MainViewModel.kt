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
    private val _currentId = MutableLiveData<Int>()
    val status: LiveData<LogStatus>
        get() = _status
    val currentId: LiveData<Int>
        get() = _currentId
    val users = getApplication<LoginApp>().appDatabase.userDao().all()

    val user by lazy { dao.find(_currentId.value!!) }
//    val user = dao.find(_currentId.value!!)

    init {
        _status.value = LogStatus.OFFLINE
        _currentId.value = getApplication<LoginApp>().prefs.getInt("current_user", -1)
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
        Log.d(tag, user.value.toString())
    }
}