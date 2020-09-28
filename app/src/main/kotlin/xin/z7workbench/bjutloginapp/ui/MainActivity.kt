package xin.z7workbench.bjutloginapp.ui

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.view.*
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.*
import xin.z7workbench.bjutloginapp.model.MainViewModel
import xin.z7workbench.bjutloginapp.util.*

class MainActivity : BasicActivity() {
    val viewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportFragmentManager.commit {
            replace(R.id.mainContainer, MainFragment::class.java, Bundle())
        }

    }

    override fun onResume() {
        super.onResume()
    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        val id = item.itemId
//
//        when (id) {
//            R.id.action_settings -> {
//                startActivity(Intent(this, SettingsActivity::class.java))
//            }
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
//        }
//
//        return super.onOptionsItemSelected(item)
//    }
}
