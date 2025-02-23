package com.customer.offerswindow.ui.onboarding.signup

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.PostNewEnquiry
import com.customer.offerswindow.model.PostPhoneNumber
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.repositry.CustomerListRepository
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val customerListRepository: CustomerListRepository,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {
    var isloading = ObservableField(false)
    var signUpResponse = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()
    var otpResponse = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()

    fun postSignUp(postNewEnquiry: PostNewEnquiry) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                customerListRepository.postSignUp(postNewEnquiry).collect { values ->
                    signUpResponse.postValue(values)
                }
            } else {
                 showToast("No Internet")
            }
        }
    }
    fun postOtp(mobileno: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                var postphonenumber = PostPhoneNumber(mobileno)
                customerListRepository.postOTPData(postphonenumber).collect { values ->
//                    if (values.data?.Status == 200) {
//                        otpResponse.postValue(values)
//                    } else
//                        otpResponse.postValue(values.message ?: "")
                    otpResponse.postValue(values)
                }
            } else {
                showToast("No Internet")
            }
        }
    }
}