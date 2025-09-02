package com.customer.offerswindow.ui.slotbooking

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.OfferWindowCommonResponse
import com.customer.offerswindow.model.PostOfferWindowCommonResponse
import com.customer.offerswindow.model.customersdata.PostSlotBooking
import com.customer.offerswindow.model.dashboard.SlotsDataResponse
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectSlotViewModel @Inject constructor(
    private val dashBoardRepositry: DashBoardRepositry,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {
    var location = ObservableField<String>()
    var isloading = ObservableField(false)
    var slotsDataResponse = MutableLiveData<NetworkResult<SlotsDataResponse>>()
    var slotPostingResponse = MutableLiveData<NetworkResult<PostOfferWindowCommonResponse>>()

    fun getSlotsData(lShowroomId: String, lLocationId: String, lServiceId: String, date: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                dashBoardRepositry.getofferTimeSlots(lShowroomId, lLocationId, lServiceId, date)
                    .collect { values ->
                        slotsDataResponse.postValue(values)
                    }

            } else {
                app.showToast("No Internet")
            }
        }
    }
    fun postSlotBooking(postSlotBooking: PostSlotBooking) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                dashBoardRepositry.postSlotBooking(postSlotBooking)
                    .collect { values ->
                        slotPostingResponse.postValue(values)
                    }

            } else {
                app.showToast("No Internet")
            }
        }
    }
}