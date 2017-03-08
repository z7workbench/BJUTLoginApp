package tk.iobserver.bjutloginapp.widget;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import tk.iobserver.bjutloginapp.R;
import tk.iobserver.bjutloginapp.ui.ReleaseNoteActivity;

/**
 * Created by ZeroGo on 2017.3.2.
 */

public class NoteCard {
    private Activity activity;
    private CardView cardView;

    @OnClick(R.id.note_rn)
    void openReleaseNote(){
        Intent intent = new Intent(activity, ReleaseNoteActivity.class);
        activity.startActivity(intent);
    }

    public NoteCard(CardView cardView, Activity activity){
        this.cardView = cardView;
        this.activity = activity;
        ButterKnife.bind(this, this.cardView);
    }


}
