package com.customer.offerswindow.ui.wallet.wallettransfersApproval

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.leads.PostFollowUpResponse
import com.customer.offerswindow.model.wallet.PostWalletTransfersApprovals
import com.customer.offerswindow.model.wallet.WalletPendingData
import com.customer.offerswindow.repositry.WalletBalanceRepositry
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletTransfersApprovalViewModel @Inject constructor(
    var app: Application,
    private val repository: WalletBalanceRepositry,
    private var networkHelper: NetworkHelper,
) : ViewModel() {
    var isloading = ObservableField(false)
    var nodata = ObservableField(false)
     val walletpendingrequest = MutableLiveData<PagingData<WalletPendingData>>()
    var postingResponse = MutableLiveData<NetworkResult<PostFollowUpResponse>>()

    fun getWalletPendingData() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getWalletApprovalData().collect { values ->
                    walletpendingrequest.postValue(values)
                }
            } else {
                com.customer.offerswindow.utils.showToast("No Internet")
            }
        }
    }

    fun postWalletApprovalData(postWalletTransfersApprovals: PostWalletTransfersApprovals) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.postWalletTransferApprovalData(postWalletTransfersApprovals).collect { values ->
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