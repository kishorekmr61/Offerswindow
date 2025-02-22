package com.customer.offerswindow.ui.onboarding.ReferenceMember

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.PostNewEnquiry
import com.customer.offerswindow.model.stock.StockPurchsasePostingResponse
import com.customer.offerswindow.repositry.CustomerListRepository
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReferencememberViewModel @Inject constructor(
    private val customerListRepository: CustomerListRepository,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {
    var isloading = ObservableField(false)
    var signUpResponse = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()

    fun postSignUp(postNewEnquiry: PostNewEnquiry) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                customerListRepository.postSignUp(postNewEnquiry).collect { values ->
                    signUpResponse.postValue(values)
                }
            } else {
                com.customer.offerswindow.utils.showToast("No Internet")
            }
        }
    }
}