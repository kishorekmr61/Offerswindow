package com.customer.offerswindow.ui.wallet.rewardPointHistory.redemption

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.OfferWindowCommonResponse
import com.customer.offerswindow.model.PostOfferWindowCommonResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.model.wallet.RedemptionRequestBody
import com.customer.offerswindow.repositry.Repository
import com.customer.offerswindow.repositry.WalletBalanceRepositry
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RedemptionViewModel @Inject constructor(
    var application: Application,
    private val repository: Repository,
    private val walletrepository: WalletBalanceRepositry,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {

    var isloading = ObservableField(false)
    var rewardsPostingResponse = MutableLiveData<NetworkResult<OfferWindowCommonResponse>>()
    var walletbalance = ObservableField("0")
    var masterdata = MutableLiveData<NetworkResult<CommonMasterResponse>>()

    fun postRedemption(redemptionRequestBody: RedemptionRequestBody) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                walletrepository.postRedemptionRequestData(redemptionRequestBody)
                    .collect { values ->
                        rewardsPostingResponse.postValue(values)
                    }

            } else {
                app.showToast("No Internet")
            }
        }
    }
    fun getMstData() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getCommonMaster("Common","0","0").collect { values ->
                    masterdata.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }
}