package com.customer.offerswindow.ui.wallet.rewardPointHistory

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.wallet.RewardBalanceResponse
import com.customer.offerswindow.model.wallet.RewardPointsHistoryResponse
import com.customer.offerswindow.repositry.WalletBalanceRepositry
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardPointHistoryViewModel @Inject constructor(
    var application: Application,
    private val repository: WalletBalanceRepositry,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {
    var rewardsHistorydata = MutableLiveData<NetworkResult<RewardPointsHistoryResponse>>()
    var rewardBalance = MutableLiveData<NetworkResult<RewardBalanceResponse>>()
    var isloading = ObservableField(false)
    var walletbalance = ObservableField("0")

    fun getRewardsHistoryData(userid: String, imaxid: Int) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getRewardsHistoryData(userid, imaxid).collect { values ->
                    rewardsHistorydata.postValue(values)
                }
            } else {
//              showToast("No Internet")
            }
        }
    }

    fun getRewardsBalanceData(userid: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getRewardsBalanceData(userid).collect { values ->
                    rewardBalance.postValue(values)
                }
            } else {
//               showToast("No Internet")
            }
        }
    }
}