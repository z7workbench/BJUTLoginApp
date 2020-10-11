package xin.z7workbench.bjutloginapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialSharedAxis
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.RecyclerBinding
import xin.z7workbench.bjutloginapp.databinding.RecyclerItemBinding

class ThemeFragment : BasicFragment<RecyclerBinding>() {
    private val values by lazy {
        resources.getStringArray(R.array.theme_index)
    }
    val setting: String
        get() = app.prefs.getString("theme_index", values.first())!!

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
            RecyclerBinding.inflate(inflater, container, false)

    override fun initViewAfterViewCreated() {
        binding.recyclerToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.recycler.run {
            adapter = ThemesAdapter(resources.getStringArray(R.array.themes).toList())
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.recyclerToolbar.title = getString(R.string.settings_theme_title)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    inner class ThemesAdapter(val themes: List<String>) : RecyclerView.Adapter<ThemesAdapter.ThemesViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ThemesViewHolder(RecyclerItemBinding.inflate(layoutInflater))

        override fun getItemCount() = themes.size

        override fun onBindViewHolder(holder: ThemesViewHolder, position: Int) {
            holder.binding.text.text = themes[position]

        }

        inner class ThemesViewHolder(val binding: RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root)
    }
}