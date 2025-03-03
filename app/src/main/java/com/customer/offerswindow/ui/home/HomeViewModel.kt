package com.customer.offerswindow.ui.home

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.CustomerListResponse
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.repositry.CustomerListRepository
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.repositry.Repository
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dashBoardRepositry: DashBoardRepositry,
    private val repository: Repository,
    private val customerListRepository: CustomerListRepository,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {
    var isloading = ObservableField(false)
    var customerinfo = MutableLiveData<NetworkResult<CustomerListResponse>>()
    var masterdata = MutableLiveData<NetworkResult<CommonMasterResponse>>()

    fun getDashboardData(userid: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                dashBoardRepositry.getCustomerData(userid).collect { values ->
                    customerinfo.postValue(values)
                }

            } else {
//                showToast("No Internet")
            }
        }
    }

    fun getMstData() {
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
}
