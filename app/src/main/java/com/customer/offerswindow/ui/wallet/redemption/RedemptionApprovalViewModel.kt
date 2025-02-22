package com.customer.offerswindow.ui.wallet.redemption

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.wallet.RedemptionApprovalResponse
import com.customer.offerswindow.repositry.WalletBalanceRepositry
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RedemptionApprovalViewModel @Inject constructor(
    private val repository: WalletBalanceRepositry,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {
    var redemptionApprovalResponse = MutableLiveData<NetworkResult<RedemptionApprovalResponse>>()
    var isloading = ObservableField(false)

    fun getRedemptionApprovalData(userid: String, imaxid: Int) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getRedemptionApprovalData(userid, imaxid).collect { values ->
                    redemptionApprovalResponse.postValue(values)
                }
            } else {
                com.customer.offerswindow.utils.showToast("No Internet")
            }
        }
    }
}