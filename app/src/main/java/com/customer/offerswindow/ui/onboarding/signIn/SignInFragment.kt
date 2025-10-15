package com.customer.offerswindow.ui.onboarding.signIn

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.BuildConfig
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentSignInBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.masters.CommonDataResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.ui.dashboard.DashboardActivity
import com.customer.offerswindow.ui.home.HomeViewModel
import com.customer.offerswindow.utils.PermissionsUtil
import com.customer.offerswindow.utils.ShowFullToast
import com.customer.offerswindow.utils.handleHardWareBackClick
import com.customer.offerswindow.utils.hideOnBoardingToolbar
import com.customer.offerswindow.utils.openURL
import com.customer.offerswindow.utils.showLongToast
import com.customer.offerswindow.utils.showToast
import com.google.gson.Gson
import com.onesignal.OneSignal
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignInFragment : Fragment() {
    private val signInViewModel: SignInViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val vm: DashBoardViewModel by activityViewModels()
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    var emailUsertype = ""
    var privacyUrl = ""
    var termsurl = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.vm = signInViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        vm.isvisble.value = false
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
        signInViewModel.getMstData()
        if (!TextUtils.isEmpty(AppPreference.read(Constants.MOBILENO, ""))) {
            binding.etMobilenumber.setText(AppPreference.read(Constants.MOBILENO, "") ?: "")
        }
        binding.versionTextview.text =
            getString(R.string.version).plus(" ( " + BuildConfig.VERSION_NAME + " ) ")

        binding.privacyTxt.setOnClickListener {
            requireActivity().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl)))
        }
        binding.termsTxt.setOnClickListener {
            requireActivity().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(termsurl)))
        }
        binding.siginupTxt.setOnClickListener {
            findNavController().navigate(R.id.nav_sign_up)
        }
        binding.loginBtn.setOnClickListener {
            if (binding.etMobilenumber.text.isNullOrEmpty()) {
                binding.etMobilenumber.error = "Please enter valid mobile number"

            } else if (binding.etPswrd.text.isNullOrEmpty()) {
                binding.etMobilenumber.error = null
                binding.etPswrd.error = "Please enter valid OTP"
            } else {
                signInViewModel.isloading.set(true)
                signInViewModel.validateOTP(
                    binding.etMobilenumber.text.toString(), "", binding.etPswrd.text.toString()
                )
            }
        }
        binding.resendTxt.setOnClickListener {
            if (binding.etMobilenumber.text.toString().isEmpty()) {
                showToast("Please enter Mobile number")
            } else {
                signInViewModel.isloading.set(true)
                signInViewModel.forgotPassword(
                    binding.etMobilenumber.text.toString()
                )
            }
        }
        binding.privacyTxt.setOnClickListener {
            openURL(Uri.parse(AppPreference.read(Constants.PRIVACYPOLICY, "www.google.com")))
        }
        PermissionsUtil.askPermissions(requireActivity())
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )
        return root
    }

    private fun validateUserLogin(): Boolean {
        if (binding.etMobilenumber.text.isNullOrEmpty()) {
            binding.etMobilenumber.error = "Please enter valid mobile number"
            return false
        }
        binding.etMobilenumber.error = null
        return true
    }

    private fun setObserver() {
        signInViewModel.userResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        signInViewModel.isloading.set(false)
                        if (!response.data?.Message.isNullOrEmpty()) {
                            if (response.data?.Status == 200) {
                                binding.etPswrd.visibility = View.VISIBLE
                                binding.loginBtn.visibility = View.VISIBLE
                                showToast("OTP sent Successfully")
                                binding.etPswrd.requestFocus()
                                binding.etPswrd.isSelected = true

                            } else {
                                ShowFullToast(response.data?.Message ?: "")
                            }
                        } else {
                            ShowFullToast(
                                response.message ?: getString(R.string.something_went_wrong)
                            )
                        }
                        if (resposnes?.Status == 200) {

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
        signInViewModel.OtpResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        signInViewModel.isloading.set(false)
                        if (resposnes?.Status == 401) {
                            findNavController().navigate(R.id.nav_sign_up)
                        } else if (resposnes?.Status != 200) {
                            showLongToast(
                                resposnes?.Message ?: getString(R.string.something_went_wrong)
                            )
                        } else {
                            signInViewModel.isloading.set(true)
                            signInViewModel.getUserInfo(
                                binding.etMobilenumber.text.toString()
                            )
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
                            response?.data?.data?.forEach {
                                val json = Gson().toJson(
                                    response.data?.data ?: arrayListOf<CommonDataResponse>()
                                )
                                AppPreference.write(Constants.MASTERDATA, json)
                                if (it.MstType == "About_us_url") {
                                    AppPreference.write(Constants.ABOUTUS, it.MstDesc)
                                }
                                if (it.MstType == "Privacy_Policy") {
                                    AppPreference.write(Constants.PRIVACYPOLICY, it.Image_path)
                                }
                                if (it.MstType == "Gold_Trend_Report") {
                                    AppPreference.write(Constants.GOLDTRENDREPORT, it.Image_path)
                                }
                                if (it.MstType == "Share_Message") {
                                    AppPreference.write(Constants.SHAREMESSAGE, it.MstDesc)
                                }
                                if (it.MstType == "Google_Playstore_Link") {
                                    AppPreference.write(Constants.GOOGLEPLAYSTORELINK, it.MstDesc)
                                }
                                if (it.MstType == "App_Share_Message") {//message + applink
                                    AppPreference.write(Constants.App_Share_Message, it.MstDesc)
                                }
                                if (it.MstType == "WhatsApp_Message") {
                                    AppPreference.write(Constants.WhatsAppMessage, it.MstDesc)
                                }
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

        signInViewModel.customerinfo.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        homeViewModel.isloading.set(false)
                        AppPreference.write(
                            Constants.NAME,
                            resposnes?.Data?.firstOrNull()?.Cust_Name ?: ""
                        )
                        AppPreference.write(Constants.ISLOGGEDIN, true)
                        AppPreference.write(
                            Constants.USERUID,
                            resposnes?.Data?.firstOrNull()?.Cust_UID ?: "0"
                        )
                        OneSignal.setEmail(resposnes.Data?.firstOrNull()?.Email_ID ?: "");
                        OneSignal.sendTag(
                            "CustomerUID",
                            resposnes.Data?.firstOrNull()?.Cust_UID ?: ""
                        )
                        AppPreference.write(
                            Constants.MOBILENO,
                            resposnes?.Data?.firstOrNull()?.Mobile_No ?: ""
                        )
                        AppPreference.write(
                            Constants.PIN,
                            resposnes?.Data?.firstOrNull()?.Pin_No ?: ""
                        )
                        AppPreference.write(
                            Constants.PROFILEPIC,
                            resposnes?.Data?.firstOrNull()?.Cust_Image_URL ?: ""
                        )
                        homeViewModel.profilepic.set(AppPreference.read(Constants.PROFILEPIC, ""))
                    }
                    homeViewModel.isloading.set(false)
                    val intent = Intent(requireActivity(), DashboardActivity::class.java)
                    startActivity(intent)
                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
                }

                else -> {}
            }
        }
    }


    private fun getData(
        data: NetworkResult<CommonMasterResponse>,
        s: String
    ): CommonDataResponse? {
        return data?.data?.data?.find {
            s == it.MstType
        }
    }

    private fun handleBackClick() {
        findNavController().popBackStack()
    }


    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(false)

    }


}