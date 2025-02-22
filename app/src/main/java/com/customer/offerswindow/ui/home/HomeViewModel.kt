package com.customer.offerswindow.ui.home

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.CustomerFeedBackHome
import com.customer.offerswindow.model.CustomerListResponse
import com.customer.offerswindow.model.TestmonialResponse
import com.customer.offerswindow.model.customersdata.GraphResponse
import com.customer.offerswindow.model.dashboard.DashBoardBannerData
import com.customer.offerswindow.model.dashboard.HomeHeader
import com.customer.offerswindow.model.dashboard.postCustomerWeight
import com.customer.offerswindow.model.stock.StockPurchsasePostingResponse
import com.customer.offerswindow.repositry.CustomerListRepository
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.utils.helper.NetworkHelper
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DashBoardRepositry,
    private val customerListRepository: CustomerListRepository,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {
    var isloading = ObservableField(false)
    var userbmi = ObservableField(0.0)
    var name = ObservableField("")
    var customerinfo = MutableLiveData<NetworkResult<CustomerListResponse>>()
    var postweightResponse = MutableLiveData<NetworkResult<StockPurchsasePostingResponse>>()
    var dashboartestmonialddata = MutableLiveData<NetworkResult<TestmonialResponse>>()
    var bannerResponse = MutableLiveData<NetworkResult<DashBoardBannerData>>()
    var graphdata = MutableLiveData<NetworkResult<GraphResponse>>()

    fun getDashboardData(userid: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getCustomerData(userid).collect { values ->
                    customerinfo.postValue(values)
                }

            } else {
                com.customer.offerswindow.utils.showToast("No Internet")
            }
        }
    }


    fun getTestmonialData(userid: String): ArrayList<CustomerFeedBackHome> {
        val customerFeedBackHomeList = ArrayList<CustomerFeedBackHome>()
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getTestmonialData(userid, "0").collect { values ->
                    dashboartestmonialddata.postValue(values)
                }
            } else {
                com.customer.offerswindow.utils.showToast("No Internet")
            }
        }
        return customerFeedBackHomeList
    }

    fun getBannerInfo(userid: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getBannersData(userid).collect { values ->
                    bannerResponse.postValue(values)
                }
            } else {
                com.customer.offerswindow.utils.showToast("No Internet")
            }
        }
    }

    fun getCustomerGraph(userUid: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                customerListRepository.getGraphDetails(userUid, "0").collect { values ->
                    graphdata.postValue(values)
                }
            } else {
                com.customer.offerswindow.utils.showToast("No Internet")
            }
        }
    }


    fun getHomeData(): ArrayList<HomeHeader> {
        var data = ArrayList<HomeHeader>()
        data.add(HomeHeader("Focus \n Group",R.drawable.focus_group))
        data.add(HomeHeader( "My \n Achievements", R.drawable.my_achievments))
        data.add( HomeHeader( "Reference \n History", R.drawable.reference_history))
        data.add(HomeHeader( "Diet Plan \n request", R.drawable.diet_plan_request))
        data.add( HomeHeader( "Stock \n Purchases", R.drawable.stock_purchase))
        data.add( HomeHeader( "Invoices", R.drawable.ic_invoice2))
        data.add( HomeHeader( "Wallet \n Balances", R.drawable.wallet2))
        data.add( HomeHeader( "Rewards", R.drawable.reward_point))
        data.add( HomeHeader( "Notifications", R.drawable.notification2))
        return data
    }

    fun postWeightData(postcustomerweight: postCustomerWeight) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                customerListRepository.postCustomerWeightData(postcustomerweight)
                    .collect { values ->
                        if (values?.data?.Status == 200) {
                            isloading.set(false)
                            postweightResponse.postValue(values)
                            com.customer.offerswindow.utils.showToast(values.data?.Message ?: "")
                            isloading.set(true)
                            getCustomerGraph(AppPreference.read(Constants.USERUID,"")?:"")
                        } else {
                            if (values.data?.Message.isNullOrEmpty()) {
                                com.customer.offerswindow.utils.showToast(values.message ?: "")
                            } else
                                com.customer.offerswindow.utils.showToast(
                                    values.data?.Message ?: ""
                                )
                        }
                    }
            } else {
                com.customer.offerswindow.utils.showToast("No Internet")
            }
        }
    }

}
