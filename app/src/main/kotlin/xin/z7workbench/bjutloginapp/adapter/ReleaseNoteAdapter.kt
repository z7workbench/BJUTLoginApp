package xin.z7workbench.bjutloginapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.release_note_item.view.*
import xin.z7workbench.bjutloginapp.R

/**
 * Created by ZeroGo on 2017.3.14.
 */

class ReleaseNoteAdapter(private val context: Context) : RecyclerView.Adapter<ReleaseNoteAdapter.RNViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RNViewHolder {
        return RNViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.release_note_item, parent, false))
    }

    override fun onBindViewHolder(holder: RNViewHolder, position: Int) {
        holder.view.rv_title.text = context.resources.getStringArray(R.array.rn_title_content)[position]
        holder.view.rv_content.text = context.resources.getStringArray(R.array.rn_note_content)[position]
    }

    override fun getItemCount(): Int {
        return context.resources.getStringArray(R.array.rn_title_content).size
    }

    class RNViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}
