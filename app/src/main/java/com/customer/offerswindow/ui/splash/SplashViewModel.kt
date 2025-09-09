package com.customer.offerswindow.ui.splash

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.CustomerDataResponse
import com.customer.offerswindow.model.TokenResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.repositry.Repository
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: Repository, private val dashBoardRepositry: DashBoardRepositry,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {

    var masterdata = MutableLiveData<NetworkResult<CommonMasterResponse>>()
    var customerinfo = MutableLiveData<NetworkResult<CustomerDataResponse>>()
    var tokenResponse = MutableLiveData<NetworkResult<TokenResponse>>()
    var isloading = ObservableField(false)


    fun getToken(
        mobilenumber: String,
        password: String
    ) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getToken(mobilenumber, password).collect { values ->
                    AppPreference.write(Constants.TOKEN, values.data?.access_token ?: "")
                    AppPreference.write(Constants.TOKENTIMER, values.data?.expires_in ?: "")
                    tokenResponse.postValue(values)
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