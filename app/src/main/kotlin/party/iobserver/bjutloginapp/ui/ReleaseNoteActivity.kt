package party.iobserver.bjutloginapp.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_release_note.*
import kotlinx.android.synthetic.main.content_release_note.*
import party.iobserver.bjutloginapp.R
import party.iobserver.bjutloginapp.adapter.ReleaseNoteAdapter

class ReleaseNoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_release_note)
        setSupportActionBar(toolbar)
        toolbar!!.setNavigationOnClickListener { onBackPressed() }

        val adapter = ReleaseNoteAdapter(this)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recycler.layoutManager = llm
        recycler.adapter = adapter
    }

}
