package party.iobserver.bjutloginapp.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import party.iobserver.bjutloginapp.R


class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    val prefs: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        toolbar.title = ""

        //TODO FAB Action
        /*        refreshFAB.setOnClickListener(view -> {
            operator.refresh(coordinatorLayout, usedFlux, this);
        });*/

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

}
