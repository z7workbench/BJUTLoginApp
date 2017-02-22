package tk.iobserver.bjutloginapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private SharedPreferences prefs;
    final OkHttpClient okHttpClient = new OkHttpClient();
    @BindView(R.id.main_toolbar) Toolbar toolbar;
    @BindView(R.id.main_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.main_tv_usedFlux) TextView usedFlux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            refresh();
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        refresh();
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
                login(prefs.getString("user", null), prefs.getString("password", null));
            }
            break;
            case R.id.action_logout: {
                logout();
            }
            break;
            case R.id.action_sync: {
                refresh();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    void login(String user, String password){
        RequestBody requestBody = new FormBody.Builder()
                .add("DDDDD", user)
                .add("upass", password)
                .add("6MKKey", "123")
                .build();
        Request request = new Request.Builder()
                .post(requestBody)
                .url("http://wlgn.bjut.edu.cn/")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Snackbar.make(coordinatorLayout, "Login Failed! " + e, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body().string().indexOf("In use")>0){
                    Snackbar.make(coordinatorLayout, "Login Failed! " + "This account is in use. ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(coordinatorLayout, "Login Succeeded! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

    }

    void refresh() {
        Snackbar.make(coordinatorLayout, "Refresh data...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        Request request = new Request.Builder()
                .get()
                .url("http://lgn.bjut.edu.cn/")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Pattern pattern = Pattern.compile("flow='(\\d+)");
                Matcher matcher = pattern.matcher(response.body().string());

                if (matcher.find()) {
                    final Double flux = (double) ((int) (Double.parseDouble(matcher.group(1)) / 1024 * 100)) / 100;
                    runOnUiThread(() -> {
                        try {
                            usedFlux.setText("Used Flux: " + flux + "MB");
                        } catch (Exception e) {
                            Log.e(TAG, "", e);
                        }
                    });
                }
            }
        });
        Snackbar.make(coordinatorLayout, "Completed! ", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    void logout(){
        Request request = new Request.Builder()
                .get()
                .url("http://wlgn.bjut.edu.cn/F.htm")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Snackbar.make(coordinatorLayout, "Logout Failed! " + e, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.body().string().indexOf("注销成功") > 0) {
                    Snackbar.make(coordinatorLayout, "Logout Succeeded! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(coordinatorLayout, "Logout Failed! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

    }
}
