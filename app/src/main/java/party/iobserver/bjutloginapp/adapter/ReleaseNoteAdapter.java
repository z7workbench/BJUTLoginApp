package party.iobserver.bjutloginapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import party.iobserver.bjutloginapp.R;

/**
 * Created by ZeroGo on 2017.3.14.
 */

public class ReleaseNoteAdapter extends RecyclerView.Adapter<ReleaseNoteAdapter.RNViewHolder> {
    private Context context;

    public ReleaseNoteAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RNViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RNViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.release_note_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RNViewHolder holder, int position) {
        holder.titleView.setText(context.getResources().getStringArray(R.array.rn_title_content)[position]);
        holder.contentView.setText(context.getResources().getStringArray(R.array.rn_note_content)[position]);
    }

    @Override
    public int getItemCount() {
        return context.getResources().getStringArray(R.array.rn_title_content).length;
    }

    static class RNViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rv_title)
        TextView titleView;
        @BindView(R.id.rv_content)
        TextView contentView;

        RNViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
