package com.customer.offerswindow.ui.wallet.rewardPointHistory.redemption

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.model.wallet.RedemptionRequestBody
import com.customer.offerswindow.repositry.WalletBalanceRepositry
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RedemptionViewModel @Inject constructor(
    var application: Application,
    private val repository: WalletBalanceRepositry,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {

    var isloading = ObservableField(false)
    var rewardsPostingResponse = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()
    var walletbalance = ObservableField("0")


    fun postRedemption(redemptionRequestBody: RedemptionRequestBody) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.postRedemptionRequestData(redemptionRequestBody)
                    .collect { values ->
                        rewardsPostingResponse.postValue(values)
                    }

            } else {
                app.showToast("No Internet")
            }
        }
    }
}