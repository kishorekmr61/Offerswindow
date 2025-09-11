package com.customer.offerswindow.ui.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.BR
import com.customer.offerswindow.R
import com.customer.offerswindow.databinding.FragmentSuccessBinding
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.utils.setWhiteToolBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuccessFragment : Fragment() {

    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!
    private val vm: DashBoardViewModel by activityViewModels()
    private val viewModel: SuccessViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.nav_home)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuccessBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        vm.hidetoolbar.value = true
        viewModel.message.set("You have successfully \n Booked your slot")
        activity?.setWhiteToolBar("", true)
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        binding.setVariable(BR.onItemClick, View.OnClickListener {
            when (it.id) {
                R.id.bookdetails_btn -> {
                    findNavController().navigate(R.id.nav_mybookings)
                }
            }
        })
    }
}