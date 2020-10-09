package xin.z7workbench.bjutloginapp.ui

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import xin.z7workbench.bjutloginapp.view.bottomappbar.cradle.BottomAppBarCutCradleTopEdge
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.ControlCardBinding
import xin.z7workbench.bjutloginapp.databinding.FluxCardBinding
import xin.z7workbench.bjutloginapp.databinding.FragmentMainBinding
import xin.z7workbench.bjutloginapp.databinding.LoginCardBinding
import xin.z7workbench.bjutloginapp.model.MainViewModel
import xin.z7workbench.bjutloginapp.util.NetworkUtils


class MainFragment : BasicFragment<FragmentMainBinding>() {
    val viewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun initView() {
        binding.swipeRefresh.setColorSchemeColors(R.attr.colorAccent)
        binding.swipeRefresh.setDistanceToTriggerSync(200)

        binding.swipeRefresh.setOnRefreshListener {
//            TODO("refresh")
            binding.swipeRefresh.isRefreshing = false
        }

        val adapter = MainRecyclerAdapter()
//        prevent recycler from scrolling
        val llm = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically() = false
        }
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = llm
        binding.recycler.addItemDecoration(CardsDecoration())

        val topEdge = BottomAppBarCutCradleTopEdge(
                binding.bottomAppBar.fabCradleMargin,
                binding.bottomAppBar.fabCradleRoundedCornerRadius,
                binding.bottomAppBar.cradleVerticalOffset
        )
        val background = binding.bottomAppBar.background as MaterialShapeDrawable
        background.shapeAppearanceModel = background.shapeAppearanceModel.toBuilder()
                .setTopEdge(topEdge).build()

        makeSnack(NetworkUtils.getWifiSSID(requireContext()))
        binding.bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_theme -> {

                }
                R.id.action_user -> {
                    makeSnack("yes")
                }
            }
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(false)
    }

    private fun makeSnack(text: CharSequence) {
        var snack = Snackbar.make(binding.mainLayout, text, Snackbar.LENGTH_SHORT)
        snack = snack.setAnchorView(binding.fab.id)
        snack.show()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
            FragmentMainBinding.inflate(inflater, container, false)

    inner class MainRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                when (viewType) {
                    0 -> {
                        val binding = LoginCardBinding.inflate(layoutInflater)
                        binding.root.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT)
                        LoginCardViewHolder(binding)
                    }
                    1 -> {
                        val binding = ControlCardBinding.inflate(layoutInflater)
                        binding.root.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT)
                        ControlCardViewHolder(binding)
                    }
                    else -> {
                        val binding = FluxCardBinding.inflate(layoutInflater)
                        binding.root.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT)
                        FluxCardViewHolder(binding)
                    }
                }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is LoginCardViewHolder -> {
                    holder.binding.userButton.setOnClickListener {
                        val extras = FragmentNavigatorExtras(
                                holder.binding.userButton to "user_transition"
                        )
                        findNavController().navigate(R.id.action_main_to_userFragment, null, null, extras)
                    }
                    viewModel.user.observe(this@MainFragment) {
                        holder.binding.user.text = it.name
                        holder.binding.flux.text = it.pack.toString() + " GB"
                    }
                }
                is ControlCardViewHolder -> {
                    if (app.prefs.getInt("ip_mode", -1) < 0) {
                        app.prefs.edit { putInt("ip_mode", holder.binding.ipv4Chip.id) }
                    }
                    holder.binding.ipModeGroup.run {
                        clearCheck()
                        check(app.prefs.getInt("ip_mode", -1))
                        setOnCheckedChangeListener { _, i ->
                            if (i < 0) holder.binding.ipModeGroup.check(app.prefs.getInt("ip_mode", -1))
                            else viewModel.changeIpMode(i)
                        }
                    }
                    holder.binding.wifiSSID.text = NetworkUtils.getWifiSSID(requireContext())
                    viewModel.ipMode.observe(this@MainFragment) {
                        if (holder.binding.ipModeGroup.checkedChipId != it)
                            holder.binding.ipModeGroup.check(it)
                    }
                }
                is FluxCardViewHolder -> {

                }
            }
        }

        override fun getItemViewType(position: Int): Int = position

        override fun getItemCount(): Int = 3

        inner class LoginCardViewHolder(val binding: LoginCardBinding) : RecyclerView.ViewHolder(binding.root)
        inner class ControlCardViewHolder(val binding: ControlCardBinding) : RecyclerView.ViewHolder(binding.root)
        inner class FluxCardViewHolder(val binding: FluxCardBinding) : RecyclerView.ViewHolder(binding.root)
    }

    inner class CardsDecoration : RecyclerView.ItemDecoration() {
        private val padding by lazy { resources.getDimensionPixelSize(R.dimen.main_cards_padding) }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.set(padding, padding / 2, padding, padding / 2)
        }
    }
}