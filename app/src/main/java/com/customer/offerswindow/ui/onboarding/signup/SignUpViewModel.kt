package com.customer.offerswindow.ui.onboarding.signup

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.OfferWindowCommonResponse
import com.customer.offerswindow.model.PostOfferWindowCommonResponse
import com.customer.offerswindow.model.customersdata.PostSignUp
import com.customer.offerswindow.model.customersdata.UserSigUp
import com.customer.offerswindow.repositry.CustomerListRepository
import com.customer.offerswindow.repositry.Repository
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val customerListRepository: CustomerListRepository,
    private val repository: Repository,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {
    var isloading = ObservableField(false)
    var isenable = ObservableField(true)
    var signUpResponse = MutableLiveData<NetworkResult<PostOfferWindowCommonResponse>>()
    var signupOTPResponse = MutableLiveData<NetworkResult<OfferWindowCommonResponse>>()

    fun postSignUp(postSignUp: PostSignUp) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                customerListRepository.postSignUp(postSignUp).collect { values ->
                    signUpResponse.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun getSignupOtp(usersignup: UserSigUp) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.postSignupOtp(usersignup).collect { values ->
                    signupOTPResponse.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

}