package com.customer.offerswindow.ui.wallet.redemptionApproveDialog

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.stock.StockPurchsasePostingResponse
import com.customer.offerswindow.model.wallet.RedemptionRequestBody
import com.customer.offerswindow.repositry.WalletBalanceRepositry
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RedemptionApprovalViewModel @Inject constructor(
    var app: Application,
    private val walletBalanceRepositry: WalletBalanceRepositry,
    private var networkHelper: NetworkHelper
) : ViewModel() {

    var amount = ObservableField("0")
    var name = ObservableField("")
    var isloading = ObservableField(false)
    var postingResponse = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()




    fun postWalletApproval(redemptionRequestBody: RedemptionRequestBody) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                walletBalanceRepositry.postRedemptionRequestData(redemptionRequestBody).collect { values ->
                    if (values.data?.Status == 200) {
                        postingResponse.postValue(values)
                    } else
                        com.customer.offerswindow.utils.showToast(values.message ?: "")
                }
            } else {
                com.customer.offerswindow.utils.showToast("No Internet")
            }
        }
    }


}