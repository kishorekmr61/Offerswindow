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
            mcustomerData.Country = countryid
            mcustomerData.Cust_Name = binding.etFirstname.text.toString()
            mcustomerData.Country_Desc = binding.etCountry.text.toString()
            mcustomerData.Location_Desc = binding.etCity.text.toString()
            mcustomerData.Email_ID = binding.etEmail.text.toString()
            mcustomerData.DOB = convertDate(
                binding.etDob.text.toString(),
                Constants.DDMMYYYY,
                Constants.YYY_HIFUN_MM_DD
            )
            mcustomerData.Location_Code = locationid
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
        binding.saveBtn.setOnClickListener {
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
        viewModel.registrationData.value?.apply {
            val formDataJson = JsonObject()
            formDataJson.addProperty(
                "customerId",
                AppPreference.read(Constants.USERUID, "") ?: "0"
            )
            formDataJson.addProperty(
                "phoneNo",
                AppPreference.read(Constants.MOBILENO, "") ?: ""
            )
            formDataJson.addProperty("customerName", binding.etFirstname.text.toString())
            formDataJson.addProperty("lastName", mcustomerData.Cust_Last_Name)
            formDataJson.addProperty("emailID", binding.etEmail.text.toString())
            formDataJson.addProperty(
                "doB",
                convertDate(
                    binding.etDob.text.toString(),
                    Constants.DDMMYYYY,
                    Constants.YYY_HIFUN_MM_DD
                )
            )

            formDataJson.addProperty("locationId", mcustomerData.Location_Code)
            formDataJson.addProperty("Cust_Image_URL", mcustomerData.Cust_Image_URL)
            formDataJson.addProperty("countryId", mcustomerData.Country)
            formDataJson.addProperty("pinCode", binding.etPincode.text.toString())
            formDataJson.addProperty(
                "CustomerPhotoFilePath",
                if (!isPhotoAvailable) CustomerImageUrl else ""
            )
            formDataJson.addProperty("createdBy", AppPreference.read(Constants.USERUID, "") ?: "0")
            formDataJson.addProperty("createdDateTime", CreatedDateTime)
            formDataJson.addProperty("updatedBy", AppPreference.read(Constants.USERUID, "") ?: "0")
            formDataJson.addProperty("updatedDateTime", UpdatedDateTime)
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

                            if (it.MstType == "Location") {
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
        viewModel.customersdata.observe(viewLifecycleOwner) { response ->
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
                    showLongToast(response?.message ?: "")
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
                                mcustomerData.Country = countryid
                            }
                        }

                        override fun onItemmultipleSelectedListner(
                            titleData: ArrayList<SpinnerRowModel>?,
                            value: ArrayList<SpinnerRowModel>
                        ) {

                        }
                    }, headerlbl = "Country")
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
                    }, headerlbl = "City")
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
                                    binding.item = it
//                                    binding.etFirstname.setText(mcustomerData.Cust_Name)
//                                    binding.etEmail.setText(mcustomerData.Email_ID)
                                    binding.etDob.setText(
                                        convertDate(
                                            mcustomerData.DOB,
                                            Constants.YYYYMMDDTHH,
                                            Constants.DDMMYYYY
                                        )
                                    )
                                    locationid = mcustomerData.Location_Code
                                    countryid = mcustomerData.Country

//                                    binding.etCountry.setText(mcustomerData.Country_Desc)
//                                    binding.etCity.setText(mcustomerData.Location_Desc)
//                                    binding.etPincode.setText(mcustomerData.Pin_No)

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