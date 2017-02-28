package tk.iobserver.bjutloginapp.widget;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tk.iobserver.bjutloginapp.R;

/**
 * Created by ZeroGo on 2017.2.28.
 */

public class StatusCard {
    @BindView(R.id.card_user) TextView userView;
    @BindView(R.id.card_time) TextView timeView;
    @BindView(R.id.card_fee) TextView feeView;
    @BindView(R.id.card_network) TextView networkView;
    @BindView(R.id.card_flux) TextView fluxView;
    private CardView cardView;
    private AppCompatActivity activity;

    public StatusCard(CardView cardView, AppCompatActivity activity){
        this.cardView = cardView;
        this.activity = activity;
        ButterKnife.bind(this, cardView);
    }


}
