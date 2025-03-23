package com.customer.offerswindow.data.api.login.apiServices

import com.customer.offerswindow.model.OTPResponse
import com.customer.offerswindow.model.PostResetPassword
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.model.TokenResponse
import com.customer.offerswindow.model.UserResponse
import com.customer.offerswindow.model.customersdata.PostSignUp
import com.customer.offerswindow.model.customersdata.PostUpdateProfile
import com.customer.offerswindow.model.customersdata.PostWishlist
import com.customer.offerswindow.model.dashboard.ProfileUpdateRequest
import com.customer.offerswindow.model.dashboard.ProfileUpdateResponse
import com.customer.offerswindow.model.masters.CommonLocationMasterResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.model.masters.HubMaster
import com.customer.offerswindow.model.notification.NotificationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface MasterApiService {
    @GET("ShowRoomOffers/Masters?")
    suspend fun getCommonMaster(
        @Query("sMasterType") mastertype: String,
        @Query("iParentMasterID") parentid: String
    ): Response<CommonMasterResponse>


    @GET("ShowRoomOffers/Masters?")
    suspend fun getCommonLocationMaster(@Query("sMasterType") mastertype: String): Response<CommonLocationMasterResponse>

    @POST("token")
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun getToken(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grant_type: String = "password",
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


    @GET("UserManagement/GetValidateOtp?")
    suspend fun validateOTP(
        @Query("sCustomerMobileNo") mobileno: String,
        @Query("sOTP") sOTP: String,
    ): Response<OTPResponse>

    @POST("UserManagement/PostSignUp")
    suspend fun signupUser(
        postSignUp: PostSignUp
    ): Response<StockPurchsasePostingResponse>


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


    @GET("ShowRoomOffers/GetNotifications?")
    suspend fun getNotifications(
        @Query("lCustomerID") mobileno: String,
    ): Response<NotificationResponse>

    @POST("ShowRoomOffers/PostWishlist")
    suspend fun postWishList(
        @Body postWishlist: PostWishlist,
    ): Response<StockPurchsasePostingResponse>

    @POST("UserManagement/PostProfileUpdate")
    suspend fun submitProfileUpdateData(
        @Body profileUpdateRequest: ProfileUpdateRequest
    ): Response<StockPurchsasePostingResponse>

    @Multipart
    @POST("UserManagement/PostProfileUpdate")
    suspend fun submitProfileUpdateData(
        @Part part: MultipartBody.Part?,
        @Part("FormData") formDataBody: RequestBody
    ): Response<ProfileUpdateResponse>
}