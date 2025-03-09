package com.customer.offerswindow.ui.home

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.CustomerDataResponse
import com.customer.offerswindow.model.dashboard.DashBoardDataResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
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
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {
    var isloading = ObservableField(false)
    var customerinfo = MutableLiveData<NetworkResult<CustomerDataResponse>>()
    var dashboardresponse = MutableLiveData<NetworkResult<DashBoardDataResponse>>()
    var masterdata = MutableLiveData<NetworkResult<CommonMasterResponse>>()
    var profilepic = ObservableField<String>()
    var username = ObservableField<String>()

    fun getDashboardData(lShowroomId: String, lLocationId: String, lServiceId: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                dashBoardRepositry.getDashBoardOffersList(lShowroomId, lLocationId, lServiceId)
                    .collect { values ->
                        dashboardresponse.postValue(values)
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
                    username.set(values.data?.Data?.firstOrNull()?.Cust_Name)
                    customerinfo.postValue(values)
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
