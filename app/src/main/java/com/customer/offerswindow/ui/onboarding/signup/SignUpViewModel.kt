package com.customer.offerswindow.ui.onboarding.signup

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.model.customersdata.PostSignUp
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

}