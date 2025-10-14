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
import com.customer.offerswindow.model.OfferWindowCommonResponse
import com.customer.offerswindow.model.PostOfferWindowCommonResponse
import com.customer.offerswindow.model.SpinnerRowModel
import com.customer.offerswindow.model.customersdata.PostUserIntrest
import com.customer.offerswindow.model.customersdata.PostWishlist
import com.customer.offerswindow.model.customersdata.PostuserSearch
import com.customer.offerswindow.model.dashboard.DashboardData
import com.customer.offerswindow.model.dashboard.OfferTypeResponse
import com.customer.offerswindow.model.dashboard.ServicesResponse
import com.customer.offerswindow.model.masters.CommonDataResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.model.masters.ShowRoomsResponse
import com.customer.offerswindow.model.offerdetails.SearchCriteriaResponse
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.repositry.Repository
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    var dashboardresponse = MutableLiveData<PagingData<DashboardData>>()
    var searchShowRoomResults = MutableLiveData<NetworkResult<ShowRoomsResponse>>()
    var goldratesdata = MutableLiveData<NetworkResult<CommonMasterResponse>>()
    var postwishlistdata = MutableLiveData<NetworkResult<OfferWindowCommonResponse>>()
    var userIntrestResponse = MutableLiveData<NetworkResult<PostOfferWindowCommonResponse>>()
    var postuserSerchResponse = MutableLiveData<NetworkResult<PostOfferWindowCommonResponse>>()
    var subcategoryResponse = MutableLiveData<NetworkResult<OfferTypeResponse>>()
    var clicksubcategoryResponse = MutableLiveData<NetworkResult<OfferTypeResponse>>()
    var categoriesResponse = MutableLiveData<NetworkResult<ServicesResponse>>()
    var searchcriteria = MutableLiveData<NetworkResult<SearchCriteriaResponse>>()
    var goldratesGridvalues = ObservableField<CommonDataResponse>()
    var profilepic = ObservableField<String>()
    var username = ObservableField<String>()
    var nodata = ObservableField(false)
    var masterdata = MutableLiveData<NetworkResult<CommonMasterResponse>>()
//    var filterResponse = MutableLiveData<NetworkResult<CommonMasterResponse>>()

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


    fun getOfferCategories(iOfferTypeId: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getOfferCategories(iOfferTypeId).collect { values ->
                    categoriesResponse.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun getOfferSubcategoryChips(lServiceId: String, isclicked: Boolean,) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getOfferChips(lServiceId).collect { values ->
                    if (isclicked){
                        clicksubcategoryResponse.postValue(values)
                    }else {
                        subcategoryResponse.postValue(values)
                    }
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }


    fun getShowRoomSearch(lShowroomId: String, lLocationId: String, lServiceId: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                dashBoardRepositry.getShowRooms(lShowroomId, lLocationId, lServiceId)
                    .collect { values ->
                        searchShowRoomResults.postValue(values)
                    }
            } else {
                app.showToast("No Internet")
            }
        }
    }

    fun getGoldRatesData() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getCommonMaster("Price","0","0","0").collect { values ->
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
        val sType = object : TypeToken<List<CommonDataResponse>>() {}.type
        val otherList = Gson().fromJson<List<CommonDataResponse>>(
            AppPreference.read(Constants.MASTERDATA, ""),
            sType
        )
        val locationList = arrayListOf<SpinnerRowModel>()
        locationList.add(SpinnerRowModel("All", false, false, mstCode = "0"))
        otherList?.forEach {
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

    fun getName(msttype: String, mstcode: String): String {
        val sType = object : TypeToken<List<CommonDataResponse>>() {}.type
        val otherList = Gson().fromJson<List<CommonDataResponse>>(
            AppPreference.read(Constants.MASTERDATA, ""),
            sType
        )
        var name = "All"
        otherList?.forEach {
            if (it.MstType == msttype) {
                if (it.MstCode == mstcode)
                    return it.MstDesc
            }
        }
        return name

    }

    fun getSearchCriteria(Customerid: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                dashBoardRepositry.getSearchCriteria(Customerid).collect { values ->
                    searchcriteria.postValue(values)
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


    fun getMstData(iCityId : String, lServiceId : String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getCommonMaster("Location",lServiceId,iCityId,"0").collect { values ->
                    masterdata.postValue(values)
                }
            } else {
                app.showToast("No Internet")
            }
        }
    }

}
