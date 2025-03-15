package com.customer.offerswindow.ui.onboarding.signIn

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.OTPResponse
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.model.TokenResponse
import com.customer.offerswindow.model.UserResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.repositry.Repository
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: Repository,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {

    var userResponse = MutableLiveData<NetworkResult<UserResponse>>()
    var OtpResponse = MutableLiveData<NetworkResult<OTPResponse>>()
    var tokenResponse = MutableLiveData<NetworkResult<TokenResponse>>()
    var forgotpasswordResponse = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()
    var isloading = ObservableField(false)
    var masterdata = MutableLiveData<NetworkResult<CommonMasterResponse>>()


    fun getToken(mobilenumber: String,
                 password: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getToken(mobilenumber,
                    password).collect { values ->
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


    fun getOTP(mobileno: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getOtp(mobileno).collect { values ->
                    userResponse.postValue(values)
//                    response.value?.data = values.data
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun validateOTP(mobileno: String, OTP: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.validateOTP(mobileno, OTP).collect { values ->
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
}