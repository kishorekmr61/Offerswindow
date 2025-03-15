package com.customer.offerswindow.ui.notifications

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.notification.NotificationResponse
import com.customer.offerswindow.repositry.Repository
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val dashBoardRepositry: Repository,
    private var networkHelper: NetworkHelper,
    var app: Application
) : ViewModel() {

    var nodata = ObservableField<Boolean>()

    var isloading = ObservableField(false)
    var notificationinfo = MutableLiveData<NetworkResult<NotificationResponse>>()

    fun getUserInfo(lCustomerID: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                dashBoardRepositry.getNotifications(lCustomerID).collect { values ->
                    notificationinfo.postValue(values)
                }

            } else {
                app.showToast("No Internet")
            }
        }
    }
}