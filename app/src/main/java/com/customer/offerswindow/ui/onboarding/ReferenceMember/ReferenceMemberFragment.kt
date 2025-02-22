package com.customer.offerswindow.ui.onboarding.ReferenceMember

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.R
import com.customer.offerswindow.databinding.ReferenceMemberFragmentBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.utils.hideOnBoardingToolbar
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReferenceMemberFragment : Fragment() {
    private val referencememberViewModel: ReferencememberViewModel by viewModels()
    private var _binding: ReferenceMemberFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ReferenceMemberFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        activity?.hideOnBoardingToolbar()
        setObserver()
        binding.signupBtn.setOnClickListener {
            if (isValidate()) {
                findNavController().navigate(R.id.nav_sign_in)
            }
        }
        binding.skipLbl.setOnClickListener {
            findNavController().navigate(R.id.nav_sign_in)
        }
        return root
    }

    private fun isValidate(): Boolean {

        if (binding.etRmobilenumber.text.toString().isNullOrEmpty()) {
            showToast("Please enter Reference Mobile Number")
            return false
        }
        if (!isValidMobile(binding.etRmobilenumber.text.toString())) {
            showToast("Please enter Valid Reference Mobile Number")
            return false
        }

        return true
    }



    private fun setObserver() {
        referencememberViewModel.signUpResponse.observe(viewLifecycleOwner){response->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes->
                        if (resposnes?.Status == 200) {
                            showToast(response.data?.Message ?: "")
                            findNavController().navigate(R.id.nav_sign_in)
                        }else{
                            showToast(response.data?.Message ?: "")

                        }
                    }
                }
                is NetworkResult.Error -> {
                    referencememberViewModel.isloading.set(false)
                    response.message?.let { showToast(response.message) }
                }
                is NetworkResult.Loading -> {
                    referencememberViewModel.isloading.set(true)
                }
            }
        }
    }

    private fun isValidMobile(phone: String): Boolean {
        return phone.length >= 10
    }


}