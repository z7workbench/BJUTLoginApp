package tk.iobserver.bjutloginapp.widget;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tk.iobserver.bjutloginapp.R;
import tk.iobserver.bjutloginapp.ui.MainActivity;
import tk.iobserver.bjutloginapp.util.Operator;

/**
 * Created by ZeroGo on 2017.2.28.
 */

public class StatusCard {
    @BindView(R.id.card_user) TextView userView;
    @BindView(R.id.card_status) TextView statusView;
    @BindView(R.id.card_time) TextView timeView;
    @BindView(R.id.card_fee) TextView feeView;
    @BindView(R.id.card_network) TextView networkView;
    @BindView(R.id.card_flux) TextView fluxView;
    private final OkHttpClient okHttpClient = new OkHttpClient();
    CoordinatorLayout coordinatorLayout;
    SharedPreferences prefs;
    private CardView cardView;
    private MainActivity activity;

    public StatusCard(CardView cardView, MainActivity activity) {
        this.cardView = cardView;
        this.activity = activity;
        ButterKnife.bind(this, cardView);
        if (activity.prefs.getString("user", null) != null && !activity.prefs.getString("user", null).isEmpty())
            userView.setText(activity.prefs.getString("user", null));
        else userView.setText(activity.getResources().getString(R.string.card_user));
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        coordinatorLayout = ButterKnife.findById(activity, R.id.main_layout);
    }

    @OnClick(R.id.card_btn_login)
    void onLogin() {
        login(coordinatorLayout, prefs.getString("user", null), prefs.getString("password", null));
    }

    @OnClick(R.id.card_btn_sync)
    void onSync() {
        sync(coordinatorLayout);
    }

    @OnClick(R.id.card_btn_logout)
    void onLogout() {
        logout(coordinatorLayout);
    }

    void login(View view, String user, String password){
        statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_yellow));
        if(user != null && !user.isEmpty()){
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
                    Snackbar.make(view, "Login Failed! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Log.e(activity.TAG, "Failed! " , e);
                    activity.runOnUiThread(()->{
                        try{
                            statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_red));
                        } catch(Exception e1){
                            Log.e(activity.TAG, "setTextColor", e1);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.body().string().indexOf("In use") > 0) {
                        Snackbar.make(view, "Login Failed! " + "This account is in use. ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        activity.runOnUiThread(()->{
                            try{
                                statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_red));
                            } catch(Exception e1){
                                Log.e(activity.TAG, "setTextColor", e1);
                            }
                        });
                    } else {
                        Snackbar.make(view, "Login Succeeded! ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        activity.runOnUiThread(()->{
                            try{
                                statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_green));
                            } catch(Exception e1){
                                Log.e(activity.TAG, "setTextColor", e1);
                            }
                        });
                    }
                }
            });
        }
    }

    public void sync(View view) {
        statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_yellow));
        Request request = new Request.Builder()
                .get()
                .url("http://lgn.bjut.edu.cn/")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Snackbar.make(view, "Refresh Failed! ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.e(activity.TAG, "Failed! ", e);
                activity.runOnUiThread(()->{
                    try{
                        statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_red));
                    } catch (Exception e1){
                        Log.e(activity.TAG, "", e1);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Pattern pattern = Pattern.compile("flow='(\\d+)");
                Matcher matcher = pattern.matcher(response.body().string());

                if (matcher.find()) {
                    final Double flux = (double) ((int) (Double.parseDouble(matcher.group(1)) / 1024 * 100)) / 100;
                    activity.runOnUiThread(() -> {
                        try {
                            fluxView.setText(flux + "MB");
                            statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_green));
                        } catch (Exception e) {
                            Log.e(activity.TAG, "", e);
                        }
                    });
                    Snackbar.make(view, "Refresh successfully completed! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Can't get data! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    activity.runOnUiThread(()->{
                        try{
                            statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_red));
                        } catch(Exception e1){
                            Log.e(activity.TAG, "setTextColor", e1);
                        }
                    });
                }
            }
        });
    }

    void logout(View view){
        Request request = new Request.Builder()
                .get()
                .url("http://wlgn.bjut.edu.cn/F.htm")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Snackbar.make(view, "Logout Failed! ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.e(activity.TAG, "Failed!", e);
                activity.runOnUiThread(()->{
                    try{
                        statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_red));
                    } catch(Exception e1){
                        Log.e(activity.TAG, "setTextColor", e1);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.body().string().indexOf("注销成功") > 0) {
                    Snackbar.make(view, "Logout Succeeded! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    activity.runOnUiThread(()->{
                        try{
                            statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_grey));
                        } catch(Exception e1){
                            Log.e(activity.TAG, "setTextColor", e1);
                        }
                    });
                } else {
                    Snackbar.make(view, "Logout Failed! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    activity.runOnUiThread(()->{
                        try{
                            statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_red));
                        } catch(Exception e1){
                            Log.e(activity.TAG, "setTextColor", e1);
                        }
                    });
                }
            }
        });

    }
}
