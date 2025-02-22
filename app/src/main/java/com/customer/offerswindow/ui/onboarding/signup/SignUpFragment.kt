package com.customer.offerswindow.ui.onboarding.signup

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.SignUpFragmentBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.PostNewEnquiry
import com.customer.offerswindow.model.stock.StockPurchsasePostingResponse
import com.customer.offerswindow.ui.onboarding.otpdialog.OtpDialogFragment
import com.customer.offerswindow.utils.ShowFullToast
import com.customer.offerswindow.utils.getDateTime
import com.customer.offerswindow.utils.hideOnBoardingToolbar
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private val signInViewModel: SignUpViewModel by viewModels()
    private var _binding: SignUpFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SignUpFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        activity?.hideOnBoardingToolbar()
        setObserver()
        binding.signupBtn.setOnClickListener {
            if (isValidate()) {
               signInViewModel.postOtp(binding.etMobilenumber.text.toString())
            }
        }
        binding.signinLbl.setOnClickListener {
            findNavController().navigate(R.id.nav_sign_in)
        }
        return root
    }

    private fun isValidate(): Boolean {
        if (binding.etName.text.toString().isNullOrEmpty()) {
            showToast("Please enter name")
            return false
        }
        if (binding.etMobilenumber.text.toString().isNullOrEmpty()) {
            showToast("Please enter Mobile number")
            return false
        }
        if (!isValidMobile(binding.etMobilenumber.text.toString())) {
            showToast("Please enter Valid Mobile Number")
            return false
        }
        if (binding.etEmail.text.toString().isNullOrEmpty()){
            showToast("Please enter Email")
        }

        if (!isValidMail(binding.etEmail.text.toString())) {
            showToast("Please enter valid Email")
            return false
        }
        return true
    }



    private fun setObserver() {
        signInViewModel.signUpResponse.observe(viewLifecycleOwner){response->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes->
                        if (resposnes?.Status == 200) {
                            ShowFullToast(response.data?.Message ?: "")
                            findNavController().navigate(R.id.nav_sign_in)
                        } else {
                            ShowFullToast(response.data?.Message ?: "")
                        }
                    }
                }
                is NetworkResult.Error -> {
                    signInViewModel.isloading.set(false)
                    response.message?.let { ShowFullToast(response.message) }
                }
                is NetworkResult.Loading -> {
                    signInViewModel.isloading.set(true)
                }
            }
        }
        signInViewModel.otpResponse.observe(viewLifecycleOwner){response->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes->
                        if (resposnes?.Status == 200) {
                            ShowFullToast(response.data?.Message ?: "")
                            openOTPScreen()
                        } else {
                            ShowFullToast(response.data?.Message ?: "")
                        }
                    }
                }
                is NetworkResult.Error -> {
                    signInViewModel.isloading.set(false)
                    response.message?.let { ShowFullToast(response.message) }
                }
                is NetworkResult.Loading -> {
                    signInViewModel.isloading.set(true)
                }
            }
        }
    }

    private fun openOTPScreen() {
        val modalBottomSheet = OtpDialogFragment.newInstance(binding.etMobilenumber.text.toString(), "",object :
            OtpDialogFragment.OtpStatusListener {
            override fun OTPSuccess(stockPurchsasePostingResponse: StockPurchsasePostingResponse) {
                if (stockPurchsasePostingResponse.Status == 200) {
                    signInViewModel.isloading.set(true)
                    postSignUpInfo()
                }
            }

            override fun OtpFailure(stockPurchsasePostingResponse: StockPurchsasePostingResponse) {
                if (stockPurchsasePostingResponse.Status != 200) {
                    ShowFullToast(stockPurchsasePostingResponse.Message)
                }
            }
        })
        modalBottomSheet.show(requireActivity().supportFragmentManager, "OTPDIALOG")
    }

    private fun isValidMail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun isValidMobile(phone: String): Boolean {
        return phone.length >= 10
    }

    fun postSignUpInfo() {
        signInViewModel.isloading.set(true)
        signInViewModel.postSignUp(
            PostNewEnquiry(
                binding.etName.text.toString(),
                binding.etMobilenumber.text.toString(),
                binding.etEmail.text.toString(),
                binding.etRefmno.text.toString(),
                0,
                0,
                "",
                "",
                arrayListOf(),
                AppPreference.read(Constants.USERUID, "") ?: "",
                getDateTime(),
                AppPreference.read(Constants.USERUID, "") ?: "",
                getDateTime()
            )
        )
    }

}