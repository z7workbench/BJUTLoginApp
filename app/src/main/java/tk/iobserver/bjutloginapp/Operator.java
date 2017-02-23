package tk.iobserver.bjutloginapp;

import android.app.Activity;
import android.support.design.widget.Snackbar;
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

/**
 * Created by ZeroGo on 2017.2.23.
 */

public class Operator {
    final OkHttpClient okHttpClient = new OkHttpClient();
    private String TAG;

    public Operator(){
        TAG = null;
    }

    public Operator(String TAG){
        this.TAG = TAG;
    }

    void login(View view, String user, String password){
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
                Snackbar.make(view, "Login Failed! " + e, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body().string().indexOf("In use")>0){
                    Snackbar.make(view, "Login Failed! " + "This account is in use. ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Login Succeeded! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

    }

    void refresh(View view, TextView textView, Activity activity) {
        Snackbar.make(view, "Refresh data...", Snackbar.LENGTH_LONG)
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
                    activity.runOnUiThread(() -> {
                        try {
                            textView.setText("Used Flux: " + flux + "MB");
                        } catch (Exception e) {
                            Log.e(TAG, "", e);
                        }
                    });
                }
            }
        });
        Snackbar.make(view, "Completed! ", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    void logout(View view){
        Request request = new Request.Builder()
                .get()
                .url("http://wlgn.bjut.edu.cn/F.htm")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Snackbar.make(view, "Logout Failed! " + e, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

    }

}
