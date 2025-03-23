package com.customer.offerswindow.ui.mybookings

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.dashboard.BookingsResponse
import com.customer.offerswindow.model.dashboard.SlotsDataResponse
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyBookingsViewModel @Inject constructor(
    private val dashBoardRepositry: DashBoardRepositry,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {
    var nodata = ObservableField<Boolean>()
    var isloading = ObservableField(false)
    var bookingsList = MutableLiveData<NetworkResult<BookingsResponse>>()

    fun getBookingsData(lCustomerID: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                dashBoardRepositry.getBookings(lCustomerID)
                    .collect { values ->
                        bookingsList.postValue(values)
                    }
            } else {
                app.showToast("No Internet")
            }
        }
    }
    fun getOffersData(lCustomerID: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                dashBoardRepositry.getOffersData(lCustomerID)
                    .collect { values ->
                        bookingsList.postValue(values)
                    }
            } else {
                app.showToast("No Internet")
            }
        }
    }
}