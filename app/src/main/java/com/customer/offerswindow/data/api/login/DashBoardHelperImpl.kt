package com.customer.offerswindow.data.api.login

import com.customer.offerswindow.data.api.login.apiServices.DashboardApiService
import com.customer.offerswindow.model.customersdata.PostSlotBooking
import javax.inject.Inject

class DashBoardHelperImpl @Inject constructor(private val dashboardApiService: DashboardApiService) {

    suspend fun getCustomerData(userid: String) =
        dashboardApiService.getCustomerData(userid)


    suspend fun getDashBoardOffersList(lShowroomId: String,lLocationId: String,lServiceId: String) =
        dashboardApiService.getDashBoardOffersList(lShowroomId,lLocationId,lServiceId)

    suspend fun getIndividualOfferDetails(lRecordId: String) =
        dashboardApiService.getIndividualOfferDetails(lRecordId)

    suspend fun getofferTimeSlots(lShowroomId: String,lLocationId: String,lServiceId: String,date:String) =
        dashboardApiService.getofferTimeSlots(lShowroomId,lLocationId,lServiceId,date)

    suspend fun postSlotBooking(postSlotBooking: PostSlotBooking) =
        dashboardApiService.postSlotBooking(postSlotBooking)

}