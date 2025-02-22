package com.customer.offerswindow.data.api.login

import com.customer.offerswindow.data.api.login.ApiServices.DashboardApiService
import javax.inject.Inject

class DashBoardHelperImpl @Inject constructor(private val dashboardApiService: DashboardApiService) {

    suspend fun getCustomerData(userid: String) =
        dashboardApiService.getCustomerData(userid)

    suspend fun getDashboardtestmonials(userid: String,maxtrasid : String) =
        dashboardApiService.getTestmonialData(userid,"Customer",maxtrasid)

    suspend fun getBannerdata(userid: String,maxtrasid : String) =
        dashboardApiService.getBannerData(userid,maxtrasid)

    suspend fun getSubCoachesData(userid: String) =
        dashboardApiService.getSubCoachesData(userid)

}