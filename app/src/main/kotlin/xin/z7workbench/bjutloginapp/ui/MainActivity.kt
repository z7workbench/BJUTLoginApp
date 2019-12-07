package xin.z7workbench.bjutloginapp.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.net.TrafficStats
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.text.format.Formatter
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import org.jetbrains.anko.startActivity
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.ActivityMainBinding
import xin.z7workbench.bjutloginapp.util.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    val tag = "MainActivity"
    var status = LogStatus.OFFLINE
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    var oldUp = TrafficStats.getTotalTxBytes()
    var oldDown = TrafficStats.getTotalRxBytes()
    var oldTime = System.currentTimeMillis()
    var emsg = ""
    var currentId = -1
    var currentName = ""
    var currentPack = -1
    lateinit var binding: ActivityMainBinding

    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                val arr = msg.obj as Array<String>
                binding.upSpeed.text = arr[0]
                binding.downSpeed.text = arr[1]
            }
        }
    }

    val task = object : TimerTask() {
        override fun run() {
            val msg = Message.obtain(handler)
            msg.what = 1
            val newUp = TrafficStats.getTotalTxBytes()
            val newDown = TrafficStats.getTotalRxBytes()
            val newTime = System.currentTimeMillis()
            msg.obj = arrayOf(Formatter.formatFileSize(this@MainActivity, (newUp - oldUp) * 1000 / (newTime - oldTime)) + "/s",
                    Formatter.formatFileSize(this@MainActivity, (newDown - oldDown) * 1000 / (newTime - oldTime)) + "/s")
            handler.sendMessage(msg)
            oldUp = newUp
            oldDown = newDown
            oldTime = newTime
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.toolbar.title = ""

        Timer().schedule(task, 1000L, 3000L)

        binding.hideAndShow.setOnClickListener {
            hideOrNot(true)
        }

        currentId = app.prefs.getInt("current_user", -1)
        val currentUser = app.appDatabase.userDao().find(currentId)
        currentName = currentUser.firstOrNull()?.name ?: getString(R.string.unknown)
        currentPack = currentUser.firstOrNull()?.pack ?: -1
/*        pack.text = when (currentPack) {
            0 -> resources.getStringArray(R.array.pack)[0]
            1 -> resources.getStringArray(R.array.pack)[1]
            2 -> resources.getStringArray(R.array.pack)[2]
            else -> getString(R.string.unknown)
        }*/
        binding.pack.text = "${currentPack} GB"

        binding.login.setOnClickListener {
            if (currentUser.isEmpty()) {
                Snackbar.make(binding.mainLayout, R.string.not_set, 3000)
                        .setAction(resources.getString(R.string.goto_settings)) {
                            startActivity<UsersActivity>()
                        }
                        .show()
                return@setOnClickListener
            }
            NetworkUtils.login(currentUser.first(), app.prefs.getBoolean("website", true), object : UIBlock {
                override val context: Context = this@MainActivity

                override fun onPrepare() = this@MainActivity.onPrepared()

                override fun onFailure(exception: IOException) {
                    emsg = exception.message ?: ""
                    binding.apply {
                        errMsg.visibility = View.VISIBLE
                        nowFlux.text = resources.getString(R.string.unknown)
                        timeView.text = resources.getString(R.string.unknown)
                        feeView.text = resources.getString(R.string.unknown)
                        progress.percent = 0F
                        statusView.text = resources.getString(status.description)
                        lastView.text
                        login.isEnabled = true
                        sync.isEnabled = true
                    }
                    status = LogStatus.ERROR
                }

                override fun onResponse(bodyString: String?) {
                    syncing()
                }

                override fun onFinished() {
                    val time = System.currentTimeMillis()
                    val date = Date(time)
                    binding.lastView.text = sdf.format(date)
                }
            })
        }

        binding.sync.setOnClickListener { syncing() }

        binding.logout.setOnClickListener {
            NetworkUtils.logout(object : UIBlock {
                override val context: Context = this@MainActivity

                override fun onPrepare() = this@MainActivity.onPrepared()

                override fun onFailure(exception: IOException) {
                    emsg = exception.message ?: ""
                    binding.errMsg.visibility = View.VISIBLE
                    syncing()
                }

                override fun onResponse(bodyString: String?) {
                    status = LogStatus.OFFLINE
                    binding.apply {
                        statusView.text = resources.getString(status.description)
                        progress.percent = 0F
                        login.isEnabled = true
                        sync.isEnabled = true
                    }
                }

                override fun onFinished() {
                    val time = System.currentTimeMillis()
                    val date = Date(time)
                    binding.lastView.text = sdf.format(date)
                }
            })
        }

        binding.errMsg.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this).create()
            alertDialog.show()
            val window = alertDialog.window
            window?.setContentView(R.layout.dialog_error)
            val tv = window?.findViewById<TextView>(R.id.dialog_message)
            val btn = window?.findViewById<Button>(R.id.dialog_ok)
            tv?.text = emsg
            btn?.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        binding.wifiSlide.setOnClickListener {

        }

        oldUp = TrafficStats.getTotalTxBytes()
        oldDown = TrafficStats.getTotalRxBytes()
        oldTime = System.currentTimeMillis()
    }

    override fun onResume() {
        super.onResume()
        currentId = app.prefs.getInt("current_user", -1)
        val currentUser = app.appDatabase.userDao().find(currentId)
        currentName = currentUser.firstOrNull()?.name ?: getString(R.string.unknown)
        currentPack = currentUser.firstOrNull()?.pack ?: -1
/*        pack.text = when (currentPack) {
            0 -> resources.getStringArray(R.array.pack)[0]
            1 -> resources.getStringArray(R.array.pack)[1]
            2 -> resources.getStringArray(R.array.pack)[2]
            else -> getString(R.string.unknown)
        }*/
        binding.pack.text = "${currentPack} GB"

        syncing()
        hideOrNot(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when (id) {
            R.id.action_settings -> {
                startActivity<SettingsActivity>()
            }
            R.id.action_help -> {
                AlertDialog.Builder(this)
                        .setTitle(R.string.help_alert_title)
                        .setIcon(R.drawable.ic_help_outline_acc_24dp)
                        .setMessage(R.string.help_alert_des)
                        .setNeutralButton(R.string.help_alert_button) { dialog, which ->

                        }
                        .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun onPrepared() {
        emsg = ""
        status = LogStatus.SYNCING
        binding.apply {
            errMsg.visibility = View.GONE
            nowFlux.text = resources.getString(R.string.unknown)
            timeView.text = resources.getString(R.string.unknown)
            feeView.text = resources.getString(R.string.unknown)
            progress.percent = 0F
            statusView.text = resources.getString(status.description)
            login.isEnabled = false
            logout.isEnabled = false
            sync.isEnabled = false
        }
    }

    private fun hideOrNot(edit: Boolean) {
        val hide = app.prefs.getBoolean("hide", true)
        if ((!hide && edit) || (hide && !edit)) {
            binding.hideAndShow.setImageDrawable(resources.getDrawable(R.drawable.ic_show))
            binding.user.text = resources.getString(R.string.main_hide)
            if (edit) {
                val editor = app.prefs.edit()
                editor.putBoolean("hide", true)
                editor.apply()
            }

        } else {
            binding.hideAndShow.setImageDrawable(resources.getDrawable(R.drawable.ic_hide))
            binding.user.text = currentName
            if (edit) {
                val editor = app.prefs.edit()
                editor.putBoolean("hide", false)
                editor.apply()
            }
        }
    }

    private fun syncing() {
        NetworkUtils.sync(app.prefs.getBoolean("website", true), object : UIBlock {
            override val context = this@MainActivity

            override fun onPrepare() = this@MainActivity.onPrepared()

            override fun onFailure(exception: IOException) {
                emsg = exception.message ?: ""
                binding.apply {
                    errMsg.visibility = View.VISIBLE
                    nowFlux.text = resources.getString(R.string.unknown)
                    timeView.text = resources.getString(R.string.unknown)
                    feeView.text = resources.getString(R.string.unknown)
                    progress.percent = 0F
                    statusView.text = resources.getString(status.description)
                    lastView.text
                    login.isEnabled = true
                    sync.isEnabled = true
                }
                status = LogStatus.ERROR
            }

            override fun onResponse(bodyString: String?) {
                val regex = """time='(.*?)';flow='(.*?)';fsele=1;fee='(.*?)'""".toRegex()
                if (bodyString == null) {
                    binding.apply {
                        nowFlux.text = resources.getString(R.string.unknown)
                        timeView.text = resources.getString(R.string.unknown)
                        feeView.text = resources.getString(R.string.unknown)
                        progress.percent = 0F
                        statusView.text = resources.getString(status.description)
                        login.isEnabled = true
                        sync.isEnabled = true
                    }

                    status = LogStatus.ERROR
                } else {
                    val result = regex.find(bodyString)
                    if (result == null || result.groups.isEmpty()) {
                        binding.apply {
                            nowFlux.text = resources.getString(R.string.unknown)
                            timeView.text = resources.getString(R.string.unknown)
                            feeView.text = resources.getString(R.string.unknown)
                            progress.percent = 0F
                        }

                        if (!bodyString.contains("""location.href="https://wlgn.bjut.edu.cn/0.htm""")) {
                            status = LogStatus.ERROR
                            binding.statusView.text = resources.getString(status.description)
                        } else {
                            status = LogStatus.OFFLINE
                            binding.statusView.text = resources.getString(status.description)
                        }
                        binding.login.isEnabled = true
                        binding.sync.isEnabled = true
                    } else {
                        val time = result.groups[1]?.value?.toInt()!!
                        val flow = result.groups[2]?.value?.toLong()!!
                        val fee = result.groups[3]?.value?.toDouble()!!
                        binding.apply {
                            nowFlux.text = formatByteSize(flow * 1024)
//                            nowFlux.text = flow.toString()
                            timeView.text = "$time min"
                            feeView.text = """￥${fee / 10000}"""
                            statusView.text = resources.getString(status.description)
                        }
                        status = LogStatus.ONLINE
                        val fl = currentPack.toFloat()
                        if (fl != -1F) {
                            var percent = flow.toFloat() / (fl * 1024 * 1024) * 100
                            if (percent > 100F) {
                                percent = 100F
                            }
                            val animator = ObjectAnimator.ofFloat(binding.progress, "percent", percent)
                            animator.duration = 1500L
                            animator.interpolator = DecelerateInterpolator(2F)
                            animator.start()
                        }
                        binding.logout.isEnabled = true
                        binding.sync.isEnabled = true
                    }
                }
            }

            override fun onFinished() {
                val time = System.currentTimeMillis()
                val date = Date(time)
                binding.lastView.text = sdf.format(date)
            }
        })

        Log.d("$tag e", emsg)
    }
}
