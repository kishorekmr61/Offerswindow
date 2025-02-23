package com.customer.offerswindow.ui.onboarding.forgotpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.customer.offerswindow.databinding.FragmentAccountRecoverBinding

class AccountRecoverFragment : Fragment() {

    private var _binding: FragmentAccountRecoverBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountRecoverViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountRecoverBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return root
    }
}