package com.customer.offerswindow.ui.adddpost

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.SpinnerRowModel
import com.customer.offerswindow.model.dashboard.ProfileUpdateResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.repositry.Repository
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(
    private val dashboardrepository: DashBoardRepositry,
    private val repository: Repository,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {
    var addpostingResponse = MutableLiveData<NetworkResult<ProfileUpdateResponse>>()
    var isloading = ObservableField(false)
    var isImgCaptured = ObservableField(false)
    var masterdata = MutableLiveData<NetworkResult<CommonMasterResponse>>()

    fun submitPost(photoPart: MultipartBody.Part?, formDataBody: RequestBody) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                dashboardrepository.submitPost(photoPart, formDataBody).collect { values ->
                    addpostingResponse.postValue(values)
                    values.data?.Data?.ID?.let {
                        app.showToast("success")
                    } ?: kotlin.run {
//                        app.showToast("failure")
                    }
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }


    fun getMstData() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getCommonMaster("Common", "0", "0","0").collect { values ->
                    masterdata.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun getLocationWIthFromCities(citycode: String,skipall : Boolean = false): ArrayList<SpinnerRowModel> {
        var locationList = arrayListOf<SpinnerRowModel>()
        if (!skipall) {
            locationList.add(SpinnerRowModel("All", false, false, mstCode = "0"))
        }
        masterdata.value?.data?.data?.forEach {
            if (it.MstType == "Location" && citycode == "0") {
                locationList.add(
                    SpinnerRowModel(
                        it.MstDesc,
                        false,
                        false,
                        mstCode = it.MstCode
                    )
                )
            } else if (it.MstType == "Location" && it.City_Code == citycode) {
                locationList.add(
                    SpinnerRowModel(
                        it.MstDesc,
                        false,
                        false,
                        mstCode = it.MstCode
                    )
                )
            }
        }

        return locationList
    }
}