package com.customer.offerswindow.data.api.login.apiServices

import com.customer.offerswindow.model.CustomerDataResponse
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.model.customersdata.PostSlotBooking
import com.customer.offerswindow.model.dashboard.BookingsResponse
import com.customer.offerswindow.model.dashboard.DashBoardDataResponse
import com.customer.offerswindow.model.dashboard.SlotsDataResponse
import com.customer.offerswindow.model.dashboard.WishListResponse
import com.customer.offerswindow.model.offerdetails.OfferDeatilsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DashboardApiService {
    @GET("UserManagement/GetProfile?")
    suspend fun getCustomerData(
        @Query("sPhoneNumber") mobileno: String,
    ): Response<CustomerDataResponse>

    @GET("ShowRoomOffers/GetOfferDetails?")
    suspend fun getDashBoardOffersList(
        @Query("lShowroomId") mobileno: String,
        @Query("lLocationId") lLocationId: String,
        @Query("lServiceId") lServiceId: String,
        @Query("lCustomerId") lCustomerId: String,
        @Query("lMaximumTransactionId") lMaximumTransactionId: String,
    ): Response<DashBoardDataResponse>

    @GET("ShowRoomOffers/GetIndividualOfferDetails?")
    suspend fun getIndividualOfferDetails(
        @Query("lRecordId") lRecordId: String,
    ): Response<OfferDeatilsResponse>


    @GET("ShowRoomOffers/GetSlotAvailability?")
    suspend fun getofferTimeSlots(
        @Query("lShowroomId") mobileno: String,
        @Query("lLocationId") lLocationId: String,
        @Query("lServiceId") lServiceId: String,
        @Query("sSlotRequiredDate") date: String,
    ): Response<SlotsDataResponse>

    @POST("ShowRoomOffers/PostSlotBooking")
    suspend fun postSlotBooking(
         @Body postSlotBooking: PostSlotBooking
    ): Response<StockPurchsasePostingResponse>

    @GET("ShowRoomOffers/GetMyBookings?")
    suspend fun getBookings(
        @Query("lCustomerID") lCustomerID: String,
    ): Response<BookingsResponse>


    @GET("ShowRoomOffers/GetWishlist?")
    suspend fun getWishList(
        @Query("lCustomerID") lCustomerID: String,
        @Query("iCategoryType") iCategoryType: String,
    ): Response<WishListResponse>

}