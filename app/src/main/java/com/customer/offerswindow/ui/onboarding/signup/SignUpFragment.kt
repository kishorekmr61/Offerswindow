package com.customer.offerswindow.ui.onboarding.signup

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.R
import com.customer.offerswindow.databinding.SignUpFragmentBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.customersdata.PostSignUp
import com.customer.offerswindow.ui.onboarding.signIn.SignInViewModel
import com.customer.offerswindow.utils.ShowFullToast
import com.customer.offerswindow.utils.handleHardWareBackClick
import com.customer.offerswindow.utils.hideOnBoardingToolbar
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private val signUpViewModel: SignUpViewModel by viewModels()
    private val signInViewModel: SignInViewModel by viewModels()
    private var _binding: SignUpFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleHardWareBackClick {
            handleBackClick()
        }
    }

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
                postSignUpInfo()
            }
        }
        binding.signinLbl.setOnClickListener {
            findNavController().navigate(R.id.nav_sign_in)
        }
        binding.etMobilenumber.doAfterTextChanged {
            if (it?.length!! >= 9) {
                binding.verify.visibility = View.VISIBLE
            } else {
                binding.verify.visibility = View.INVISIBLE
            }
        }

        binding.verify.setOnClickListener {
            if (binding.etMobilenumber.text.isNullOrEmpty()) {
                binding.etMobilenumber.error = "Please enter valid mobile number"
            } else {
                signInViewModel.isloading.set(true)
                signInViewModel.getOTP(
                    binding.etMobilenumber.text.toString()
                )
            }
        }
        binding.resendotpLbl.setOnClickListener {
            if (binding.etMobilenumber.text.toString().isEmpty()) {
                showToast("Please enter Mobile number")
            } else {
                signInViewModel.isloading.set(true)
                signInViewModel.getOTP(
                    binding.etMobilenumber.text.toString()
                )
            }
        }
        binding.signinLbl.setOnClickListener {
            findNavController().navigate(R.id.nav_sign_in)
        }
        return root
    }

    private fun isValidate(): Boolean {
        if (binding.etName.text.toString().isEmpty()) {
            showToast("Please enter name")
            return false
        }
        if (binding.etLastname.text.toString().isEmpty()) {
            showToast("Please enter LastName")
            return false
        }
        if (binding.etMobilenumber.text.toString().isEmpty()) {
            showToast("Please enter Mobile number")
            return false
        }
        if (!isValidMobile(binding.etMobilenumber.text.toString())) {
            showToast("Please enter Valid Mobile Number")
            return false
        }
        if (binding.etEmail.text.toString().isEmpty()) {
            showToast("Please enter Email")
        }

        if (!isValidMail(binding.etEmail.text.toString())) {
            showToast("Please enter valid Email")
            return false
        }
        if (binding.etPin.text.toString().isEmpty()) {
            showToast("Please set 4 digit pin")
            return false
        }
        if (binding.etPin.text.toString().length > 4) {
            showToast("Please set valid PIN")
            return false
        }
        if (binding.confirmetPin.text.toString().isEmpty()) {
            showToast("confirm 4 digit pin")
            return false
        }
        if (binding.confirmetPin.text.toString().length > 4) {
            showToast("confirm 4 digit pin")
            return false
        }
        if (binding.confirmetPin.text.toString() != binding.etPin.text.toString()) {
            showToast("set pin and Confirm is not matching  please check and try")
            return false
        }

        return true
    }


    private fun setObserver() {
        signUpViewModel.signUpResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
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

    }


    private fun isValidMail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidMobile(phone: String): Boolean {
        return phone.length >= 10
    }

    fun postSignUpInfo() {
        signInViewModel.isloading.set(true)
        var postSignup = PostSignUp(
            binding.etMobilenumber.text.toString(),
            binding.etEmail.text.toString(),
            binding.etName.text.toString(),
            binding.etLastname.text.toString(),
            binding.etOtp.text.toString(),
            binding.etPin.text.toString()
        )
        signUpViewModel.postSignUp(
            postSignup
        )
    }


    private fun handleBackClick() {
        findNavController().popBackStack()
    }
}