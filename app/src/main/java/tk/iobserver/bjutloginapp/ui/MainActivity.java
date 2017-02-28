package tk.iobserver.bjutloginapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tk.iobserver.bjutloginapp.R;
import tk.iobserver.bjutloginapp.util.Operator;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private SharedPreferences prefs;
    Operator operator = new Operator(TAG);
    @BindView(R.id.main_toolbar) Toolbar toolbar;
    @BindView(R.id.main_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.main_tv_usedFlux) TextView usedFlux;
    @BindView(R.id.fab_refresh) FloatingActionButton refreshFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        refreshFAB.setOnClickListener(view -> {
            operator.refresh(coordinatorLayout, usedFlux, this);
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        operator.refresh(coordinatorLayout, usedFlux, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.action_login: {
                operator.login(coordinatorLayout, prefs.getString("user", null), prefs.getString("password", null));
            }
            break;
            case R.id.action_logout: {
                operator.logout(coordinatorLayout);
            }
            break;
            case R.id.action_sync: {
                operator.refresh(coordinatorLayout, usedFlux, this);
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

}
