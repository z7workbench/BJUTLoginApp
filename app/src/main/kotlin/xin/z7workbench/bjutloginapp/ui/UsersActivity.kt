package xin.z7workbench.bjutloginapp.ui

import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_users.*
import kotlinx.android.synthetic.main.dialog_user.view.*
import kotlinx.android.synthetic.main.item_users.view.*
import org.jetbrains.anko.alert
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.model.User
import xin.z7workbench.bjutloginapp.util.app

class UsersActivity : AppCompatActivity() {
    private var currentId = 0
    val prefs by lazy { app.prefs }
    private val userDao by lazy { app.appDatabase.userDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        val usersAdapter = UsersAdapter()
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL

        val users = userDao.all()
        users.observe(this, Observer {
            it?.apply {
                val diffUtil = DiffUtil.calculateDiff(UserDiffCallback(usersAdapter.users, it))
                usersAdapter.users = it
                diffUtil.dispatchUpdatesTo(usersAdapter)
                if (it.isEmpty()) {
                    placeholder1.visibility = View.VISIBLE
                    placeholder2.visibility = View.VISIBLE
                } else {
                    placeholder1.visibility = View.GONE
                    placeholder2.visibility = View.GONE
                }
            }
        })

        recycler.apply {
            adapter = usersAdapter
            layoutManager = llm
            itemAnimator = DefaultItemAnimator()
        }

        currentId = app.prefs.getInt("current_user", 0)
    }

    class UserDiffCallback(private val old: List<User>, private val new: List<User>) : DiffUtil.Callback() {
        override fun getOldListSize() = old.size
        override fun getNewListSize() = new.size
        override fun areItemsTheSame(p0: Int, p1: Int) = old[p0].id == new[p1].id
        override fun areContentsTheSame(p0: Int, p1: Int) = old[p0] == new[p1]
    }

    fun openUserDialog(newUser: Boolean, user: User) {
        var currentPackage = 8
        val view: View = layoutInflater.inflate(R.layout.dialog_user, null, false)
        view.name.setText(user.name)
        view.name.setSelection(user.name.length)
        view.password.setText(user.password)
        if (!newUser) {
            currentPackage = user.pack
            view.seek_pack.progress = user.pack
        }
        view.text_pack.text = """$currentPackage GB"""

        view.seek_pack.setOnSeekBarChangeListener (object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                view.text_pack.text = """${view.seek_pack.progress} GB"""
                currentPackage = view.seek_pack.progress
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        alert(R.string.action_users) {
            customView = view
            positiveButton(R.string.OK) { _ ->
                user.name = view.name.text.toString()
                user.password = view.password.text.toString()
                user.pack = currentPackage
                if (!newUser) {
                    userDao.update(user)
                } else {
                    userDao.insert(user)
                }
            }
            negativeButton(R.string.cancel) {}
        }.show()
    }

    inner class UsersAdapter(var users: MutableList<User> = mutableListOf()) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                UsersViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_users, parent, false))

        override fun getItemCount() = users.size

        override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
            val user = users[holder.adapterPosition]

            holder.itemView.setOnClickListener {
                prefs.edit().putInt("current_user", user.id).apply()
                this@UsersActivity.finish()
            }
            holder.view.user.text = user.name
            holder.view.edit.setOnClickListener {
                openUserDialog(false, user.copy())
            }
            holder.view.trash.setOnClickListener {
                userDao.delete(user)
            }
            if (currentId == user.id) {
                holder.view.user.toggle()
            }
        }

        inner class UsersViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_users, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                openUserDialog(true, User(0, "", "", 0))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}