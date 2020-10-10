package xin.z7workbench.bjutloginapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialSharedAxis
import xin.z7workbench.bjutloginapp.R
import xin.z7workbench.bjutloginapp.databinding.RecyclerBinding
import xin.z7workbench.bjutloginapp.databinding.RecyclerItemBinding

class LocaleFragment: BasicFragment<RecyclerBinding>() {
    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
            RecyclerBinding.inflate(inflater, container, false)

    override fun initViewAfterViewCreated() {
        binding.recyclerToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.recycler.run {
            adapter = LocalesAdapter(resources.getStringArray(R.array.themes).toList())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    inner class LocalesAdapter(val locales: List<String>): RecyclerView.Adapter<LocalesAdapter.LocalesViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                LocalesViewHolder(RecyclerItemBinding.inflate(layoutInflater))

        override fun getItemCount() = locales.size

        inner class LocalesViewHolder(val binding: RecyclerItemBinding): RecyclerView.ViewHolder(binding.root)

        override fun onBindViewHolder(p0: LocalesViewHolder, p1: Int) {

        }
    }
}