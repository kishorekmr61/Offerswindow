package com.customer.offerswindow.ui.home

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.CustomerDataResponse
import com.customer.offerswindow.model.SpinnerRowModel
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.model.TokenResponse
import com.customer.offerswindow.model.customersdata.PostUserIntrest
import com.customer.offerswindow.model.customersdata.PostWishlist
import com.customer.offerswindow.model.customersdata.PostuserSearch
import com.customer.offerswindow.model.dashboard.DashboardData
import com.customer.offerswindow.model.dashboard.OfferTypeResponse
import com.customer.offerswindow.model.dashboard.ServicesResponse
import com.customer.offerswindow.model.masters.CommonDataResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.model.masters.ShowRoomsResponse
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.repositry.Repository
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dashBoardRepositry: DashBoardRepositry,
    private val repository: Repository,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {
    var isloading = ObservableField(false)
    var customerinfo = MutableLiveData<NetworkResult<CustomerDataResponse>>()
    var dashboardresponse = MutableLiveData<PagingData<DashboardData>>()
    var masterdata = MutableLiveData<NetworkResult<CommonMasterResponse>>()
    var showRoomdata = MutableLiveData<NetworkResult<ShowRoomsResponse>>()
    var goldratesdata = MutableLiveData<NetworkResult<CommonMasterResponse>>()
    var postwishlistdata = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()
    var userIntrestResponse = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()
    var postuserSerchResponse = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()
    var offertypeResponse = MutableLiveData<NetworkResult<OfferTypeResponse>>()
    var serviceResponse = MutableLiveData<NetworkResult<ServicesResponse>>()
    var goldratesGridvalues = ObservableField<CommonDataResponse>()
    var profilepic = ObservableField<String>()
    var username = ObservableField<String>()
    var nodata = ObservableField(false)
    var tokenResponse = MutableLiveData<NetworkResult<TokenResponse>>()
    var filterResponse = MutableLiveData<NetworkResult<CommonMasterResponse>>()

    fun getDashboardData(
        lShowroomId: String,
        lLocationId: String,
        lServiceId: String,
        iCategoryId: String,
        iCityId: String,
        lCustomerId: String,
        defaultindex: String
    ) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                if (!AppPreference.read(Constants.USERUID,"").isNullOrEmpty()) {
                    val postuserSearch = PostuserSearch(
                        AppPreference.read(Constants.USERUID, "") ?: "",
                        lShowroomId,
                        lLocationId,
                        lServiceId,
                        iCategoryId
                    )
                    postUserSearch(postuserSearch)
                }
                dashBoardRepositry.getDashBoardOffersListPagenation(
                    lShowroomId,
                    lLocationId,
                    lServiceId, iCategoryId, iCityId, lCustomerId,
                    defaultindex
                )
                    .collect { values ->
                        dashboardresponse.postValue(values)
                    }

            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun getUserInfo(mobileno: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                dashBoardRepositry.getCustomerData(mobileno).collect { values ->
                    customerinfo.postValue(values)
                }

            } else {
                app.showToast("No Internet")
            }
        }
    }

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

    fun getShowRoomsData(lShowroomId: String, lLocationId: String, lServiceId: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                dashBoardRepositry.getShowRooms(lShowroomId, lLocationId, lServiceId)
                    .collect { values ->
                        showRoomdata.postValue(values)
                    }
            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun getGoldRatesData() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getCommonMaster("Price").collect { values ->
                    goldratesdata.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun postWishListItem(postWishlist: PostWishlist) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.postWishlistItem(postWishlist).collect { values ->
                    postwishlistdata.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun getToken(
        mobilenumber: String,
        password: String
    ) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getToken(mobilenumber, password).collect { values ->
                    AppPreference.write(Constants.TOKEN, values.data?.access_token ?: "")
                    AppPreference.write(Constants.TOKENTIMER, values.data?.expires_in ?: "")
                    tokenResponse.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun getFilterData() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getFilterData().collect { values ->
                    filterResponse.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }


    fun getUserInterest(postUserIntrest: PostUserIntrest) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.postUserIntrest(postUserIntrest).collect { values ->
                    userIntrestResponse.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun getLocationWIthFromCities(citycode: String): ArrayList<SpinnerRowModel> {
        var locationList = arrayListOf<SpinnerRowModel>()
        locationList.add(SpinnerRowModel("All", false, false, mstCode = "0"))
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


    fun getOfferTypeResponse(lServiceId: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getOfferTypeDetails(lServiceId).collect { values ->
                    offertypeResponse.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun getOfferServiceDetails(iOfferTypeId: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getOfferServiceDetails(iOfferTypeId).collect { values ->
                    serviceResponse.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun postUserSearch(postuserSearch: PostuserSearch) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.postSearch(postuserSearch).collect { values ->
                    postuserSerchResponse.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }
}
