package com.customer.offerswindow.data.api.login

import com.customer.offerswindow.data.api.login.apiServices.DashboardApiService
import javax.inject.Inject

class DashBoardHelperImpl @Inject constructor(private val dashboardApiService: DashboardApiService) {

    suspend fun getCustomerData(userid: String) =
        dashboardApiService.getCustomerData(userid)


    suspend fun getDashBoardOffersList(lShowroomId: String,lLocationId: String,lServiceId: String) =
        dashboardApiService.getDashBoardOffersList(lShowroomId,lLocationId,lServiceId)

    suspend fun getIndividualOfferDetails(lRecordId: String) =
        dashboardApiService.getIndividualOfferDetails(lRecordId)

}