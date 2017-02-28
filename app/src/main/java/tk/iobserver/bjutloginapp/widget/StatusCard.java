package tk.iobserver.bjutloginapp.widget;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    CoordinatorLayout coordinatorLayout;
    SharedPreferences prefs;
    private CardView cardView;
    private MainActivity activity;
    Operator operator;

    public StatusCard(CardView cardView, MainActivity activity){
        this.cardView = cardView;
        this.activity = activity;
        operator= new Operator(activity.TAG);
        ButterKnife.bind(this, cardView);
        userView.setText(activity.prefs.getString("user", null));
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        coordinatorLayout = ButterKnife.findById(activity, R.id.main_layout);
    }

    @OnClick(R.id.card_btn_login)
    public void onLogin(){
        operator.login(coordinatorLayout, prefs.getString("user", null), prefs.getString("password", null));
    }

    @OnClick(R.id.card_btn_sync)
    public void onSync(){
        operator.refresh(coordinatorLayout, fluxView, activity);
    }

    @OnClick(R.id.card_btn_logout)
    public void onLogout(){
        operator.logout(coordinatorLayout);
    }
}
