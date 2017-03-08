package party.iobserver.bjutloginapp.util;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import party.iobserver.bjutloginapp.R;

/**
 * Created by ZeroGo on 2017.2.23.
 */

public class Operator {
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private Activity activity;
    private String TAG;

    public Operator(String TAG, Activity activity){
        this.TAG = TAG;
        this.activity = activity;
    }

    public void login(View view, String user, String password){
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
                    Log.e(TAG, "Failed! " , e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.body().string().indexOf("In use") > 0) {
                        Snackbar.make(view, "Login Failed! " + "This account is in use. ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, "Login Succeeded! ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });
        }
    }

    public void refresh(View view, TextView fluxView, TextView statusView) {
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
                Log.e(TAG, "Failed! ", e);
                activity.runOnUiThread(()->{
                    try{
                        statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_red));
                    } catch (Exception e1){
                        Log.e(TAG, "", e1);
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
                            Log.e(TAG, "", e);
                        }
                    });
                    Snackbar.make(view, "Refresh successfully completed! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_red));
                    Snackbar.make(view, "Can't get data! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    public void logout(View view, TextView statusView){
        Request request = new Request.Builder()
                .get()
                .url("http://wlgn.bjut.edu.cn/F.htm")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Snackbar.make(view, "Logout Failed! ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.e(TAG, "Failed!", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.body().string().indexOf("注销成功") > 0) {
                    Snackbar.make(view, "Logout Succeeded! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Logout Failed! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        statusView.setTextColor(ContextCompat.getColor(activity, R.color.alert_yellow));

    }

}
