package com.customer.offerswindow.data.api.login.ApiServices

import com.customer.offerswindow.model.CustomerListResponse
import com.customer.offerswindow.model.dashboard.DashBoardBannerData
import com.customer.offerswindow.model.dashboard.SubCoachesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardApiService {
    @GET("Transactions/CustomersDetailsByID?")
    suspend fun getCustomerData(
        @Query("lUserID") userid: String,
    ): Response<CustomerListResponse>
//
//    @GET("Transactions/TestimonialDetails?")
//    suspend fun getTestmonialData(
//        @Query("lUserUID") userid: String,
//        @Query("sRelatedTo") sRelatedTo :String,
//        @Query("lMaximumTransactionID") lMaximumTransactionID: String,
//    ): Response<TestmonialResponse>


    @GET("CustomerTransactions/DashboardBannerDetails?")
    suspend fun getBannerData(
        @Query("lUserID") userid: String,
        @Query("lMaximumTransactionID") lMaximumTransactionID: String,
    ): Response<DashBoardBannerData>

    @GET("Transactions/SubCoachesDetails?")
    suspend fun getSubCoachesData(
        @Query("lUserUID") userid: String,
        @Query("lMaximumTransactionID") lMaximumTransactionID: String = "0"
    ): Response<SubCoachesResponse>
}