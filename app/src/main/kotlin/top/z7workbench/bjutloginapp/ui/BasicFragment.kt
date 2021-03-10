package top.z7workbench.bjutloginapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import top.z7workbench.bjutloginapp.LoginApp

abstract class BasicFragment<T : ViewBinding> : Fragment() {
    val app by lazy { requireActivity().application as LoginApp }
    private var _binding: T? = null
    val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = initBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewAfterViewCreated()
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    abstract fun initBinding(inflater: LayoutInflater, container: ViewGroup?): T

    abstract fun initViewAfterViewCreated()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}