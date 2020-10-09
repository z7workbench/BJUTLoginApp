package xin.z7workbench.bjutloginapp.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialContainerTransform
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.*
import xin.z7workbench.bjutloginapp.model.MainViewModel
import xin.z7workbench.bjutloginapp.model.User

class UserFragment : BasicFragment<FragmentUserBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[MainViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.mainContainer
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun initView() {
        NavigationUI.setupWithNavController(binding.toolbar, findNavController())
        binding.toolbar.title = getString(R.string.action_users)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_add -> {
                    openUserDialog(true, User(0, "", "", 0))
                }
            }
            true
        }
        val usersAdapter = UsersAdapter()
        val llm = LinearLayoutManager(requireContext())
        llm.orientation = LinearLayoutManager.VERTICAL

        viewModel.users.observe(this) {
            val diffUtil = DiffUtil.calculateDiff(UserDiffCallback(usersAdapter.users, it))
            usersAdapter.users = it
            diffUtil.dispatchUpdatesTo(usersAdapter)
            if (it.isEmpty()) {
                binding.placeholder1.visibility = View.VISIBLE
                binding.placeholder2.visibility = View.VISIBLE
            } else {
                binding.placeholder1.visibility = View.GONE
                binding.placeholder2.visibility = View.GONE
            }
        }

        binding.recycler.apply {
            adapter = usersAdapter
            layoutManager = llm
            itemAnimator = DefaultItemAnimator()
        }

    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
            FragmentUserBinding.inflate(inflater, container, false)

    class UserDiffCallback(private val old: List<User>, private val new: List<User>) : DiffUtil.Callback() {
        override fun getOldListSize() = old.size
        override fun getNewListSize() = new.size
        override fun areItemsTheSame(p0: Int, p1: Int) = old[p0].id == new[p1].id
        override fun areContentsTheSame(p0: Int, p1: Int) = old[p0] == new[p1]
    }

    fun openUserDialog(newUser: Boolean, user: User) {
        var currentPackage = 30
        val dialogBinding = DialogUserBinding.inflate(layoutInflater)
        dialogBinding.name.setText(user.name)
        dialogBinding.name.setSelection(user.name.length)
        dialogBinding.password.setText(user.password)
        if (!newUser) {
            currentPackage = user.pack
            dialogBinding.seekPack.progress = user.pack
        }
        dialogBinding.textPack.text = """$currentPackage GB"""

        dialogBinding.seekPack.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                dialogBinding.textPack.text = """${dialogBinding.seekPack.progress} GB"""
                currentPackage = dialogBinding.seekPack.progress
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        AlertDialog.Builder(requireContext())
                .setView(dialogBinding.root)
                .setPositiveButton(R.string.OK) { _, _ ->
                    user.name = dialogBinding.name.text.toString()
                    user.password = dialogBinding.password.text.toString()
                    user.pack = currentPackage
                    if (!newUser) {
                        viewModel.updateUser(user)
                        viewModel.refreshUserId()
                    } else {
                        viewModel.insertUser(user)
                    }
                }
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .show()
    }

    inner class UsersAdapter(var users: List<User> = listOf()) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                UsersViewHolder(ItemUsersBinding.inflate(layoutInflater))

        override fun getItemCount() = users.size

        override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
            val user = users[holder.adapterPosition]

            holder.itemView.setOnClickListener {
                app.prefs.edit { putInt("current_user", user.id) }
                viewModel.refreshUserId()
//                this@UserFragment.finish()
            }
            holder.binding.user.text = user.name
            holder.binding.edit.setOnClickListener {
                openUserDialog(false, user.copy())
            }
            holder.binding.trash.setOnClickListener {
                viewModel.deleteUser(user)
                viewModel.refreshUserId()
            }
            viewModel.currentId.observe(this@UserFragment) {
                if (it == user.id) {
                    if (!holder.binding.user.isChecked)
                        holder.binding.user.toggle()
                } else {
                    if (holder.binding.user.isChecked)
                        holder.binding.user.isChecked = false
                }
            }
        }

        inner class UsersViewHolder(val binding: ItemUsersBinding) : RecyclerView.ViewHolder(binding.root)
    }
}