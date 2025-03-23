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
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.model.dashboard.ProfileUpdateRequest
import com.customer.offerswindow.model.dashboard.ProfileUpdateResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.repositry.CustomerListRepository
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.repositry.Repository
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
    var customersdatapost = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()
    var masterdata = MutableLiveData<NetworkResult<CommonMasterResponse>>()
    var customerinfo = MutableLiveData<NetworkResult<CustomerDataResponse>>()
    val registrationData = MutableLiveData(ProfileUpdateRequest())
    var deleteResponse = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()
    var isloading = ObservableField(false)
    var customerdata = MutableLiveData<NetworkResult<CustomerDataResponse>>()


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
                repository.getCommonMaster("Common").collect { values ->
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

    fun updateProfileData(profileUpdateRequest: ProfileUpdateRequest) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.submitProfileUpdateData(profileUpdateRequest).collect { values ->
                    customersdatapost.postValue(values)
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

    fun bindCustomerData(customerData: CustomerData) {
        registrationData.value?.CustomerId = customerData.Cust_UID
        registrationData.value?.CustomerName = customerData.Cust_Name ?: ""
        registrationData.value?.LastName = customerData
            .Cust_Last_Name ?: ""
        registrationData.value?.PhoneNo = customerData.Mobile_No
//        registrationData.value?.DoB =
//            convertDate(customerData.dob, Constants.YYYYMMDDTHH, Constants.DDMMMYYYY)
        registrationData.value?.EmailID = customerData.Email_ID
        registrationData.value?.CustomerImageUrl = customerData.Cust_Image_URL


    }
}