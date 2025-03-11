package com.customer.offerswindow.ui.wishlist

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.dashboard.DashBoardDataResponse
import com.customer.offerswindow.model.dashboard.WishListResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.repositry.Repository
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(
    private val repository: DashBoardRepositry,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {
    var wishlistResponse = MutableLiveData<NetworkResult<WishListResponse>>()
    var isloading = ObservableField(false)

    fun getWishListData( lCustomerID: String,iCategoryType:String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getWishList( "2"/*lCustomerID*/,iCategoryType).collect { values ->
                    wishlistResponse.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }
}