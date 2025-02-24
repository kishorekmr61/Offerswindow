package com.customer.offerswindow.ui.customerprofile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.BR
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentCustomerProfileBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.CustomerData
import com.customer.offerswindow.model.SpinnerRowModel
import com.customer.offerswindow.model.masters.CommonLocationMasterResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.ui.onboarding.OnBoardingActivity
import com.customer.offerswindow.utils.ShowFullToast
import com.customer.offerswindow.utils.bottomsheet.OnItemSelectedListner
import com.customer.offerswindow.utils.bottomsheet.SpinnerBottomSheet
import com.customer.offerswindow.utils.convertDate
import com.customer.offerswindow.utils.isEmailValid
import com.customer.offerswindow.utils.setWhiteToolBar
import com.customer.offerswindow.utils.showCalenderDialog
import com.customer.offerswindow.utils.showCommonCustomIOSDialog
import com.customer.offerswindow.utils.showToast
import com.customer.offerswindow.utils.userimagecapture.ActionBottomSheet
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Calendar


@AndroidEntryPoint
class CustomerProfileFragment : Fragment() {

    var _binding: FragmentCustomerProfileBinding? = null
    val binding get() = _binding!!
    private var selectedType: String = ""
    var masterdata = arrayListOf<SpinnerRowModel>()
    private val viewModel: CustomerProfileViewModel by viewModels()
    var locationmasterdata = arrayListOf<SpinnerRowModel>()
    var tempFile: File? = null
    lateinit var mcustomerData:CustomerData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomerProfileBinding.inflate(inflater, container, false)
         binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        activity?.setWhiteToolBar("My Profile", true)
        setUpListeners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInputFilters()
        setObserver()
        viewModel.isloading.set(true)
        viewModel.getDashboardData(AppPreference.read(Constants.USERUID, "") ?: "")
        viewModel.getStatusMstData()
        viewModel.getLocationsMst()


    }

    private fun setInputFilters() {
        // Set input filters for name and height fields
        binding.etFirstname.filters = arrayOf(NameInputFilter())
    }

    private fun setUpListeners() {
        binding.etGender.setOnClickListener {
            val masterdata = arrayListOf<SpinnerRowModel>()
            masterdata.add(SpinnerRowModel("Male", false, false, "", ""))
            masterdata.add(SpinnerRowModel("Female", false, false, "", ""))
//            masterdata.add(SpinnerRowModel("TransGender", false, false, "",""))
            activity?.let { it1 ->
                val modalBottomSheet = SpinnerBottomSheet.newInstance(Constants.STATUS,
                    binding.etGender.text.toString(), masterdata, false, object :
                        OnItemSelectedListner {
                        override fun onItemSelectedListner(
                            titleData: SpinnerRowModel?, datevalue: String
                        ) {
                            if (titleData != null) binding.etGender.setText(titleData.title)
                        }

                        override fun onItemmultipleSelectedListner(
                            titleData: ArrayList<SpinnerRowModel>?,
                            value: ArrayList<SpinnerRowModel>
                        ) {

                        }
                    })
                modalBottomSheet.show(it1.supportFragmentManager, SpinnerBottomSheet.TAG)
            }
        }


        binding.etDob.setOnClickListener {
            openCalendar(binding.etDob)
        }

        binding.updateBtn.setOnClickListener {
            try {
                if (isEmailValid(binding.etEmail.text.toString())) {
                    val emptyBody = "".toRequestBody(null)
                    val photoPart: MultipartBody.Part =
                        MultipartBody.Part.createFormData("ImageFile", "", emptyBody)
                    viewModel.isloading.set(true)
                    postData(false, photoPart)

                } else {
                    binding.etEmail.setText("")
                    binding.etEmail.requestFocus()
                    showToast("Please enter valid email")
                }
            } catch (e: Exception) {
                viewModel.isloading.set(false)
                showToast(getString(R.string.something_went_wrong))
            }
        }
        binding.setVariable(BR.onItemClick, View.OnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.ISFROM, "")
            when (it.id) {


                R.id.deleteaccount_lbl -> {
                    showCommonCustomIOSDialog(
                        requireActivity(),
                        "Delete Account ?", "Are you sure? Do you want to delete profile?\n" +
                                "This profile's personal Information - Including the user name, email address and activity - will be gone forever, and you won't be able to access it again.",
                        "Delete",
                        {
                            viewModel.isloading.set(true)
                            viewModel.deleteUserAccount()
                        },
                        "Cancel",
                        {
                        }, true
                    )
                }
            }
        })
    }

    private fun postData(isPhotoAvailable: Boolean, photoPart: MultipartBody.Part) {
        viewModel.registrationData.value?.apply {
            val formDataJson = JsonObject()
            formDataJson.addProperty("CustomerUID", CustomerUID)
            formDataJson.addProperty("CustomerName", binding.etFirstname.text.toString())
            formDataJson.addProperty(
                "DoB",
                convertDate(
                    binding.etDob.text.toString(),
                    Constants.DDMMMYYYY,
                    Constants.YYY_HIFUN_MM_DD
                )
            )
            formDataJson.addProperty("EmailID", binding.etEmail.text.toString())
            formDataJson.addProperty("Gender", getGenderType(binding.etGender.text.toString()))



            formDataJson.addProperty(
                "CustomerPhotoFilePath",
                if (!isPhotoAvailable) CustomerImageUrl else ""
            )
            formDataJson.addProperty("CreatedBy", CreatedBy)
            formDataJson.addProperty("CreatedDateTime", CreatedDateTime)
            formDataJson.addProperty("UpdatedBy", UpdatedBy)
            formDataJson.addProperty("UpdatedDateTime", UpdatedDateTime)
            val formDataBody: RequestBody =
                RequestBody.create("application/json".toMediaTypeOrNull(), formDataJson.toString())
            viewModel.updateProfileData(
                photoPart,
                formDataBody
            )
        }

    }

    private fun getMaritalStatusType(maritalstatus: String): String? {
        return if (maritalstatus == "Married") {
            "M"
        } else if (maritalstatus == "Single")
            "S"
        else if (maritalstatus == "Divorced")
            "D"
        else
            maritalstatus
    }

    private fun getGenderType(gender: String): String {
        return if (gender == "Female") {
            "F"
        } else if (gender == "Male")
            "M"
        else
            ""

    }

    private fun openCalendar(etDob: TextInputEditText) {
        showCalenderDialog(requireActivity(), object : OnItemSelectedListner {
            override fun onItemSelectedListner(
                titleData: SpinnerRowModel?,
                datevalue: String
            ) {
                if (datevalue != null) {
                    etDob.setText(datevalue)
                }
            }

            override fun onItemmultipleSelectedListner(
                titleData: ArrayList<SpinnerRowModel>?,
                value: ArrayList<SpinnerRowModel>
            ) {

            }
        }, Constants.DDMMMYYYY, Calendar.getInstance().timeInMillis)

    }

    private fun triggerCameraOrGallerySelection(img: Any) {
        activity?.let { it1 ->
            val chooseActionBottomSheet = ActionBottomSheet.newInstance(
                false, false,
                object : ActionBottomSheet.OnFileSelectedListener {
                    override fun onFileSelected(
                        uri: Uri?,
                        value: String,
                        flag: String,
                        file: File?
                    ) {
                        if (uri != null) {
                            var bundle = Bundle()
                            bundle.putString(
                                Constants.PROFILEINFO,
                                Gson().toJson(mcustomerData)
                            )
                            bundle.putString(Constants.BUNDLE_IMG_PATH, uri.toString())
                            findNavController().navigate(R.id.nav_manage_profile, bundle)
                        }
                    }

                })
            chooseActionBottomSheet.show(
                it1.supportFragmentManager, ActionBottomSheet.TAG
            )
        }
    }


    private fun setObserver() {
        viewModel.customersdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    viewModel.isloading.set(false)
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            findNavController().popBackStack(R.id.nav_home, false)
                        } else {
                            ShowFullToast(response.data?.Message ?: "")
                        }
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                    response.message?.let { ShowFullToast(response.data?.Message ?: "") }
                }

                is NetworkResult.Loading -> {
                    viewModel.isloading.set(true)
                }
            }
        }

        viewModel.locationsmasterdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        viewModel.isloading.set(false)
                        if (resposnes?.Status == 200) {
                            updateLocatioMasterdata(resposnes)
                        } else {
                            viewModel.isloading.set(false)
                            ShowFullToast(response.data?.Message ?: "")
                        }
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                    response.message?.let { ShowFullToast(response.message) }
                }

                is NetworkResult.Loading -> {

                }
            }
        }
        viewModel.deleteResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    viewModel.isloading.set(false)
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            ShowFullToast(response.data?.Message ?: "")
                            logOutUser(response.data?.Message)
                        } else {
                            ShowFullToast(response.data?.Message ?: "")
                        }
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                    response.message?.let { ShowFullToast(response.message) }
                }

                is NetworkResult.Loading -> {

                }

                else -> {}
            }
        }
        viewModel.customerinfo.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    viewModel.isloading.set(false)
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            response.data?.Data?.firstOrNull()
                                ?.let {
                                    mcustomerData = it
                                   updateUI(it)
                                }
                        }
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                    response.message?.let { ShowFullToast(response.message) }
                }

                is NetworkResult.Loading -> {

                }

                else -> {}
            }
        }
    }

    private fun updateUI(customerData: CustomerData) {

        if (!TextUtils.isEmpty(customerData.DOB)) {
            customerData.DOB = convertDate(customerData.DOB?:"",Constants.YYYYMMDDTHH,Constants.DDMMMYYYY)
        }
        if (!TextUtils.isEmpty(customerData.Marriage_Anniversary)) {
            customerData.Marriage_Anniversary = convertDate(customerData.Marriage_Anniversary?:"",Constants.YYYYMMDDTHH,Constants.DDMMMYYYY)
        }
        if (!TextUtils.isEmpty(customerData.Marital_Status)) {
            customerData.Marital_Status =  viewModel.getMaritalinfo(customerData.Marital_Status?:"")
        }
        if (!TextUtils.isEmpty(customerData.Gender)) {
            customerData.Gender =  viewModel.getGenderinfo(customerData.Gender)
        }

        binding.item = customerData
        binding.etGender.isEnabled = customerData?.Gender?.trim().isNullOrEmpty()
        binding.etDob.isEnabled = customerData?.DOB?.trim().isNullOrEmpty()
        binding.etEmail.isEnabled = customerData?.Email_ID?.trim().isNullOrEmpty()

        viewModel.bindCustomerData(customerData)
        binding.executePendingBindings()
    }

    private fun logOutUser(message: String?) {
        val intent = Intent(requireActivity(), OnBoardingActivity::class.java)
        intent.putExtra(
            Constants.MOBILENO,
            AppPreference.read(Constants.MOBILENO, "") ?: ""
        )
        intent.putExtra(Constants.ISFROM, "LOGOUT")
        intent.putExtra("Message", message)
        AppPreference.write(Constants.ISLOGGEDIN, false)
        AppPreference.clearAll()
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private inner class NameInputFilter : InputFilter {
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence {
            val filtered = StringBuilder()
            for (i in start until end) {
                val character = source[i]
                if (Character.isLetter(character) || character == ' ') {
                    filtered.append(character)
                }
            }
            return filtered.toString()
        }
    }

    // InputFilter for accepting only numbers and dot in the height field
    private inner class HeightInputFilter : InputFilter {
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence {
            val filtered = StringBuilder()
            for (i in start until end) {
                val character = source[i]
                if (Character.isDigit(character) || character == '.') {
                    filtered.append(character)
                }
            }
            return filtered.toString()
        }
    }

    private fun updateMasterdata(resposnes: CommonMasterResponse) {
        resposnes.data.forEach {
            if (it.MstType == "Customer_Category" && it.MstStatus == "A") {
                masterdata.add(SpinnerRowModel(it.MstDesc, false, false, "", it.MstCode))
            }
        }
    }

    private fun updateLocatioMasterdata(resposnes: CommonLocationMasterResponse) {
        locationmasterdata.clear()
        resposnes.Data.forEach {
            locationmasterdata.add(SpinnerRowModel(it.Location_Desc.trim(), false, false, "", "0"))
        }
    }
}