package xin.z7workbench.bjutloginapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_release_note.*
import kotlinx.android.synthetic.main.content_release_note.*
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.adapter.ReleaseNoteAdapter

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
