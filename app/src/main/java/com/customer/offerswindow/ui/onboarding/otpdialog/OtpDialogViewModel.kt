package com.customer.offerswindow.ui.wallet.otpdialog

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.repositry.CustomerListRepository
import com.customer.offerswindow.R
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.PostPhoneNumber
import com.customer.offerswindow.model.stock.StockPurchsasePostingResponse
import com.customer.offerswindow.repositry.CustomerListRepository
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpDialogViewModel @Inject constructor(
    var app: Application,
    private val customerListRepository: CustomerListRepository,
    private var networkHelper: NetworkHelper,
) : ViewModel() {

    var isloading = ObservableField(false)
    var image = ObservableField(R.drawable.ic_pending)
    var otpResponse = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()
    var verifyResponse = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()

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
                com.customer.offerswindow.utils.showToast("No Internet")
            }
        }
    }


    fun postVerifyOtp(mobileno: String, OTP: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                var postphonenumber = PostPhoneNumber(mobileno)
                customerListRepository.postOTPVerificationData(mobileno, OTP).collect { values ->

                    verifyResponse.postValue(values)

                }
            } else {
                com.customer.offerswindow.utils.showToast("No Internet")
            }
        }
    }

    fun getWalletTransferStatusinfo(
        screenname: String,
        Status: String,
        createdby: String,
        craeteddatetime: String
    ) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {

            } else {
                com.customer.offerswindow.utils.showToast("No Internet")
            }
        }
    }


}