package com.customer.offerswindow.ui.adddpost

import android.Manifest
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentAddPostBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.SpinnerRowModel
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.utils.ShowFullToast
import com.customer.offerswindow.utils.bottomsheet.OnItemSelectedListner
import com.customer.offerswindow.utils.bottomsheet.SpinnerBottomSheet
import com.customer.offerswindow.utils.getDateTime
import com.customer.offerswindow.utils.isValidEmail
import com.customer.offerswindow.utils.setWhiteToolBar
import com.customer.offerswindow.utils.showCalenderDialog
import com.customer.offerswindow.utils.showToast
import com.customer.offerswindow.utils.userimagecapture.ActionBottomSheet
import com.customer.offerswindow.utils.validateMobilenumber
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.util.Calendar


@AndroidEntryPoint
open class AddPostFragment : Fragment() {

    companion object {
        fun newInstance() = AddPostFragment()
    }

    private val viewModel: AddPostViewModel by viewModels()

    var cityList = arrayListOf<SpinnerRowModel>()
    var categoriesList = ArrayList<SpinnerRowModel>()
    var locationid: String = ""
    var categoryid: String = ""
    var cityid: String = ""
    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    private val vm: DashBoardViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        activity?.setWhiteToolBar("Add Post", true)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBack()
                }
            }
        )
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        vm.hidetoolbar.value = true
        viewModel.isloading.set(true)
        vm.isvisble.value = false
        viewModel.getMstData()
        binding.saveBtn.setOnClickListener {
            if (validateFileds()) {
                viewModel.isloading.set(true)
                if (!binding.isimageRequired.isChecked) {
                    val formDataBody: RequestBody =
                        RequestBody.create(
                            "application/json".toMediaTypeOrNull(),
                            passData().toString()
                        )
                    viewModel.submitPost(
                        null,
                        formDataBody
                    )
                }
            }
        }
        binding.etCategory.setOnClickListener {
            activity?.let { it1 ->
                val modalBottomSheet = SpinnerBottomSheet.newInstance(
                    Constants.STATUS,
                    binding.etLocation.text.toString(),
                    categoriesList,
                    false,
                    object :
                        OnItemSelectedListner {
                        override fun onItemSelectedListner(
                            titleData: SpinnerRowModel?,
                            datevalue: String
                        ) {
                            if (titleData != null) {
                                binding.etCategory.setText(titleData.title)
                                categoryid = titleData.mstCode
                            }
                        }

                        override fun onItemmultipleSelectedListner(
                            titleData: ArrayList<SpinnerRowModel>?,
                            value: ArrayList<SpinnerRowModel>
                        ) {

                        }
                    },
                    headerlbl = "Service"
                )
                modalBottomSheet.show(it1.supportFragmentManager, SpinnerBottomSheet.TAG)
            }
        }
        binding.uploadButton.setOnClickListener {
            if (validateFileds()) {
                if (!binding.isimageRequired.isChecked) {
                    triggerCameraOrGallerySelection()
                } else {
                    showToast("Please check image required to upload image")
                }
            }

        }
        binding.etStartdate.setOnClickListener {
            openCalendar(binding.etStartdate, Calendar.getInstance().timeInMillis)
        }
        binding.etEnddate.setOnClickListener {
            openCalendar(binding.etEnddate, 0)
        }
        binding.etOpentime.setOnClickListener {
            openTimeDialog(binding.etOpentime)
        }
        binding.etEnddtime.setOnClickListener {
            openTimeDialog(binding.etEnddtime)
        }
        binding.etCity.setOnClickListener {
            activity?.let { it1 ->
                val modalBottomSheet = SpinnerBottomSheet.newInstance(
                    Constants.LOCATION,
                    binding.etCity.text.toString(), cityList, false, object :
                        OnItemSelectedListner {
                        override fun onItemSelectedListner(
                            titleData: SpinnerRowModel?,
                            datevalue: String
                        ) {
                            if (titleData != null) {
                                binding.etCity.setText(titleData.title)
                                cityid = titleData.mstCode
                            } else {
                                binding.etCity.setText(datevalue)
                                cityid = ""
                            }
                            val locationslist =
                                viewModel.getLocationWIthFromCities(cityid, true)
//                            if (locationslist.isNullOrEmpty()) {
//                                binding.etEditlocation.VISIBLE()
//                                binding.etLocation.GONE()
//                            } else {
//                                binding.etEditlocation.GONE()
//                                binding.etLocation.VISIBLE()
//                            }
                        }

                        override fun onItemmultipleSelectedListner(
                            titleData: ArrayList<SpinnerRowModel>?,
                            value: ArrayList<SpinnerRowModel>
                        ) {

                        }
                    }, headerlbl = "City"
                )
                modalBottomSheet.show(it1.supportFragmentManager, SpinnerBottomSheet.TAG)
            }
        }
        binding.etLocation.setOnClickListener {
            val locationslist =
                viewModel.getLocationWIthFromCities(cityid, true)
            activity?.let { it1 ->
                val modalBottomSheet = SpinnerBottomSheet.newInstance(
                    Constants.LOCATION,
                    binding.etLocation.text.toString(),
                    locationslist,
                    false,
                    object :
                        OnItemSelectedListner {
                        override fun onItemSelectedListner(
                            titleData: SpinnerRowModel?,
                            datevalue: String
                        ) {
                            if (titleData != null) {
                                binding.etLocation.setText(titleData.title)
                                locationid = titleData.mstCode
                            } else {
                                binding.etLocation.setText(datevalue)
                            }
                        }

                        override fun onItemmultipleSelectedListner(
                            titleData: ArrayList<SpinnerRowModel>?,
                            value: ArrayList<SpinnerRowModel>
                        ) {

                        }
                    },
                    headerlbl = "Location"
                )
                modalBottomSheet.show(it1.supportFragmentManager, SpinnerBottomSheet.TAG)
            }
        }
    }

    private fun validateFileds(): Boolean {
        if (binding.etCategory.text?.trim().isNullOrEmpty()) {
            showToast("Please Select Service")
            return false
        }
        if (binding.etStorename.text?.trim().isNullOrEmpty()) {
            showToast("Please enter Store name")
            return false
        }
        if (binding.etOffertitle.text?.trim().isNullOrEmpty()) {
            showToast("Please enter Offer title")
            return false
        }
        if (binding.etStartdate.text?.trim().isNullOrEmpty()) {
            showToast("Please select start date")
            return false
        }
        if (binding.etEnddate.text?.trim().isNullOrEmpty()) {
            showToast("Please select End date")
            return false
        }
        if (binding.etEditcity.text?.trim().isNullOrEmpty()) {
            showToast("Please select city")
            return false
        }


        if (binding.etEditlocation.text?.trim().isNullOrEmpty()) {
            showToast("Please enter Location")
            return false
        }


        if (binding.etContactperson.text?.trim().isNullOrEmpty()) {
            showToast("Please enter contact person")
            return false
        }
        if (binding.etMobilenumber.text?.trim().isNullOrEmpty()) {
            showToast("Please enter Mobile Number")
            return false
        }
        if (!binding.etMobilenumber.text?.trim().toString().validateMobilenumber()) {
            showToast("Please enter valid Mobile Number")
            return false
        }
        if (binding.etWhatsappnumber.text?.trim().isNullOrEmpty()) {
            showToast("Please enter whatsapp Number")
            return false
        }

        if (!binding.etWhatsappnumber.text?.trim().toString().validateMobilenumber()) {
            showToast("Please enter valid Whatsapp Number")
            return false
        }
        if (binding.etEmail.text?.trim().isNullOrEmpty()) {
            showToast("Please enter Email")
            return false
        }
        if (!binding.etEmail.text?.trim().toString().isValidEmail()) {
            showToast("Please enter valid email")
            return false
        }

        if (binding.etShowroomaddress.text?.trim().isNullOrEmpty()) {
            showToast("Please enter ShowroomAddress")
            return false
        }

        if (binding.isimageRequired.isChecked) {
            showToast("Please select image")
            return false
        }
        return true
    }


    private fun setObserver() {
        viewModel.masterdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        response?.data?.data?.forEach {
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
                            if (it.MstType == "Service") {
                                categoriesList.add(
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
        viewModel.addpostingResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    viewModel.isloading.set(false)
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            findNavController().navigate(R.id.nav_home)
                        } else {
                            ShowFullToast(response.data?.Message ?: "")
                        }
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                    response.message?.let { ShowFullToast(response?.message ?: "") }
                }

                is NetworkResult.Loading -> {
                    viewModel.isloading.set(true)
                }
            }
        }
    }


    private fun passData(): JsonObject {
        val formDataJson = JsonObject()
        formDataJson.addProperty(
            "ShowroomName",
            binding.etStorename.text.toString()
        )
        formDataJson.addProperty(
            "OfferTitle",
            binding.etOffertitle.text.toString()
        )
        formDataJson.addProperty(
            "OfferDescription",
            binding.etOfferdesc.text.toString()
        )
        formDataJson.addProperty("ImageUrl", "")
        formDataJson.addProperty("ConditionsIfAny", binding.etTandc.text.toString())
        formDataJson.addProperty("StartDate", binding.etStartdate.text.toString())
        formDataJson.addProperty("categoryid", categoryid)
        formDataJson.addProperty("EndDate", binding.etEnddate.text.toString())
        formDataJson.addProperty("CityName", binding.etCity.text.toString())
        if (TextUtils.isEmpty(cityid)) {
            formDataJson.addProperty("CityId", "0")
        } else {
            formDataJson.addProperty("CityId", cityid)
        }
        formDataJson.addProperty("AreaId", locationid)
        if (binding.etLocation.isVisible) {
            formDataJson.addProperty("AreaName", binding.etLocation.text.toString())
            formDataJson.addProperty("AreaId", locationid)
        }
        if (binding.etEditlocation.isVisible) {
            formDataJson.addProperty("AreaName", binding.etEditlocation.text.toString())
            formDataJson.addProperty("AreaId", "0")
        }

        formDataJson.addProperty("GoogleLocation", binding.etMaplocation.text.toString())
        formDataJson.addProperty("ContactPersonName", binding.etContactperson.text.toString())
        formDataJson.addProperty("MobileNo", binding.etMobilenumber.text.toString())
        formDataJson.addProperty("WhatsAppNo", binding.etWhatsappnumber.text.toString())
        formDataJson.addProperty("ShowroomAddress", binding.etShowroomaddress.text.toString())
        formDataJson.addProperty("EmailId", binding.etEmail.text.toString())
        formDataJson.addProperty("WebSite", binding.etWebsite.text.toString())
        formDataJson.addProperty("OpenTime", binding.etOpentime.text.toString())
        formDataJson.addProperty("CloseTime", binding.etEnddtime.text.toString())
        var imageRequired = "N"
        if (binding.isimageRequired.isChecked) {
            imageRequired = "Y"
        }
        formDataJson.addProperty("VideoLink", binding.etVideo.text.toString())
        formDataJson.addProperty("DisplayImage", imageRequired)
        formDataJson.addProperty("createdBy", AppPreference.read(Constants.USERUID, "") ?: "0")
        formDataJson.addProperty("createdDateTime", getDateTime())
        formDataJson.addProperty("updatedBy", AppPreference.read(Constants.USERUID, "") ?: "0")
        formDataJson.addProperty("updatedDateTime", getDateTime())
        return formDataJson
    }

    private fun triggerCameraOrGallerySelection() {
        activity?.let { it1 ->
            val chooseActionBottomSheet = ActionBottomSheet.newInstance(
                false, true,
                object : ActionBottomSheet.OnFileSelectedListener {
                    override fun onFileSelected(
                        uri: Uri?,
                        value: String,
                        flag: String,
                        file: File?
                    ) {
                        if (uri != null) {
                            val bundle = Bundle()
                            bundle.putString("ADDPOST", passData().toString())
                            bundle.putString(Constants.BUNDLE_IMG_PATH, uri.toString())
                            findNavController().navigate(R.id.nav_manage_post, bundle)
                        }
                    }

                })
            chooseActionBottomSheet.show(
                it1.supportFragmentManager, ActionBottomSheet.TAG
            )
        }
    }

    private fun openCalendar(etDob: TextInputEditText, timeInMillis: Long) {
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
        }, Constants.YYY_HIFUN_MM_DD, timeInMillis)

    }

    private fun openTimeDialog(text: TextInputEditText) {
        activity?.let { it1 ->
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                val formattedHour = String.format("%02d", hour)
                text.setText("$formattedHour:$minute:00")
            }
            val timePickerDialog = TimePickerDialog(
                context,
                timeSetListener,
                14, // default hour
                30, // default minute
                true // 24-hour format
            )
            timePickerDialog.show()
        }
    }

    fun handleBack() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("")
        builder.setMessage("Are you sure you want to exit?")
        builder.setPositiveButton("Yes") { dialog: DialogInterface, which: Int ->
            findNavController().navigate(R.id.nav_home)
        }
        builder.setNegativeButton("No") { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

      fun checkPermissions() {
        try {
            val hasPermissionLocation = ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermissionLocation) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    11
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}