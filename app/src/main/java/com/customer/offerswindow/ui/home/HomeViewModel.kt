package com.customer.offerswindow.ui.home

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.CustomerListResponse
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.repositry.CustomerListRepository
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DashBoardRepositry,
    private val customerListRepository: CustomerListRepository,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {
    var isloading = ObservableField(false)
    var userbmi = ObservableField(0.0)
    var name = ObservableField("")
    var customerinfo = MutableLiveData<NetworkResult<CustomerListResponse>>()
    var postweightResponse = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()

    fun getDashboardData(userid: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getCustomerData(userid).collect { values ->
                    customerinfo.postValue(values)
                }

            } else {
//                showToast("No Internet")
            }
        }
    }


}
