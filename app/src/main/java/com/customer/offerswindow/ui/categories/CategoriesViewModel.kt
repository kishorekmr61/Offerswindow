package com.customer.offerswindow.ui.categories

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.repositry.Repository
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
     private val repository: Repository,
    private var networkHelper: NetworkHelper,
    var app: Application,
) :  ViewModel() {
    var masterdata = MutableLiveData<NetworkResult<CommonMasterResponse>>()
    var isloading = ObservableField(false)

    fun getMstData() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getCommonMaster("Common").collect { values ->
                    masterdata.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }
}