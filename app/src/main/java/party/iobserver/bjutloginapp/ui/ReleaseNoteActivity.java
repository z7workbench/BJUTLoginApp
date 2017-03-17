package party.iobserver.bjutloginapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import party.iobserver.bjutloginapp.R;
import party.iobserver.bjutloginapp.adapter.ReleaseNoteAdapter;

public class ReleaseNoteActivity extends AppCompatActivity {
    @BindView(R.id.rn_toolbar) Toolbar toolbar;
    @BindView(R.id.note_rv) RecyclerView recyclerView;
    ReleaseNoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_note);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        adapter = new ReleaseNoteAdapter(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
    }

}
