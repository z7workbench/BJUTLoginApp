package xin.z7workbench.bjutloginapp.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.net.TrafficStats
import android.os.Bundle
import android.os.Handler
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.ActivityMainBinding
import xin.z7workbench.bjutloginapp.model.MainViewModel
import xin.z7workbench.bjutloginapp.model.User
import xin.z7workbench.bjutloginapp.util.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : LoginAppActivity() {
    val viewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val handler = Handler()
    var oldUp = TrafficStats.getTotalTxBytes()
    var oldDown = TrafficStats.getTotalRxBytes()
    var oldTime = System.currentTimeMillis()
    lateinit var currentUser: MutableList<User>
    lateinit var binding: ActivityMainBinding
    private val task = object : TimerTask() {
        override fun run() {
            val newUp = TrafficStats.getTotalTxBytes()
            val newDown = TrafficStats.getTotalRxBytes()
            val newTime = System.currentTimeMillis()
            handler.post {
                if (newTime != 0L && oldTime != 0L && newTime - oldTime != 0L) {
                    binding.upSpeed.text = "${formatByteSize((newUp - oldUp) * 1024 / (newTime - oldTime))}/s"
                    binding.downSpeed.text = "${formatByteSize((newDown - oldDown) * 1024 / (newTime - oldTime))}/s"
                }
                oldUp = newUp
                oldDown = newDown
                oldTime = newTime
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        Timer().scheduleAtFixedRate(task, 500L, 3500L)

        binding.toolbar.setNavigationOnClickListener {
            hideOrNot(true)
        }

        viewModel.currentId = app.prefs.getInt("current_user", -1)
        currentUser = app.appDatabase.userDao().find(viewModel.currentId)
        viewModel.currentName = currentUser.firstOrNull()?.name ?: getString(R.string.unknown)
        viewModel.currentPack = currentUser.firstOrNull()?.pack ?: -1
        binding.pack.text = "${viewModel.currentPack} GB"
        viewModel.status.observe(this) {
            binding.statusView.text = resources.getString(it.description)
        }

        binding.login.setOnClickListener {
            if (currentUser.isEmpty()) {
                Snackbar.make(binding.mainLayout, R.string.user_password_not_set, 3000)
                        .setAction(resources.getString(R.string.goto_settings)) {
                            startActivity(Intent(this, UsersActivity::class.java))
                        }
                        .show()
                return@setOnClickListener
            }
            NetworkUtils.login(currentUser.first(), app.prefs.getBoolean("website", true), object : UIBlock {
                override val context: Context = this@MainActivity

                override fun onPrepare() = this@MainActivity.onPrepared()

                override fun onFailure(exception: IOException) {
                    viewModel.emsg = exception.message ?: ""
                    viewModel.error()
                    binding.apply {
                        errMsg.visibility = View.VISIBLE
                        nowFlux.text = resources.getString(R.string.unknown)
                        timeView.text = resources.getString(R.string.unknown)
                        feeView.text = resources.getString(R.string.unknown)
                        progress.percent = 0F
                        numberBar.progress = 0
                        lastView.text
                        login.isEnabled = true
                        sync.isEnabled = true
                        exceeded.text = "${getString(R.string.exceeded)}${getString(R.string.unknown)}"
                        remained.text = "${getString(R.string.remaining)}${getString(R.string.unknown)}"

                    }
                }

                override fun onResponse(bodyString: String?) {
                    Snackbar.make(binding.constraint, getString(R.string.main_login_successful), Snackbar.LENGTH_SHORT).show()
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
            NetworkUtils.logout(app.prefs.getBoolean("website", true), object : UIBlock {
                override val context: Context = this@MainActivity

                override fun onPrepare() = this@MainActivity.onPrepared()

                override fun onFailure(exception: IOException) {
                    viewModel.emsg = exception.message ?: ""
                    binding.errMsg.visibility = View.VISIBLE
                    syncing()
                }

                override fun onResponse(bodyString: String?) {
                    viewModel.offline()
                    binding.apply {
                        progress.percent = 0F
                        numberBar.progress = 0
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
            tv?.text = viewModel.emsg
            btn?.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        binding.wifiSlide.setOnClickListener {

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentId = app.prefs.getInt("current_user", -1)
        currentUser = app.appDatabase.userDao().find(viewModel.currentId)
        viewModel.currentName = currentUser.firstOrNull()?.name ?: getString(R.string.unknown)
        viewModel.currentPack = currentUser.firstOrNull()?.pack ?: -1
        binding.pack.text = "${viewModel.currentPack} ${ByteSize.GB.display}"

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
                startActivity(Intent(this, SettingsActivity::class.java))
            }
//            TODO design action help
//            R.id.action_help -> {
//                AlertDialog.Builder(this)
//                        .setTitle(R.string.help_alert_title)
//                        .setIcon(R.drawable.ic_help_outline_acc_24dp)
//                        .setMessage(R.string.help_alert_des)
//                        .setNeutralButton(R.string.help_alert_button) { dialog, which ->
//
//                        }
//                        .show()
//            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun onPrepared() {
        viewModel.emsg = ""
        viewModel.syncing()
        binding.apply {
            errMsg.visibility = View.GONE
            nowFlux.text = resources.getString(R.string.unknown)
            timeView.text = resources.getString(R.string.unknown)
            feeView.text = resources.getString(R.string.unknown)
            progress.percent = 0F
            numberBar.progress = 0
            login.isEnabled = false
            logout.isEnabled = false
            sync.isEnabled = false
            exceeded.text = "${getString(R.string.exceeded)}${getString(R.string.unknown)}"
            remained.text = "${getString(R.string.remaining)}${getString(R.string.unknown)}"
        }
    }

    private fun hideOrNot(edit: Boolean) {
        val hide = app.prefs.getBoolean("hide", true)
        if ((!hide && edit) || (hide && !edit)) {
            binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_show)
            binding.toolbar.title = "${getString(R.string.main_user)}${getString(R.string.main_hide)}"
            if (edit) {
                app.prefs.edit { putBoolean("hide", true) }
            }
            Snackbar.make(binding.constraint, getString(R.string.main_hint_hide), Snackbar.LENGTH_SHORT)
                    .setBackgroundTintMode(PorterDuff.Mode.ADD)
                    .setAction(R.string.undo) {
                        hideOrNot(true)
                    }.show()

        } else {
            binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_hide)
            binding.toolbar.title = "${getString(R.string.main_user)}${viewModel.currentName}"
            if (edit) {
                app.prefs.edit { putBoolean("hide", false) }
            }
            Snackbar.make(binding.constraint, getString(R.string.main_hint_reveal), Snackbar.LENGTH_SHORT)
                    .setBackgroundTintMode(PorterDuff.Mode.ADD)
                    .setAction(R.string.undo) {
                        hideOrNot(true)
                    }.show()
        }
    }

    private fun syncing() {
        NetworkUtils.sync(app.prefs.getBoolean("website", true), object : UIBlock {
            override val context = this@MainActivity

            override fun onPrepare() = this@MainActivity.onPrepared()

            override fun onFailure(exception: IOException) {
                viewModel.emsg = exception.message ?: ""
                viewModel.error()
                binding.apply {
                    errMsg.visibility = View.VISIBLE
                    nowFlux.text = resources.getString(R.string.unknown)
                    timeView.text = resources.getString(R.string.unknown)
                    feeView.text = resources.getString(R.string.unknown)
                    progress.percent = 0F
                    numberBar.progress = 0
                    lastView.text
                    login.isEnabled = true
                    sync.isEnabled = true
                    exceeded.text = "${getString(R.string.exceeded)}${getString(R.string.unknown)}"
                    remained.text = "${getString(R.string.remaining)}${getString(R.string.unknown)}"
                }
            }

            override fun onResponse(bodyString: String?) {
                val regex = """time='(.*?)';flow='(.*?)';fsele=1;fee='(.*?)'""".toRegex()
                if (bodyString == null) {
                    viewModel.error()
                    binding.apply {
                        nowFlux.text = resources.getString(R.string.unknown)
                        timeView.text = resources.getString(R.string.unknown)
                        feeView.text = resources.getString(R.string.unknown)
                        progress.percent = 0F
                        numberBar.progress = 0
                        login.isEnabled = true
                        sync.isEnabled = true
                        exceeded.text = "${getString(R.string.exceeded)}${getString(R.string.unknown)}"
                        remained.text = "${getString(R.string.remaining)}${getString(R.string.unknown)}"
                    }
                } else {
                    val result = regex.find(bodyString)
                    if (result == null || result.groups.isEmpty()) {
                        binding.apply {
                            nowFlux.text = resources.getString(R.string.unknown)
                            timeView.text = resources.getString(R.string.unknown)
                            feeView.text = resources.getString(R.string.unknown)
                            progress.percent = 0F
                            numberBar.progress = 0
                            exceeded.text = "${getString(R.string.exceeded)}${getString(R.string.unknown)}"
                            remained.text = "${getString(R.string.remaining)}${getString(R.string.unknown)}"
                        }

                        if (!bodyString.contains("""location.href="https://wlgn.bjut.edu.cn/0.htm""")) {
                            viewModel.error()
                        } else {
                            viewModel.offline()
                        }
                        binding.login.isEnabled = true
                        binding.sync.isEnabled = true
                    } else {
                        val time = result.groups[1]?.value?.toInt() ?: -1
                        val flow = result.groups[2]?.value?.toLong() ?: -1L
                        val fee = result.groups[3]?.value?.toFloat() ?: -1F
                        viewModel.online()
                        val bundle = exceededByteSizeBundle(flow, viewModel.currentPack, fee)
                        val numberBarProgress = bundle["percent"] as Int
                        val exceededFlow = bundle["exceeded"] as String
                        val remainedFlow = bundle["remained"] as String
                        // Change UI
                        binding.apply {
                            nowFlux.text = formatByteSize(flow * 1024)
                            timeView.text = "$time ${getString(R.string.minutes)}"
                            feeView.text = """￥${fee / 10000}"""
                            exceeded.text = "${getString(R.string.exceeded)}$exceededFlow"
                            remained.text = "${getString(R.string.remaining)}$remainedFlow"
                            logout.isEnabled = true
                            sync.isEnabled = true
                        }
                        // Deal with ColorfulRingProgressView and NumberProgressBar
                        val fl = viewModel.currentPack.toFloat()
                        if (fl >= 0F && flow >= 0L) {
                            var percent = flow.toFloat() / (fl * 1024 * 1024) * 100
                            if (percent > 100F) {
                                percent = 100F
                            }
                            val animator = ObjectAnimator.ofFloat(binding.progress, "percent", percent)
                            animator.duration = 1500L
                            animator.interpolator = DecelerateInterpolator(2F)
                            animator.start()

                            val PGBanimator = ObjectAnimator.ofInt(binding.numberBar, "progress", numberBarProgress)
                            PGBanimator.duration = 1500L
                            PGBanimator.interpolator = DecelerateInterpolator(0.5F)
                            PGBanimator.start()
                            binding.numberBar
                        } else {
                            binding.apply {
                                progress.percent = 0F
                                numberBar.progress = 0
                            }
                        }
                    }
                }
            }

            override fun onFinished() {
                val time = System.currentTimeMillis()
                val date = Date(time)
                binding.lastView.text = sdf.format(date)
            }
        })

        Log.d("${viewModel.tag} e", viewModel.emsg)
    }
}
