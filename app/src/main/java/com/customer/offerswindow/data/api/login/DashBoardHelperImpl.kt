package com.customer.offerswindow.data.api.login

import com.customer.offerswindow.data.api.login.apiServices.DashboardApiService
import javax.inject.Inject

class DashBoardHelperImpl @Inject constructor(private val dashboardApiService: DashboardApiService) {

    suspend fun getCustomerData(userid: String) =
        dashboardApiService.getCustomerData(userid)




    suspend fun getSubCoachesData(userid: String) =
        dashboardApiService.getSubCoachesData(userid)

}