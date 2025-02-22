package com.customer.offerswindow.ui.wallet.wallettransfer

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.wallet.WalletHistoryResponse
import com.customer.offerswindow.repositry.WalletBalanceRepositry
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletTransferViewModel @Inject constructor(
    var app: Application,
    private val repository: WalletBalanceRepositry,
    private var networkHelper: NetworkHelper,
) : ViewModel() {

    var name = ObservableField("")
    var email = ObservableField("")
    var walletbalacne = ObservableField("0.0")
    var walletBalancedata = MutableLiveData<NetworkResult<WalletHistoryResponse>>()
    var isloading = ObservableField(false)


    fun getWalletBalanceData(userid: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getWalletHistoryData(userid,0).collect { values ->
                    walletBalancedata.postValue(values)
                    if (values.data?.Status == 200) {
                        walletbalacne.set(values.data?.Data?.Table?.firstOrNull()?.Wallet_Balance.toString())
                    }
                }
            } else {
                com.customer.offerswindow.utils.showToast("No Internet")
            }
        }
    }

}