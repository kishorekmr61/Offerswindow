package com.customer.offerswindow.data.api.login.apiServices

import com.customer.offerswindow.model.PostResetPassword
import com.customer.offerswindow.model.TokenResponse
import com.customer.offerswindow.model.UserResponse
import com.customer.offerswindow.model.masters.CommonLocationMasterResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.model.masters.HubMaster
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface MasterApiService {
    @GET("Masters/Masters")
    suspend fun getHubMaster(
        @Query("sMasterType") mastertype: String,
        @Query("iParentMasterID") parentid: String
    ): Response<List<HubMaster>>

    @GET("Masters/Masters")
    suspend fun getCommonMaster(
        @Query("sMasterType") mastertype: String,
        @Query("iParentMasterID") parentid: String
    ): Response<CommonMasterResponse>


    @GET("Masters/Masters")
    suspend fun getCommonLocationMaster(@Query("sMasterType") mastertype: String): Response<CommonLocationMasterResponse>

    @GET("token")
//    @FormUrlEncoded
//    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun getToken(
        /*@Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grant_type: String="password",*/
    ): Response<TokenResponse>

    @GET("UserManagement/CheckUserLogin?")
    suspend fun verifylogin(
        @Query("sUserLoginID") mobileno: String,
        @Query("sUserPassword") password: String,
    ): Response<UserResponse>


    @GET("UserManagement/PostSendOtp?")
    suspend fun verifyPhone(
        @Query("sPhoneNumber") mobileno: String,
    ): Response<UserResponse>

    @POST("UserManagement/PostSignUp")
    suspend fun signupUser(

    ): Response<UserResponse>






    @GET("Transactions/ForgotPassword?")
    suspend fun forgotPassword(
        @Query("lUserID") mobileno: String,
    ): Response<StockPurchsasePostingResponse>

    @POST("UserManagement/ResetPassword")
    suspend fun resetPassword(
        @Body postResetPassword: PostResetPassword,
    ): Response<StockPurchsasePostingResponse>

    @GET("UserManagement/DeleteCustomerProfile?")
    suspend fun deleteUserAccount(
        @Query("lUserID") mobileno: String,
    ): Response<StockPurchsasePostingResponse>
}