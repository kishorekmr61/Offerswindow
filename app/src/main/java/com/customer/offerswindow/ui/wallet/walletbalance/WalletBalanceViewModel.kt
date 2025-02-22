package com.customer.offerswindow.ui.wallet.walletbalance

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.wallet.Table1
import com.customer.offerswindow.model.wallet.WalletHistoryResponse
import com.customer.offerswindow.repositry.WalletBalanceRepositry
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletBalanceViewModel @Inject constructor(
    private val repository: WalletBalanceRepositry,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {

    var walletbalancedata = MutableLiveData<NetworkResult<WalletHistoryResponse>>()
    val walletdata = MutableLiveData<PagingData<Table1>>()
    var isloading = ObservableField(false)
    var walletbalance = ObservableField("0.0")
    var nodata = ObservableField(false)

    fun getWalletHistoryData() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getWalletHistoryData().cachedIn(viewModelScope).collect { values ->
                    walletdata.postValue(values)
                }
            } else {
                com.customer.offerswindow.utils.showToast("No Internet")
            }
        }
    }

    fun getWalletBalanceData() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getWalletHistoryData(AppPreference.read(Constants.USERUID, "") ?: "0", 0)
                    .collect { values ->
                        walletbalancedata.postValue(values)
                    }
            } else {
                com.customer.offerswindow.utils.showToast("No Internet")
            }
        }
    }

}