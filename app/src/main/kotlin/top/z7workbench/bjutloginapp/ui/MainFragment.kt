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
import coil.load
import com.google.android.material.transition.MaterialElevationScale
import top.z7workbench.bjutloginapp.BuildConfig
import top.z7workbench.bjutloginapp.R
import top.z7workbench.bjutloginapp.databinding.ControlCardBinding
import top.z7workbench.bjutloginapp.databinding.FluxCardBinding
import top.z7workbench.bjutloginapp.databinding.FragmentMainBinding
import top.z7workbench.bjutloginapp.databinding.StatusCardBinding
import top.z7workbench.bjutloginapp.model.StatusViewModel
import top.z7workbench.bjutloginapp.model.UserViewModel
import top.z7workbench.bjutloginapp.util.IpMode
import top.z7workbench.bjutloginapp.util.NetworkState
import top.z7workbench.bjutloginapp.util.buildString
import top.z7workbench.bjutloginapp.util.startActivity

class MainFragment : BasicFragment<FragmentMainBinding>() {
    val viewModel by activityViewModels<UserViewModel>()
    val statusViewModel by activityViewModels<StatusViewModel>()

    override fun initViewAfterViewCreated() {
        binding.swipeRefresh.setColorSchemeColors(R.attr.colorAccent)
        binding.swipeRefresh.setDistanceToTriggerSync(200)

        binding.swipeRefresh.setOnRefreshListener {
//          TODO("refresh")
            statusViewModel.networkState(requireContext())
//            sync()
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.swipe.observe(this) {
            if (it) binding.swipeRefresh.isRefreshing = false
        }

//      prevent recycler from scrolling
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

    //    @NeedsPermission(Manifest.permission.INTERNET)
    fun sync() = viewModel.sync()

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
                    holder.binding.status.setOnClickListener { requireContext().startActivity<ComposableUserActivity>() }
                    statusViewModel.status.observe(this@MainFragment) {
                        if (it.state != NetworkState.OTHER) {
                            val id = when {
                                it.state == NetworkState.CELLULAR && it.validate ->
                                    R.drawable.ic_baseline_signal_cellular_4_bar_24
                                it.state == NetworkState.CELLULAR && !it.validate ->
                                    R.drawable.ic_baseline_signal_cellular_off_24
                                it.state == NetworkState.WIFI && it.validate ->
                                    R.drawable.ic_wifi_24dp
                                else -> R.drawable.ic_baseline_wifi_off_24
                            }
                            holder.binding.status.load(id)
                            holder.binding.validate.text =
                                if (it.validate) resources.getString(R.string.validated)
                                else resources.getString(R.string.not_validated)
                        } else {
                            holder.binding.status.load(R.drawable.ic_baseline_disabled_by_default_24)
                            holder.binding.validate.text = ""
                        }
                    }
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
                    viewModel.stats.observe(this@MainFragment) {
                        holder.binding.usedTime.text = if (it != null && it.time >= 0)
                            resources.getString(R.string.used_time).buildString(
                                resources.getString(R.string.colon),
                                it.time.toString(),
                                resources.getString(R.string.minutes)
                            )
                        else resources.getString(R.string.used_time).buildString(
                            resources.getString(R.string.colon),
                            resources.getString(R.string.unknown)
                        )
                        holder.binding.fee.text = if (it != null && it.fee > 0)
                            resources.getString(R.string.fee)
                                .buildString(
                                    resources.getString(R.string.colon),
                                    "￥",
                                    it.fee.toString()
                                )
                        else resources.getString(R.string.fee)
                            .buildString(
                                resources.getString(R.string.colon),
                                resources.getString(R.string.unknown)
                            )

                    }
                    viewModel.status.observe(this@MainFragment) {
                        holder.binding.title.text = resources.getString(R.string.status_card)
                            .buildString(resources.getString(it.description))
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
                            holder.binding.changeLang to "lang_transition"
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
                            holder.binding.changeTheme to "theme_transition"
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
                            }
                        }
                    holder.binding.theme.text =
                        resources.getStringArray(R.array.themes)[app.prefs.getInt("theme_index", 0)]
                    holder.binding.language.text =
                        resources.getStringArray(R.array.language)[app.prefs.getInt("language", 0)]
                    holder.binding.version.text = BuildConfig.VERSION_NAME
                }
                is FluxCardViewHolder -> {
                    viewModel.stats.observe(this@MainFragment) {
                        holder.binding.flux.text = if (it != null && it.fee > 0)
                            resources.getString(R.string.fee)
                                .buildString(
                                    resources.getString(R.string.colon),
                                    "￥",
                                    it.fee.toString(),
                                    resources.getString(R.string.change_to_flux),
                                    resources.getString(R.string.colon),
                                    it.remained
                                )
                        else resources.getString(R.string.fee)
                            .buildString(
                                resources.getString(R.string.colon),
                                resources.getString(R.string.unknown),
                                resources.getString(R.string.change_to_flux),
                                resources.getString(R.string.colon),
                                resources.getString(R.string.unknown)
                            )
                        if (it == null)
                            holder.binding.exceeded.text = resources.getString(R.string.exceeded)
                                .buildString(
                                    resources.getString(R.string.colon),
                                    resources.getString(R.string.unknown)
                                )
                        else holder.binding.exceeded.text = resources.getString(R.string.exceeded)
                            .buildString(
                                resources.getString(R.string.colon),
                                it.exceeded
                            )

                        holder.binding.remained.text = if (it == null)
                            resources.getString(R.string.remained)
                                .buildString(
                                    resources.getString(R.string.colon),
                                    resources.getString(R.string.unknown)
                                )
                        else resources.getString(R.string.remained)
                            .buildString(
                                resources.getString(R.string.colon),
                                it.remained
                            )
                        if (it == null) {
                            holder.binding.progressBar.progress = 0
                        } else {
                            holder.binding.progressBar.progress = it.percent
                        }
                    }
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