package com.customer.offerswindow.ui.onboarding.signIn

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.CustomerDataResponse
import com.customer.offerswindow.model.OTPResponse
import com.customer.offerswindow.model.OfferWindowCommonResponse
import com.customer.offerswindow.model.TokenResponse
import com.customer.offerswindow.model.UserResponse
import com.customer.offerswindow.model.customersdata.UserSigUp
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.repositry.Repository
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: Repository, private val dashBoardRepositry: DashBoardRepositry,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {

    var userResponse = MutableLiveData<NetworkResult<UserResponse>>()
    var OtpResponse = MutableLiveData<NetworkResult<OTPResponse>>()
    var tokenResponse = MutableLiveData<NetworkResult<TokenResponse>>()
    var customerinfo = MutableLiveData<NetworkResult<CustomerDataResponse>>()
    var forgotpasswordResponse = MutableLiveData<NetworkResult<OfferWindowCommonResponse>>()
    var isloading = ObservableField(false)
    var masterdata = MutableLiveData<NetworkResult<CommonMasterResponse>>()


    fun getToken(
        mobilenumber: String,
        password: String
    ) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getToken(
                    mobilenumber,
                    password
                ).collect { values ->
                    AppPreference.write(Constants.TOKEN, values.data?.access_token ?: "")
                    AppPreference.write(Constants.TOKENTIMER, values.data?.expires_in ?: "")
                    tokenResponse.postValue(values)
//                    response.value?.data = values.data
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }




    fun validateOTP(mobileno: String, OTP: String, sPinNo: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.validateOTP(mobileno, OTP, sPinNo).collect { values ->
                    OtpResponse.postValue(values)
//                    response.value?.data = values.data
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun forgotPassword(mobileno: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.forgotPassword(mobileno).collect { values ->
                    forgotpasswordResponse.postValue(values)
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

    fun getUserInfo(mobileno: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                dashBoardRepositry.getCustomerData(mobileno).collect { values ->
                    customerinfo.postValue(values)
                }

            } else {
                app.showToast("No Internet")
            }
        }
    }
}