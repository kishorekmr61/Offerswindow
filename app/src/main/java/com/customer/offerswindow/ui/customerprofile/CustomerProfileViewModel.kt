package com.customer.offerswindow.ui.customerprofile

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.CustomerData
import com.customer.offerswindow.model.CustomerDataResponse
import com.customer.offerswindow.model.SpinnerRowModel
import com.customer.offerswindow.model.OfferWindowCommonResponse
import com.customer.offerswindow.model.dashboard.ProfileUpdateRequest
import com.customer.offerswindow.model.dashboard.ProfileUpdateResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.repositry.CustomerListRepository
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.repositry.Repository
import com.customer.offerswindow.utils.convertDate
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class CustomerProfileViewModel @Inject constructor(
    private val Dashboardrepository: DashBoardRepositry,
    private val customerrepository: CustomerListRepository,
    private val repository: Repository,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {
    var customersdata = MutableLiveData<NetworkResult<ProfileUpdateResponse>>()
    var customersdatapost = MutableLiveData<NetworkResult<OfferWindowCommonResponse>>()
    var masterdata = MutableLiveData<NetworkResult<CommonMasterResponse>>()
    var customerinfo = MutableLiveData<NetworkResult<CustomerDataResponse>>()
    val registrationData = MutableLiveData(ProfileUpdateRequest())
    var deleteResponse = MutableLiveData<NetworkResult<OfferWindowCommonResponse>>()
    var isloading = ObservableField(false)
    var customerdata = MutableLiveData<NetworkResult<CustomerDataResponse>>()
    var profilePic = ObservableField<String>()


    fun deleteUserAccount() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.deleteUserAccount(AppPreference.read(Constants.USERUID, "") ?: "")
                    .collect { values ->
                        deleteResponse.postValue(values)
                    }
            } else {
                app.showToast("No Internet")
            }
        }
    }


    fun getDashboardData(userid: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                Dashboardrepository.getCustomerData(userid).collect { values ->
                    customerinfo.postValue(values)
                    customerinfo.value?.data?.Data?.firstOrNull()?.let { bindCustomerData(it) }
                }

            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun getMstData() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getCommonMaster("Common","0").collect { values ->
                    masterdata.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun updateProfileData(photoPart: MultipartBody.Part?, formDataBody: RequestBody) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.submitProfileUpdateData(photoPart, formDataBody).collect { values ->
                    customersdata.postValue(values)
                    values.data?.Data?.ID?.let {
                        app.showToast("success")
                    } ?: kotlin.run {
//                        app.showToast("failure")
                    }
                }
            } else {
                app.showToast("No Internet")
            }
        }

    }

//    fun updateProfileData(profileUpdateRequest: ProfileUpdateRequest) {
//        viewModelScope.launch {
//            if (networkHelper.isNetworkConnected()) {
//                repository.submitProfileUpdateData(profileUpdateRequest).collect { values ->
//                    customersdatapost.postValue(values)
//                    values.data?.Data?.ID?.let {
//                        app.showToast("success")
//                    } ?: kotlin.run {
////                        app.showToast("failure")
//                    }
//                }
//            } else {
//                app.showToast("No Internet")
//            }
//        }
//
//    }

    fun bindCustomerData(customerData: CustomerData) {
//        registrationData.value?.CustomerId = customerData.Cust_UID
        registrationData.value?.CustomerName = customerData.Cust_Name ?: ""
        registrationData.value?.LastName = customerData
            .Cust_Last_Name ?: ""
        registrationData.value?.PhoneNo = customerData.Mobile_No
        registrationData.value?.DoB =
            convertDate(customerData.DOB ?: "", Constants.YYYYMMDDTHH, Constants.DDMMMYYYY)
        registrationData.value?.EmailID = customerData.Email_ID
        registrationData.value?.Location_Desc = customerData.Sub_Location_Desc ?: ""
        registrationData.value?.CustomerImageUrl = customerData.Cust_Image_URL ?: ""
        registrationData.value?.PinCode = customerData.Pin_Code ?: ""
        registrationData.value?.Country = customerData.Country ?: ""
        registrationData.value?.Country_Desc = customerData.Country_Desc ?: ""
        registrationData.value?.Pin_No = customerData.Pin_No ?: ""
        profilePic.set(customerData.Cust_Image_URL)
//        registrationData.value?.CustomerImageUrl = customerData.Cust_Image_URL


    }

    fun getLocationWIthFromCities(citycode: String): ArrayList<SpinnerRowModel> {
        var locationList = arrayListOf<SpinnerRowModel>()
        locationList.add(SpinnerRowModel("All", false, false, mstCode = "0"))
        masterdata.value?.data?.data?.forEach {
            if (it.MstType == "Location" && citycode == "0") {
                locationList.add(
                    SpinnerRowModel(
                        it.MstDesc,
                        false,
                        false,
                        mstCode = it.MstCode
                    )
                )
            } else if (it.MstType == "Location" && it.City_Code == citycode) {
                locationList.add(
                    SpinnerRowModel(
                        it.MstDesc,
                        false,
                        false,
                        mstCode = it.MstCode
                    )
                )
            }
        }

        return locationList
    }

}