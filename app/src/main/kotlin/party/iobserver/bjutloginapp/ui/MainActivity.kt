package party.iobserver.bjutloginapp.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.format.Formatter
import android.view.Menu
import android.view.MenuItem
import android.view.animation.DecelerateInterpolator
import kotlinx.android.synthetic.main.activity_main.*
import party.iobserver.bjutloginapp.R
import party.iobserver.bjutloginapp.model.User
import party.iobserver.bjutloginapp.util.LogStatus
import party.iobserver.bjutloginapp.util.NetworkUtils
import party.iobserver.bjutloginapp.util.UIBlock
import party.iobserver.bjutloginapp.util.app
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    var status = LogStatus.OFFLINE
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    var oldUp = TrafficStats.getTotalTxBytes()
    var oldDown = TrafficStats.getTotalRxBytes()
    var oldTime = System.currentTimeMillis()

    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                val arr = msg.obj as Array<String>
                up_speed.text = arr[0]
                down_speed.text = arr[1]
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
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        toolbar.title = ""

        Timer().schedule(task, 1000L, 3000L)

        hideAndShow.setOnClickListener {
            hideOrNot(true)
        }

        login.setOnClickListener {
            NetworkUtils.login(User(name = app.prefs.getString("user", resources.getString(R.string.unknown)),
                    password = app.prefs.getString("password", resources.getString(R.string.unknown))), object : UIBlock {
                override val context: Context = this@MainActivity

                override fun onPrepare() = this@MainActivity.onPrepared()

                override fun onFailure(exception: IOException) {
                    now_flux.text = resources.getString(R.string.unknown)
                    time_view.text = resources.getString(R.string.unknown)
                    fee_view.text = resources.getString(R.string.unknown)
                    progress.percent = 0F
                    status = LogStatus.ERROR
                    status_view.text = resources.getString(status.description)
                    last_view.text
                    login.isEnabled = true
                    sync.isEnabled = true
                }

                override fun onResponse(bodyString: String?) {
                    syncing()
                }

                override fun onFinished() {
                    val time = System.currentTimeMillis()
                    val date = Date(time)
                    last_view.text = sdf.format(date)
                }
            })
        }

        sync.setOnClickListener { syncing() }

        logout.setOnClickListener {
            NetworkUtils.logout(object : UIBlock {
                override val context: Context = this@MainActivity

                override fun onPrepare() = this@MainActivity.onPrepared()

                override fun onFailure(exception: IOException) {
                    syncing()
                }

                override fun onResponse(bodyString: String?) {
                    status = LogStatus.OFFLINE
                    status_view.text = resources.getString(status.description)
                    progress.percent = 0F
                    login.isEnabled = true
                    sync.isEnabled = true
                }

                override fun onFinished() {
                    val time = System.currentTimeMillis()
                    val date = Date(time)
                    last_view.text = sdf.format(date)
                }
            })
        }

        oldUp = TrafficStats.getTotalTxBytes()
        oldDown = TrafficStats.getTotalRxBytes()
        oldTime = System.currentTimeMillis()
    }

    override fun onResume() {
        super.onResume()
        pack.text = app.prefs.getString("pack", resources.getString(R.string.unknown))
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
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
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
        status = LogStatus.SYNCING
        now_flux.text = resources.getString(R.string.unknown)
        time_view.text = resources.getString(R.string.unknown)
        fee_view.text = resources.getString(R.string.unknown)
        progress.percent = 0F
        status_view.text = resources.getString(status.description)
        login.isEnabled = false
        logout.isEnabled = false
        sync.isEnabled = false
    }

    private fun hideOrNot(edit: Boolean) {
        val hide = app.prefs.getBoolean("hide", true)
        if ((!hide && edit) || (hide && !edit)) {
            hideAndShow.setImageDrawable(resources.getDrawable(R.drawable.ic_show))
            user.text = resources.getString(R.string.main_hide)
            if (edit) {
                val editor = app.prefs.edit()
                editor.putBoolean("hide", true)
                editor.apply()
            }

        } else {
            hideAndShow.setImageDrawable(resources.getDrawable(R.drawable.ic_hide))
            user.text = app.prefs.getString("user", resources.getString(R.string.unknown))
            if (edit) {
                val editor = app.prefs.edit()
                editor.putBoolean("hide", false)
                editor.apply()
            }
        }
    }

    private fun syncing() {
        NetworkUtils.sync(object : UIBlock {
            override val context = this@MainActivity

            override fun onPrepare() = this@MainActivity.onPrepared()

            override fun onFailure(exception: IOException) {
                now_flux.text = resources.getString(R.string.unknown)
                time_view.text = resources.getString(R.string.unknown)
                fee_view.text = resources.getString(R.string.unknown)
                progress.percent = 0F
                status = LogStatus.ERROR
                status_view.text = resources.getString(status.description)
                last_view.text
                login.isEnabled = true
                sync.isEnabled = true
            }

            override fun onResponse(bodyString: String?) {
                val regex = """time='(.*?)';flow='(.*?)';fsele=1;fee='(.*?)'""".toRegex()
                if (bodyString == null) {
                    now_flux.text = resources.getString(R.string.unknown)
                    time_view.text = resources.getString(R.string.unknown)
                    fee_view.text = resources.getString(R.string.unknown)
                    progress.percent = 0F
                    status = LogStatus.ERROR
                    status_view.text = resources.getString(status.description)
                    login.isEnabled = true
                    sync.isEnabled = true
                } else {
                    val result = regex.find(bodyString)
                    if (result == null || result.groups.isEmpty()) {
                        now_flux.text = resources.getString(R.string.unknown)
                        time_view.text = resources.getString(R.string.unknown)
                        fee_view.text = resources.getString(R.string.unknown)
                        progress.percent = 0F

                        val offline = """Please enter Account""".toRegex()
                        val result2 = offline.matches(bodyString)
                        if (!result2) {
                            status = LogStatus.ERROR
                            status_view.text = resources.getString(status.description)
                        } else {
                            status = LogStatus.OFFLINE
                            status_view.text = resources.getString(status.description)
                        }
                        login.isEnabled = true
                        sync.isEnabled = true
                    } else {
                        val time = result.groups[1]?.value?.toInt()!!
                        val flow = result.groups[2]?.value?.toLong()!!
                        val fee = result.groups[3]?.value?.toDouble()!!
                        now_flux.text = Formatter.formatFileSize(this@MainActivity, flow * 1024)
                        time_view.text = time.toString() + " min"
                        fee_view.text = "￥" + fee / 10000
                        status = LogStatus.ONLINE
                        status_view.text = resources.getString(status.description)
                        val fl = when (app.prefs.getString("pack", "NaN")) {
                            "8 GB" -> 8F
                            "25 GB" -> 25F
                            "30 GB" -> 30F
                            else -> -1F
                        }
                        if (fl != -1F) {
                            var percent = flow.toFloat() / (fl * 1024 * 1024) * 100
                            if (percent > 100F) {
                                percent = 100F
                            }
                            val animator = ObjectAnimator.ofFloat(progress, "percent", percent)
                            animator.duration = 1500L
                            animator.interpolator = DecelerateInterpolator(2F)
                            animator.start()
                        }
                        logout.isEnabled = true
                        sync.isEnabled = true
                    }
                }
            }

            override fun onFinished() {
                val time = System.currentTimeMillis()
                val date = Date(time)
                last_view.text = sdf.format(date)
            }
        })
    }
}
