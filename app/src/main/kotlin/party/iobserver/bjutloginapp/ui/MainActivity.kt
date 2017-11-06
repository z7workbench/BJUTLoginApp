package party.iobserver.bjutloginapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.format.Formatter
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
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
    var fee = -1
    var time = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        toolbar.title = ""

        login.setOnClickListener {
            NetworkUtils.login(User(name = app.prefs.getString("user", ""),
                    password = app.prefs.getString("password", "")), object : UIBlock {
                override val context: Context = this@MainActivity

                override fun onPrepare() {
                    now_flux.text = Formatter.formatFileSize(context, 0L)
                    status = LogStatus.SYNCING
                    status_view.text = status.description
                    login.isEnabled = false
                    logout.isEnabled = false
                    sync.isEnabled = false
                }

                override fun onFailure(exception: IOException) {
                    now_flux.text = "NaN"
                    status = LogStatus.ERROR
                    status_view.text = status.description
                    time_view.text
                    login.isEnabled = true
                    sync.isEnabled = true
                }

                override fun onResponse(bodyString: String?) {
                    syncing()
                }

                override fun onFinished() {
                    val time = System.currentTimeMillis()
                    val date = Date(time)
                    time_view.text = sdf.format(date)
                }
            })
        }

        sync.setOnClickListener { syncing() }

        logout.setOnClickListener {
            NetworkUtils.logout(object : UIBlock {
                override val context: Context = this@MainActivity

                override fun onPrepare() {
                    status = LogStatus.SYNCING
                    status_view.text = status.description
                    login.isEnabled = false
                    logout.isEnabled = false
                    sync.isEnabled = false
                }

                override fun onFailure(exception: IOException) {
                    syncing()
                }

                override fun onResponse(bodyString: String?) {
                    status = LogStatus.OFFLINE
                    status_view.text = status.description
                    login.isEnabled = true
                    sync.isEnabled = true
                }

                override fun onFinished() {
                    val time = System.currentTimeMillis()
                    val date = Date(time)
                    time_view.text = sdf.format(date)
                }
            })
        }

        detail.setOnClickListener {
            if (fee == -1 || time == -1) {
                alert {
                    title = "Oops! "
                    message = "You haven't synced yet! "
                    positiveButton("OK") { }
                }.show()
            } else {
                alert {
                    title = "Info"
                    positiveButton("OK") { }
                }.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        user.text = app.prefs.getString("user", "null")
        pack.text = app.prefs.getString("pack", "NaN")
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

    private fun syncing() {
        NetworkUtils.sync(object : UIBlock {
            override val context = this@MainActivity

            override fun onPrepare() {
                status = LogStatus.SYNCING
                status_view.text = status.description
                login.isEnabled = false
                logout.isEnabled = false
                sync.isEnabled = false
            }

            override fun onFailure(exception: IOException) {
                now_flux.text = "NaN"
                status = LogStatus.ERROR
                status_view.text = status.description
                time_view.text
                login.isEnabled = true
                sync.isEnabled = true
            }

            override fun onResponse(bodyString: String?) {
                val regex = """time='(.*?)';flow='(.*?)';fsele=1;fee='(.*?)'""".toRegex()
                if (bodyString == null) {
                    now_flux.text = "NaN"
                    status = LogStatus.ERROR
                    status_view.text = status.description
                    login.isEnabled = true
                    sync.isEnabled = true
                } else {
                    val result = regex.find(bodyString)
                    if (result == null || result.groups.isEmpty()) {
                        now_flux.text = "NaN"
                        status = LogStatus.ERROR
                        status_view.text = status.description
                        login.isEnabled = true
                        sync.isEnabled = true
                    } else {
                        time = result.groups[1]?.value?.toInt()!!
                        val flow = result.groups[2]?.value?.toLong()!!
                        fee = result.groups[3]?.value?.toInt()!!
                        now_flux.text = Formatter.formatFileSize(this@MainActivity, flow)
                        status = LogStatus.ONLINE
                        status_view.text = status.description
                        var fl = when (app.prefs.getString("pack", "NaN")) {
                            "8 GB" -> 8F
                            "25 GB" -> 25F
                            "30 GB" -> 30F
                            else -> -1F
                        }
                        if (fl != -1F) {
                            val percent = flow.toFloat() / (fl * 1024 * 1024 * 1024)
                            if (percent < 1)
                                progress.percent = percent
                            else
                                progress.percent = 100F
                        }
                        logout.isEnabled = true
                        sync.isEnabled = true
                    }
                }
            }

            override fun onFinished() {
                val time = System.currentTimeMillis()
                val date = Date(time)
                time_view.text = sdf.format(date)
            }
        })
    }
}
