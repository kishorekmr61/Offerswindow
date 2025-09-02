package com.customer.offerswindow.ui.detailview

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.OfferWindowCommonResponse
import com.customer.offerswindow.model.PostOfferWindowCommonResponse
import com.customer.offerswindow.model.customersdata.PostOfferBooking
import com.customer.offerswindow.model.offerdetails.OfferDeatils
import com.customer.offerswindow.model.offerdetails.OfferDeatilsResponse
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.repositry.Repository
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewViewModel @Inject constructor(
    private val dashBoardRepositry: DashBoardRepositry,
    private val repository: Repository,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {


    var isloading = ObservableField(false)
    var imagepath = ObservableField<String>()
    var deatiledresponse = MutableLiveData<NetworkResult<OfferDeatilsResponse>>()
    var offerPostingResponse = MutableLiveData<NetworkResult<PostOfferWindowCommonResponse>>()
    var OfferDeatils = ObservableField<OfferDeatils>()

    fun getDetailData(lRecordId: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                dashBoardRepositry.getIndividualOfferDetails(lRecordId).collect { values ->
                        deatiledresponse.postValue(values)
                    }

            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun postOfferBooking(postOfferBooking: PostOfferBooking) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                dashBoardRepositry.postOfferBooking(postOfferBooking)
                    .collect { values ->
                        offerPostingResponse.postValue(values)
                    }

            } else {
                app.showToast("No Internet")
            }
        }
    }
}