package com.customer.offerswindow.ui.onboarding.signIn

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.BuildConfig
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentSignInBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.UserResponse
import com.customer.offerswindow.model.masters.CommonDataResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.ui.dashboard.DashboardActivity
import com.customer.offerswindow.utils.PermissionsUtil
import com.customer.offerswindow.utils.ShowFullToast
import com.customer.offerswindow.utils.handleHardWareBackClick
import com.customer.offerswindow.utils.hideOnBoardingToolbar
import com.customer.offerswindow.utils.showCommonCustomIOSDialog
import com.customer.offerswindow.utils.showToast
import com.onesignal.OneSignal
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignInFragment : Fragment() {
    private val signInViewModel: SignInViewModel by viewModels()
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    var emailUsertype = ""
    var privacyUrl = ""
    var termsurl = ""
    val permissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_PHONE_NUMBERS,
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.vm = signInViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setDoneListener()
        activity?.hideOnBoardingToolbar()
        handleHardWareBackClick {
            handleBackClick()
        }
        if (!arguments?.getString(Constants.MOBILENO).isNullOrEmpty()) {
            binding.etMobilenumber.setText(arguments?.getString(Constants.MOBILENO))
        }
        if (!arguments?.getString("Message").isNullOrEmpty()) {
            ShowFullToast(arguments?.getString("Message") ?: "")
        }
        setObserver()
        signInViewModel.isloading.set(true)
        signInViewModel.getMstData()
        //9619117769
        //9000099288
        binding.versionTextview.text = "Version :".plus(" ( " + BuildConfig.VERSION_NAME + " ) ")
        binding.verify.setOnClickListener {
            if (validateUserLogin()) {
                signInViewModel.isloading.set(true)
                AppPreference.write(Constants.LOGINUSERNAME, binding.etMobilenumber.text.toString())
                AppPreference.write(Constants.LOGINPASSWORD, binding.etPswrd.text.toString())
                signInViewModel.verifyLogin(
                    binding.etMobilenumber.text.toString()
                )
            }

            val intent = Intent(requireActivity(), DashboardActivity::class.java)
            startActivity(intent)
        }
        binding.privacyTxt.setOnClickListener {
            requireActivity().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl)))
        }
        binding.termsTxt.setOnClickListener {
            requireActivity().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(termsurl)))
        }
        binding.siginupTxt.setOnClickListener {
            findNavController().navigate(R.id.nav_sign_up)
        }
        binding.forgotpswrd.setOnClickListener {
            if (binding.etMobilenumber.text.toString().isNullOrEmpty()) {
                showToast("Please enter Mobile number")
            } else {
                signInViewModel.isloading.set(true)
                signInViewModel.forgotPassword(binding.etMobilenumber.text.toString())
            }
        }
        PermissionsUtil.askPermissions(requireActivity())
        return root
    }

    private fun validateUserLogin(): Boolean {
        if (binding.etMobilenumber.text.isNullOrEmpty()) {
            binding.etMobilenumber.error = "Please enter valid mobile number"
            return false
        }
//        if (binding.etPswrd.text.isNullOrEmpty()) {
//            binding.etMobilenumber.error = null
//            binding.etPswrd.error = "Please enter valid password"
//            return false
//        }
        binding.etMobilenumber.error = null
//        binding.etPswrd.error = null
        return true
    }

    private fun setObserver() {
        signInViewModel.tokenResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        signInViewModel.isloading.set(false)
                        AppPreference.write(Constants.TOKENTIMER, resposnes?.expires_in ?: "0")
                        signInViewModel.verifyLogin(
                            binding.etMobilenumber.text.toString(),
                            binding.etPswrd.text.toString()
                        )
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
        signInViewModel.userResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        signInViewModel.isloading.set(false)
                        if (!response.data?.Message.isNullOrEmpty()) {
                            ShowFullToast(response.data?.Message ?: "")
                        } else {
                            ShowFullToast(
                                response.message ?: getString(R.string.something_went_wrong)
                            )
                        }
                        if (resposnes?.Status == 200) {
                            if (!resposnes.Data.firstOrNull()?.Cust_Version.isNullOrEmpty()) {
                                navigateUser(resposnes, response)
                            }
                        } else {
                            signInViewModel.isloading.set(false)
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
        signInViewModel.forgotpasswordResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    signInViewModel.isloading.set(false)
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            ShowFullToast(response.data?.Message ?: "")
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
                }
            }
        }
        signInViewModel.masterdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    signInViewModel.isloading.set(false)
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            var responsevalue = getData(response, "Privacy_Policy")
                            privacyUrl = responsevalue?.MstDesc ?: ""
                            if (privacyUrl.isNullOrEmpty()) {
                                binding.privacyTxt.visibility = View.GONE
                            }
                            var responsetermsvalue = getData(response, "Terms_Of_Services")
                            termsurl = responsetermsvalue?.MstDesc ?: ""
                            if (termsurl.isNullOrEmpty()) {
                                binding.termsTxt.visibility = View.GONE
                            }
                        } else {
//                            ShowFullToast(response.data?.Message ?: "")
                        }
                    }
                }

                is NetworkResult.Error -> {
                    signInViewModel.isloading.set(false)
//                    response.message?.let { ShowFullToast(response.message) }
                }

                is NetworkResult.Loading -> {
                }
            }
        }
    }

    private fun navigateUser(resposnes: UserResponse, response: NetworkResult<UserResponse>) {
        signInViewModel.isloading.set(false)
        AppPreference.write(Constants.ISLOGGEDIN, true)
        AppPreference.write(
            Constants.DEFAULT_NAME,
            resposnes.Data.firstOrNull()?.Cust_Name ?: ""
        )
        AppPreference.write(
            Constants.DEFAULT_EMAIL,
            resposnes.Data.firstOrNull()?.Email_ID ?: ""
        )
        AppPreference.write(
            Constants.DEFAULT_PHOTO,
            resposnes.Data.firstOrNull()?.Cust_Image_Path ?: ""
        )
//        OneSignal.setEmail(resposnes.Data.firstOrNull()?.Email_ID ?: "");
        AppPreference.write(
            Constants.NAME,
            resposnes.Data.firstOrNull()?.Cust_Name ?: ""
        )
        AppPreference.write(
            Constants.EMAIL,
            resposnes.Data.firstOrNull()?.Email_ID ?: ""
        )
        AppPreference.write(
            Constants.PROFILEPIC,
            resposnes.Data.firstOrNull()?.Cust_Image_Path ?: ""
        )
        AppPreference.write(
            Constants.PLANCODE,
            resposnes.Data.firstOrNull()?.Plan_Code ?: ""
        )
        AppPreference.write(
            Constants.MOBILENO,
            resposnes.Data.firstOrNull()?.User_ID ?: ""
        )
//        OneSignal.sendTag(
//            "CustomerUID",
//            resposnes.Data.firstOrNull()?.User_UID ?: ""
//        )
        // Pass in phone number provided by customer
        //OneSignal.setSMSNumber(resposnes.data.firstOrNull()?.Email_ID);
        response.data?.Data?.get(0)?.User_UID?.let {
            AppPreference.write(
                Constants.USERUID,
                it
            )
        }
        signInViewModel.isloading.set(false)
        val intent = Intent(requireActivity(), DashboardActivity::class.java)
        startActivity(intent)
    }

    private fun getData(data: NetworkResult<CommonMasterResponse>, s: String): CommonDataResponse? {
        return data?.data?.data?.find {
            s == it.MstType
        }
    }

    private fun handleBackClick() {
        activity?.finishAffinity()
    }

    private fun setDoneListener() {
//        binding.mobile.setOnEditorActionListener() { v, actionId, event ->
//            when (actionId) {
//                EditorInfo.IME_ACTION_DONE -> {
//                    v.hideKeyboard()
//                    if (signInViewModel.isNextEnable.get() == true) {
////                        signInViewModel.isError.set(false)
//                        validate()
//                    }
//                    true
//                }
//                else -> false
//            }
//        }
//        binding.email.setOnEditorActionListener() { v, actionId, event ->
//            when (actionId) {
//                EditorInfo.IME_ACTION_DONE -> {
//                    v.hideKeyboard()
//                    if ((Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString())
//                            .matches())
//                    ) {
//                        if (signInViewModel.isNextEnable.get() == true) {
//                            validate()
//                        }
//                    } else {
//                        signInViewModel.isError.set(true)
//                        signInViewModel.isMobile.set(false)
//                        binding.mobileError.text =
//                            getString(R.string.please_enter_valid_email_address)
//                    }
//                    true
//                }
//                else -> false
//            }
//        }
    }


    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(false)

    }






}