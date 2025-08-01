package com.customer.offerswindow.data.api.login

import com.customer.offerswindow.data.api.login.apiServices.DashboardApiService
import com.customer.offerswindow.model.customersdata.PostOfferBooking
import com.customer.offerswindow.model.customersdata.PostSlotBooking
import javax.inject.Inject

class DashBoardHelperImpl @Inject constructor(private val dashboardApiService: DashboardApiService) {

    suspend fun getCustomerData(userid: String) =
        dashboardApiService.getCustomerData(userid)


    suspend fun getDashBoardOffersList(
        lShowroomId: String,
        lLocationId: String,
        lServiceId: String,
        iCategoryId: String,
        iCityId: String,
        lCustomerId: String,
        lMaximumTransactionId: String
    ) =
        dashboardApiService.getDashBoardOffersList(
            lShowroomId,
            lLocationId,
            lServiceId,
            iCategoryId,
            iCityId,
            lCustomerId,
            lMaximumTransactionId
        )

    suspend fun getIndividualOfferDetails(lRecordId: String) =
        dashboardApiService.getIndividualOfferDetails(lRecordId)

    suspend fun getofferTimeSlots(
        lShowroomId: String,
        lLocationId: String,
        lServiceId: String,
        date: String
    ) =
        dashboardApiService.getofferTimeSlots(lShowroomId, lLocationId, lServiceId, date)

    suspend fun postSlotBooking(postSlotBooking: PostSlotBooking) =
        dashboardApiService.postSlotBooking(postSlotBooking)

    suspend fun getBooking(lCustomerID: String) =
        dashboardApiService.getBookings(lCustomerID)

    suspend fun getShowRooms(lShowroomId: String, lLocationId: String, lServiceId: String) =
        dashboardApiService.getShowrooms(lServiceId, lLocationId, lShowroomId)

    suspend fun getWishList(lCustomerID: String, iCategoryType: String) =
        dashboardApiService.getWishList(lCustomerID, iCategoryType)

    suspend fun postOfferBooking(postOfferBooking: PostOfferBooking) =
        dashboardApiService.postOfferBooking(postOfferBooking)

    suspend fun getOffersData(lCustomerID: String) =
        dashboardApiService.getOffersData(lCustomerID)

    suspend fun removeWishListIteam(lOfferId: String,lCustomerId:String) =
        dashboardApiService.removeWishListIteam(lOfferId,lCustomerId)

}