package com.customer.offerswindow.data.api.login.apiServices

import com.customer.offerswindow.model.CustomerDataResponse
import com.customer.offerswindow.model.dashboard.DashBoardDataResponse
import com.customer.offerswindow.model.offerdetails.OfferDeatilsResponse
import retrofit2.Response
import retrofit2.http.GET
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
    ): Response<DashBoardDataResponse>

    @GET("ShowRoomOffers/GetIndividualOfferDetails?")
    suspend fun getIndividualOfferDetails(
        @Query("lRecordId") lRecordId: String,
    ): Response<OfferDeatilsResponse>


}