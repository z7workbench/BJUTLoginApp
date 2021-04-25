package top.z7workbench.bjutloginapp.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import top.z7workbench.bjutloginapp.R
import top.z7workbench.bjutloginapp.databinding.RecyclerBinding
import top.z7workbench.bjutloginapp.databinding.RecyclerItemBinding
import top.z7workbench.bjutloginapp.model.MainViewModel

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
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.recyclerToolbar.title = getString(R.string.settings_language_title)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.mainContainer
            scrimColor = Color.TRANSPARENT
        }
    }


    inner class LocalesAdapter(val locales: List<String>) :
        RecyclerView.Adapter<LocalesAdapter.LocalesViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LocalesViewHolder(RecyclerItemBinding.inflate(layoutInflater))

        override fun getItemCount() = locales.size

        override fun onBindViewHolder(holder: LocalesViewHolder, position: Int) {
            holder.binding.text.text = locales[position]
            val current = viewModel.langIndies.indexOf(
                app.prefs.getString(
                    "language",
                    viewModel.langIndies.first()
                )
            )
            if (current == position) {
                holder.binding.text.toggle()
            }
            holder.itemView.setOnClickListener {
                if (current != position) {
                    app.prefs.edit { putString("language", viewModel.langIndies[position]) }
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    requireContext().startActivity(intent)
                    requireActivity().finish()
                }
            }
        }

        inner class LocalesViewHolder(val binding: RecyclerItemBinding) :
            RecyclerView.ViewHolder(binding.root)
    }
}