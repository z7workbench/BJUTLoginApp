package tk.iobserver.bjutloginapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tk.iobserver.bjutloginapp.R;
import tk.iobserver.bjutloginapp.widget.NoteCard;
import tk.iobserver.bjutloginapp.widget.StatusCard;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.main_toolbar) Toolbar toolbar;
    @BindView(R.id.main_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.fab_refresh) FloatingActionButton refreshFAB;
    @BindView(R.id.status_card) CardView statusCardView;
    @BindView(R.id.note_card) CardView noteCardView;
    public final String TAG = "MainActivity";
    public SharedPreferences prefs;
    StatusCard statusCard;
    NoteCard noteCard;
    TextView userView;
    TextView statusView;
    TextView timeView;
    TextView feeView;
    TextView networkView;
    TextView fluxView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        userView = ButterKnife.findById(statusCardView, R.id.card_user);
        statusView = ButterKnife.findById(statusCardView, R.id.card_status);
        timeView = ButterKnife.findById(statusCardView, R.id.card_time);
        feeView = ButterKnife.findById(statusCardView, R.id.card_fee);
//        networkView = ButterKnife.findById(statusCardView, R.id.card_network);
        fluxView = ButterKnife.findById(statusCardView, R.id.card_flux);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //TODO FAB Action
/*        refreshFAB.setOnClickListener(view -> {
            operator.refresh(coordinatorLayout, usedFlux, this);
        });*/

        statusCard = new StatusCard(statusCardView, this);
        noteCard = new NoteCard(noteCardView, this);

    }

    @Override
    public void onResume(){
        super.onResume();
        if (prefs.getString("user", null) != null && !prefs.getString("user", null).isEmpty())
            userView.setText(prefs.getString("user", null));
        else userView.setText(getResources().getString(R.string.card_user));
        statusCard.sync(coordinatorLayout, true);
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
            } break;
            case R.id.action_help: {

            }
        }

        return super.onOptionsItemSelected(item);
    }

}
