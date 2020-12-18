package xin.z7workbench.bjutloginapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialSharedAxis
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.RecyclerBinding
import xin.z7workbench.bjutloginapp.databinding.RecyclerItemBinding
import xin.z7workbench.bjutloginapp.model.MainViewModel
import java.text.FieldPosition

class LocaleFragment : BasicFragment<RecyclerBinding>() {
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
            RecyclerBinding.inflate(inflater, container, false)

    override fun initViewAfterViewCreated() {
        binding.recyclerToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.recycler.run {
            adapter = LocalesAdapter(resources.getStringArray(R.array.language).toList())
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.recyclerToolbar.title = getString(R.string.settings_language_title)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    inner class LocalesAdapter(val locales: List<String>) : RecyclerView.Adapter<LocalesAdapter.LocalesViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                LocalesViewHolder(RecyclerItemBinding.inflate(layoutInflater))

        override fun getItemCount() = locales.size

        override fun onBindViewHolder(holder: LocalesViewHolder, position: Int) {
            holder.binding.text.text = locales[position]
            val current = viewModel.localeIndies.indexOf(app.prefs.getString("language", viewModel.localeIndies.first()))
            if (current == position){
                holder.binding.text.toggle()
            }
            holder.itemView.setOnClickListener {
                if (current != position) {
                    app.prefs.edit { putString("language", viewModel.localeIndies[position]) }
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    requireContext().startActivity(intent)
                    requireActivity().finish()
                }
            }
        }

        inner class LocalesViewHolder(val binding: RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root)
    }
}