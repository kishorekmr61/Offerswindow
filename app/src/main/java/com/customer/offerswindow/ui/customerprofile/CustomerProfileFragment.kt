package com.customer.offerswindow.ui.customerprofile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.customer.offerswindow.model.dashboard.ProfileUpdateRequest
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.ui.onboarding.OnBoardingActivity
import com.customer.offerswindow.utils.ShowFullToast
import com.customer.offerswindow.utils.bottomsheet.OnItemSelectedListner
import com.customer.offerswindow.utils.bottomsheet.SpinnerBottomSheet
import com.customer.offerswindow.utils.convertDate
import com.customer.offerswindow.utils.getFilePathFromUri
import com.customer.offerswindow.utils.isEmailValid
import com.customer.offerswindow.utils.setWhiteToolBar
import com.customer.offerswindow.utils.showCalenderDialog
import com.customer.offerswindow.utils.showCommonCustomIOSDialog
import com.customer.offerswindow.utils.showLongToast
import com.customer.offerswindow.utils.showToast
import com.customer.offerswindow.utils.userimagecapture.ActionBottomSheet
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Calendar


@AndroidEntryPoint
class CustomerProfileFragment : Fragment() {

    var _binding: FragmentCustomerProfileBinding? = null
    val binding get() = _binding!!
    var masterdata = arrayListOf<SpinnerRowModel>()
    private var selectedType: String = ""
    private val viewModel: CustomerProfileViewModel by viewModels()
    private val vm: DashBoardViewModel by activityViewModels()
    var tempFile: File? = null
    lateinit var mcustomerData: CustomerData
    var cityList = arrayListOf<SpinnerRowModel>()
    var countryList = arrayListOf<SpinnerRowModel>()
    var locationid: String = ""
    var countryid: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomerProfileBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        vm.isvisble.value = false
        activity?.setWhiteToolBar("My Profile", true)
        setUpListeners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInputFilters()
        setInputFilters()
        setObserver()
        viewModel.isloading.set(true)
        viewModel.getMstData()
        viewModel.getDashboardData(AppPreference.read(Constants.MOBILENO, "") ?: "")
    }

    private fun setInputFilters() {
        // Set input filters for name and height fields
        binding.etFirstname.filters = arrayOf(NameInputFilter())
        binding.editprofileImg.setOnClickListener {
            mcustomerData.Country_Code = countryid
            mcustomerData.Location_Code = locationid
            mcustomerData.Cust_Name = binding.etFirstname.text.toString()
            mcustomerData.Country_Desc = binding.etCountry.text.toString()
            mcustomerData.Country_Desc = binding.etCountry.text.toString()
            mcustomerData.Email_ID = binding.etEmail.text.toString()
            mcustomerData.DOB = convertDate(
                binding.etDob.text.toString(),
                Constants.DDMMYYYY,
                Constants.YYY_HIFUN_MM_DD
            )
            mcustomerData.Location_Desc = binding.etCity.text.toString()
            mcustomerData.Mobile_No = AppPreference.read(Constants.MOBILENO, "") ?: ""
            mcustomerData.Pin_No = binding.etPincode.text.toString()
            triggerCameraOrGallerySelection(binding.profilepic)
        }
    }

    private fun setUpListeners() {
        binding.setVariable(BR.onItemClick, View.OnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.ISFROM, "")
            when (it.id) {
                R.id.signout_lbl -> {
                    val intent = Intent(requireActivity(), OnBoardingActivity::class.java)
                    intent.putExtra(
                        Constants.MOBILENO,
                        AppPreference.read(Constants.MOBILENO, "") ?: ""
                    )
                    AppPreference.write(Constants.ISLOGGEDIN, false)
                    AppPreference.clearAll()
                    AppPreference.write(Constants.SKIPSIGNIN, true)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }

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
        binding.etDob.setOnClickListener {
            openCalendar(binding.etDob)
        }
        binding.updateBtn.setOnClickListener {
            if (isEmailValid(binding.etEmail.text.toString() ?: "")) {
                var photoFile: File? = null
                binding.profilepic.tag?.let {
                    if (selectedType == "Camera") {
                        photoFile = tempFile
                    } else {
                        getFilePathFromUri(context?.contentResolver!!, it as Uri)?.let {
                            photoFile = File(it)
                        }
                    }
                    photoFile?.let {
                        val photoRequestBody = it.asRequestBody("image/*".toMediaTypeOrNull())
                        val photoPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                            "ImageFile",
                            it.name,
                            photoRequestBody
                        )
                        viewModel.isloading.set(true)
                        postData(true, photoPart)
                    }


                } ?: kotlin.run {
                    val emptyBody = "".toRequestBody(null)
                    val photoPart: MultipartBody.Part =
                        MultipartBody.Part.createFormData("ImageFile", "", emptyBody)
                    viewModel.isloading.set(true)
                    postData(false, photoPart)
                }
            } else {
                binding.etEmail.setText("")
                binding.etEmail.requestFocus()
                showToast("Please enter valid email")
            }


        }
    }

    private fun postData(isPhotoAvailable: Boolean, photoPart: MultipartBody.Part) {
        if (!isPhotoAvailable) {
            viewModel.isloading.set(true)
            var postcustomerdata = ProfileUpdateRequest(
                mcustomerData.Cust_UID,
                mcustomerData?.Cust_Name ?: "",
                mcustomerData.Cust_Last_Name ?: "",
                mcustomerData.Email_ID,
                mcustomerData.Mobile_No,
                mcustomerData.DOB,
                mcustomerData.Location_Code,
                mcustomerData.Country_Code,
                mcustomerData.Pin_No,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTCB6HqWvcVC3HlKduT7nP44d_RYuJOIFTwcA4LQQo0zA8GRbW_N9wEwaF1kBiMPoKcnN4&usqp=CAU",
            )
            viewModel.updateProfileData(
                postcustomerdata
            )
        } else {
            viewModel.registrationData.value?.apply {
                val formDataJson = JsonObject()
                formDataJson.addProperty(
                    "CustomerId",
                    AppPreference.read(Constants.USERUID, "") ?: ""
                )
                formDataJson.addProperty(
                    "PhoneNo",
                    AppPreference.read(Constants.MOBILENO, "") ?: ""
                )
                formDataJson.addProperty("CustomerName", binding.etFirstname.text.toString())
                formDataJson.addProperty("LastName", mcustomerData.Cust_Last_Name)
                formDataJson.addProperty("EmailID", binding.etEmail.text.toString())
                formDataJson.addProperty(
                    "DoB",
                    convertDate(
                        binding.etDob.text.toString(),
                        Constants.DDMMYYYY,
                        Constants.YYY_HIFUN_MM_DD
                    )
                )

                formDataJson.addProperty("LocationId", mcustomerData.Location_Code)
                formDataJson.addProperty("CountryId", mcustomerData.Country_Code)
                formDataJson.addProperty("PinCode", binding.etPincode.text.toString())
                formDataJson.addProperty(
                    "CustomerPhotoFilePath",
                    if (!isPhotoAvailable) CustomerImageUrl else ""
                )
                formDataJson.addProperty("CreatedBy", CreatedBy)
                formDataJson.addProperty("CreatedDateTime", CreatedDateTime)
                formDataJson.addProperty("UpdatedBy", UpdatedBy)
                formDataJson.addProperty("UpdatedDateTime", UpdatedDateTime)
                val formDataBody: RequestBody =
                    RequestBody.create(
                        "application/json".toMediaTypeOrNull(),
                        formDataJson.toString()
                    )
                viewModel.updateProfileData(
                    photoPart,
                    formDataBody
                )
            }
        }

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
        }, Constants.DDMMYYYY, Calendar.getInstance().timeInMillis)

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
        viewModel.masterdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        response?.data?.data?.forEach {
                            if (it.MstType == "Country") {
                                countryList.add(
                                    SpinnerRowModel(
                                        it.MstDesc, false,
                                        false,
                                        mstCode = it.MstCode
                                    )
                                )
                            }

                            if (it.MstType == "City") {
                                cityList.add(
                                    SpinnerRowModel(
                                        it.MstDesc,
                                        false,
                                        false,
                                        mstCode = it.MstCode
                                    )
                                )
                            }
                        }
                    }

                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                }

                else -> {}
            }
        }
        viewModel.customersdatapost.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        viewModel.isloading.set(false)
                        response.data.let { resposnes ->
                            if (resposnes?.Status == 200) {
                                findNavController().popBackStack()
                            } else {
                                ShowFullToast(response.data?.Message ?: "")
                            }
                        }
                    }

                }

                is NetworkResult.Error -> {
                    showLongToast(response.data?.Message ?: "")
                    viewModel.isloading.set(false)
                }

                else -> {}
            }
        }
        binding.etCountry.setOnClickListener {
            activity?.let { it1 ->
                val modalBottomSheet = SpinnerBottomSheet.newInstance(Constants.STATUS,
                    binding.etCountry.text.toString(), countryList, false, object :
                        OnItemSelectedListner {
                        override fun onItemSelectedListner(
                            titleData: SpinnerRowModel?,
                            datevalue: String
                        ) {
                            if (titleData != null) {
                                binding.etCountry.setText(titleData.title)
                                countryid = titleData.mstCode
                                mcustomerData.Country_Code = countryid
                            }
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
        binding.etCity.setOnClickListener {
            activity?.let { it1 ->
                val modalBottomSheet = SpinnerBottomSheet.newInstance(Constants.STATUS,
                    binding.etCity.text.toString(), cityList, false, object :
                        OnItemSelectedListner {
                        override fun onItemSelectedListner(
                            titleData: SpinnerRowModel?,
                            datevalue: String
                        ) {
                            if (titleData != null) {
                                binding.etCity.setText(titleData.title)
                                locationid = titleData.mstCode
                                mcustomerData.Location_Code = locationid
                            }
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
                                    binding.etFirstname.setText(mcustomerData.Cust_Name)
                                    binding.etEmail.setText(mcustomerData.Email_ID)
                                    binding.etDob.setText(
                                        convertDate(
                                            mcustomerData.DOB,
                                            Constants.YYYYMMDDTHH,
                                            Constants.DDMMYYYY
                                        )
                                    )
                                    binding.etCountry.setText(mcustomerData.Country_Desc)
                                    binding.etCity.setText(mcustomerData.Location_Desc)
                                    binding.etPincode.setText(mcustomerData.Pin_No)
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
}