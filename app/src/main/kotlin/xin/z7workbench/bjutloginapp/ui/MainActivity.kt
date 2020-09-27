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
import androidx.recyclerview.widget.LinearLayoutManager
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.ActivityMainBinding
import xin.z7workbench.bjutloginapp.databinding.LoginCardBinding
import xin.z7workbench.bjutloginapp.model.MainViewModel
import xin.z7workbench.bjutloginapp.model.User
import xin.z7workbench.bjutloginapp.util.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BasicActivity() {
    val viewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.bottomAppBar)
        binding.swipeRefresh.setColorSchemeColors(R.attr.colorAccent)
        binding.swipeRefresh.setDistanceToTriggerSync(200)

        binding.swipeRefresh.setOnRefreshListener {
//            TODO("refresh")
            binding.swipeRefresh.isRefreshing = false
        }

//        prevent recycler from scrolling
//        val llm = object : LinearLayoutManager(this) {
//            override fun canScrollVertically() = false
//        }

        Snackbar.make(binding.swipeRefresh, NetworkUtils.getWifiSSID(this), Snackbar.LENGTH_LONG)
//                .setAnchorView(binding.bottomAppBar)
                .show()
    }

    override fun onResume() {
        super.onResume()
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
}
