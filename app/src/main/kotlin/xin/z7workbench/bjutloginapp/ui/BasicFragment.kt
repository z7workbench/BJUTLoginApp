package xin.z7workbench.bjutloginapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import xin.z7workbench.bjutloginapp.LoginApp
import xin.z7workbench.bjutloginapp.databinding.FragmentUserBinding

abstract class BasicFragment<T: ViewBinding>: Fragment() {
    val app by lazy { requireActivity().application as LoginApp }
    private var _binding: T? = null
    val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = initBinding(inflater, container)
        initView()
        return binding.root
    }

    abstract fun initBinding(inflater: LayoutInflater, container: ViewGroup?): T

    abstract fun initView()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}