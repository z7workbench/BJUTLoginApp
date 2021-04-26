package top.z7workbench.bjutloginapp.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialElevationScale
import top.z7workbench.bjutloginapp.R
import top.z7workbench.bjutloginapp.databinding.ControlCardBinding
import top.z7workbench.bjutloginapp.databinding.FluxCardBinding
import top.z7workbench.bjutloginapp.databinding.FragmentMainBinding
import top.z7workbench.bjutloginapp.databinding.StatusCardBinding
import top.z7workbench.bjutloginapp.model.MainViewModel
import top.z7workbench.bjutloginapp.network.NetworkGlobalObject
import top.z7workbench.bjutloginapp.util.IpMode
import top.z7workbench.bjutloginapp.util.buildString


class MainFragment : BasicFragment<FragmentMainBinding>() {
    val viewModel by activityViewModels<MainViewModel>()

    override fun initViewAfterViewCreated() {
        binding.swipeRefresh.setColorSchemeColors(R.attr.colorAccent)
        binding.swipeRefresh.setDistanceToTriggerSync(200)

        binding.swipeRefresh.setOnRefreshListener {
//            TODO("refresh")
            binding.swipeRefresh.isRefreshing = false
//            viewModel.syncing(requireContext()) {
//                binding.swipeRefresh.isRefreshing = false
//            }
        }

//        prevent recycler from scrolling
        val llm =
            object : LinearLayoutManager(requireContext(), VERTICAL, false) {
                override fun canScrollVertically() = false
            }
        binding.recycler.run {
            adapter = MainRecyclerAdapter()
            layoutManager = llm
            addItemDecoration(CardsDecoration())

        }

        viewModel.time.observe(this) {
            binding.syncTime.text = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(false)
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMainBinding.inflate(inflater, container, false)

    inner class MainRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                0 -> {
                    val binding = StatusCardBinding.inflate(layoutInflater)
                    binding.root.layoutParams = RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    StatusCardViewHolder(binding)
                }
                1 -> {
                    val binding = ControlCardBinding.inflate(layoutInflater)
                    binding.root.layoutParams = RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    ControlCardViewHolder(binding)
                }
                else -> {
                    val binding = FluxCardBinding.inflate(layoutInflater)
                    binding.root.layoutParams = RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    FluxCardViewHolder(binding)
                }
            }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is StatusCardViewHolder -> {
                    holder.binding.wifiSSID.text = NetworkGlobalObject.getWifiSSID(requireContext())
                    viewModel.user.observe(this@MainFragment) {
                        if (it != null) {
                            holder.binding.user.text = resources.getString(R.string.user)
                                .buildString(resources.getString(R.string.colon), it.name)
                            holder.binding.plan.text = resources.getString(R.string.pack)
                                .buildString(
                                    resources.getString(R.string.colon),
                                    it.pack.toString(),
                                    " GB"
                                )
                        } else {
                            holder.binding.user.text = resources.getString(R.string.user)
                                .buildString(
                                    resources.getString(R.string.colon),
                                    resources.getString(R.string.unknown)
                                )
                            holder.binding.plan.text = resources.getString(R.string.pack)
                                .buildString(
                                    resources.getString(R.string.colon),
                                    resources.getString(R.string.unknown)
                                )
                        }
                    }
                    viewModel.fee.observe(this@MainFragment) {
                        if (it != null && it > 0) {
                            holder.binding.fee.text = resources.getString(R.string.fee)
                                .buildString(resources.getString(R.string.colon), it.toString())
                        } else {
                            holder.binding.fee.text = resources.getString(R.string.fee)
                                .buildString(
                                    resources.getString(R.string.colon),
                                    resources.getString(R.string.unknown)
                                )
                        }
                    }
                    viewModel.status.observe(this@MainFragment) {
                        holder.binding.title.text = resources.getString(R.string.status_card)
                            .buildString(resources.getString(it.description))
                    }
                    viewModel.usedTime.observe(this@MainFragment) {
                        holder.binding.usedTime.text = if (it != null && it >= 0)
                            resources.getString(R.string.used_time).buildString(
                                resources.getString(R.string.colon),
                                it.toString(),
                                resources.getString(R.string.minutes)
                            )
                        else
                            resources.getString(R.string.used_time).buildString(
                                resources.getString(R.string.colon),
                                resources.getString(R.string.unknown)
                            )
                    }
                }
                is ControlCardViewHolder -> {
                    viewModel.user.observe(this@MainFragment) {
                        if (it != null) {
                            holder.binding.user.text = it.name
                        } else {
                            holder.binding.user.text = resources.getString(R.string.unknown)
                        }
                    }
                    holder.binding.changeUser.setOnClickListener {
                        val extras = FragmentNavigatorExtras(
                            holder.binding.changeUser to "user_transition"
                        )
                        findNavController().navigate(
                            R.id.action_main_to_userFragment,
                            null,
                            null,
                            extras
                        )
                    }
                    holder.binding.changeLang.setOnClickListener {
                        val extras = FragmentNavigatorExtras(
                            holder.binding.changeLang to "transition"
                        )
                        findNavController().navigate(
                            R.id.action_mainFragment_to_localeFragment,
                            null,
                            null,
                            extras
                        )
                    }
                    holder.binding.changeTheme.setOnClickListener {
                        val extras = FragmentNavigatorExtras(
                            holder.binding.changeTheme to "transition"
                        )
                        findNavController().navigate(
                            R.id.action_mainFragment_to_themeFragment,
                            null,
                            null,
                            extras
                        )
                    }
                    ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.ip_mode,
                        R.layout.spinner_item
                    ).also {
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        holder.binding.ipSpinner.adapter = it
                    }
                    viewModel.ipMode.observe(this@MainFragment) {
                        when (it) {
                            IpMode.WIRELESS -> holder.binding.ipSpinner.setSelection(0)
                            IpMode.WIRED_IPV4 -> holder.binding.ipSpinner.setSelection(1)
                            IpMode.WIRED_IPV6 -> holder.binding.ipSpinner.setSelection(2)
                            IpMode.WIRED_BOTH -> holder.binding.ipSpinner.setSelection(3)
                            else -> holder.binding.ipSpinner.setSelection(0)
                        }
                    }
                    holder.binding.ipSpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                when (position) {
                                    0 -> {
                                        viewModel.changeIpMode(IpMode.WIRELESS)
                                    }
                                    1 -> {
                                        viewModel.changeIpMode(IpMode.WIRED_IPV4)
                                    }
                                    2 -> {
                                        viewModel.changeIpMode(IpMode.WIRED_IPV6)
                                    }
                                    3 -> {
                                        viewModel.changeIpMode(IpMode.WIRED_BOTH)
                                    }
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                holder.binding.ipSpinner.setSelection(0)
                            }
                        }
                    viewModel.theme.observe(this@MainFragment) {
                        if (it != null) {
                            holder.binding.theme.text = it
                        } else {
                            holder.binding.theme.text = resources.getStringArray(R.array.themes)[0]
                        }
                    }
                    viewModel.locale.observe(this@MainFragment) {
                        if (it != null) {
                            holder.binding.language.text = it
                        } else {
                            holder.binding.language.text =
                                resources.getStringArray(R.array.language)[0]
                        }
                    }
                }
                is FluxCardViewHolder -> {

                }
            }
        }

        override fun getItemViewType(position: Int): Int = position

        override fun getItemCount(): Int = 3

        inner class StatusCardViewHolder(val binding: StatusCardBinding) :
            RecyclerView.ViewHolder(binding.root)

        inner class ControlCardViewHolder(val binding: ControlCardBinding) :
            RecyclerView.ViewHolder(binding.root)

        inner class FluxCardViewHolder(val binding: FluxCardBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    inner class CardsDecoration : RecyclerView.ItemDecoration() {
        private val padding by lazy { resources.getDimensionPixelSize(R.dimen.main_cards_padding) }

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.set(padding, padding / 2, padding, padding / 2)
        }
    }
}