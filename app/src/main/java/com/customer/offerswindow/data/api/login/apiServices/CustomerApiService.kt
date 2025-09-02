package com.customer.offerswindow.data.api.login.apiServices

import com.customer.offerswindow.model.*
import com.customer.offerswindow.model.customersdata.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CustomerApiService {
    @GET("Transactions/CustomersList?")
    suspend fun getCustomerList(
        @Query("lUserID") userid: String,
        @Query("lMaximumCustomerID") imaxid: Int
    ): Response<CustomerDataResponse>

    @GET("Transactions/CustomersDetailsByID?")
    suspend fun getCustomerDeatilsByid(
        @Query("lUserID") userid: String,
    ): Response<CustomerDataResponse>



    @POST("UserManagement/OTPDetails")
    suspend fun postOTPData(
        @Body postPhoneNumber: PostPhoneNumber
    ): Response<OfferWindowCommonResponse>


    @POST("UserManagement/PostSignUp")
    suspend fun postSignUp(
        @Body postSignUp: PostSignUp
    ): Response<OfferWindowCommonResponse>

    @GET("UserManagement/OTPValidation?")
    suspend fun postVerifyOTPData(
        @Query("sCustomerMobileNo") sCustomerMobileNo: String,
        @Query("sOTP") sOTP: String,
    ): Response<OfferWindowCommonResponse>


//    @GET("CustomerTransactions/EventBookingDetails?")
//    suspend fun getConfirmedTickets(
//        @Query("lCustomerUID") lCustomerUID: String,
//        @Query("lMaximumTransactionID") lMaximumTransactionID: Long,
//    ): Response<ConfirmetTicketsResponse>

}