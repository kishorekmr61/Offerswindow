package com.customer.offerswindow.ui.customerprofile

import android.app.Application
import android.util.Patterns
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.CustomerData
import com.customer.offerswindow.model.CustomerListResponse
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.model.customersdata.ProfileUpdateRequest
import com.customer.offerswindow.model.customersdata.ProfileUpdateResponse
import com.customer.offerswindow.model.masters.CommonLocationMasterResponse
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

    var customerinfo = MutableLiveData<NetworkResult<CustomerListResponse>>()
    val registrationData = MutableLiveData(ProfileUpdateRequest())
    var masterdata = MutableLiveData<NetworkResult<CommonMasterResponse>>()
    var deleteResponse = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()
    var isloading = ObservableField(false)
    var customerdata = MutableLiveData<NetworkResult<CustomerListResponse>>()
    var locationsmasterdata = MutableLiveData<NetworkResult<CommonLocationMasterResponse>>()



    fun bindCustomerData(customerData: CustomerData) {
        try {
            registrationData.value?.CustomerUID = customerData.User_UID
            registrationData.value?.CustomerCategory = customerData.Cust_Category
            registrationData.value?.CustomerName = customerData.Cust_Name ?: ""
            registrationData.value?.SurName = customerData.Sur_Name ?: ""
            registrationData.value?.Mobile_No = customerData.Mobile_No ?: ""
            registrationData.value?.CustomerHeight = customerData.Height_CM
            registrationData.value?.FitnessGoal = customerData.Fitness_Goal
            registrationData.value?.CustomerWeight = customerData.Initial_Weight
            registrationData.value?.CustomerLocation = customerData.Location
            registrationData.value?.CoachUserID = customerData.Coach_User_UID
            registrationData.value?.CustomerImageUrl = customerData.Cust_Image_Path
            registrationData.value?.CoachUserID = customerData.Coach_User_UID
            registrationData.value?.Coachname = customerData.Coach_Name
            registrationData.value?.CoachMobileno = customerData.Coach_Mobile_No
            registrationData.value?.Gender = customerData.Gender
            registrationData.value?.EmailID = customerData.Email_ID
            registrationData.value?.DoB = customerData.DOB ?: ""
            registrationData.value?.MaritalStatus = customerData.Marital_Status ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    fun getGenderinfo(gender: String): String {
        return if (gender == "F") {
            "Female"
        } else if (gender == "M")
            "Male"
        else if (gender.equals("T"))
            "TransGender"
        else
            gender
    }

    fun getMaritalinfo(mstatus: String): String {
        return if (mstatus == "M") {
            "Married"
        } else if (mstatus == "S")
            "Single"
        else if (mstatus.equals("D"))
            "Divorced"
        else
            mstatus
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun updateProfileData(photoPart: MultipartBody.Part?, formDataBody: RequestBody) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                customerrepository.submitProfileUpdateData(photoPart, formDataBody)
                    .collect { values ->
                        customersdata.postValue(values)
                        app.showToast(values.data?.Message ?: values.message ?: "")
                    }
            } else {
                app.showToast("No Internet")
            }
        }

    }

    fun getCustomerData(userid: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                customerrepository.getCustomerData(userid).collect { values ->
                    if (values.data?.Status == 200) {
                        customerdata.postValue(values)
                        getDashboardData(userid)
                    }
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun getStatusMstData() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getCommonbMaster("Common").collect { values ->
                    masterdata.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

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

    fun getLocationsMst() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getCommonLocationMaster("Locations").collect { values ->
                    locationsmasterdata.postValue(values)
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
                }

            } else {
                app.showToast("No Internet")
            }
        }
    }

}