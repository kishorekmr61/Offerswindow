package com.customer.offerswindow.data.api.login.apiServices

import com.customer.offerswindow.model.OTPResponse
import com.customer.offerswindow.model.PostResetPassword
import com.customer.offerswindow.model.OfferWindowCommonResponse
import com.customer.offerswindow.model.TokenResponse
import com.customer.offerswindow.model.UserResponse
import com.customer.offerswindow.model.customersdata.PostSignUp
import com.customer.offerswindow.model.customersdata.PostUpdateProfile
import com.customer.offerswindow.model.customersdata.PostUserIntrest
import com.customer.offerswindow.model.customersdata.PostWishlist
import com.customer.offerswindow.model.customersdata.PostuserSearch
import com.customer.offerswindow.model.customersdata.UserSigUp
import com.customer.offerswindow.model.dashboard.OfferTypeResponse
import com.customer.offerswindow.model.dashboard.ProfileUpdateRequest
import com.customer.offerswindow.model.dashboard.ProfileUpdateResponse
import com.customer.offerswindow.model.dashboard.ServicesResponse
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

    @POST("UserManagement/PostSendOtp?")
    suspend fun postSignupOTP(
        @Body userSigUp: UserSigUp,
    ): Response<OfferWindowCommonResponse>



    @GET("UserManagement/GetValidateOtp?")
    suspend fun validateOTP(
        @Query("sCustomerMobileNo") mobileno: String,
        @Query("sOTP") sOTP: String,
        @Query("sPinNo") sPinNo: String,
    ): Response<OTPResponse>

    @POST("UserManagement/PostSignUp")
    suspend fun signupUser(
        postSignUp: PostSignUp
    ): Response<OfferWindowCommonResponse>


    @GET("Transactions/ForgotPassword?")
    suspend fun forgotPassword(
        @Query("lUserID") mobileno: String,
    ): Response<OfferWindowCommonResponse>

    @POST("UserManagement/ResetPassword")
    suspend fun resetPassword(
        @Body postResetPassword: PostResetPassword,
    ): Response<OfferWindowCommonResponse>

    @GET("UserManagement/DeleteCustomerProfile?")
    suspend fun deleteUserAccount(
        @Query("lUserID") mobileno: String,
    ): Response<OfferWindowCommonResponse>


    @GET("ShowRoomOffers/GetNotifications?")
    suspend fun getNotifications(
        @Query("lCustomerID") mobileno: String,
    ): Response<NotificationResponse>

    @POST("ShowRoomOffers/PostWishlist")
    suspend fun postWishList(
        @Body postWishlist: PostWishlist,
    ): Response<OfferWindowCommonResponse>

    @POST("ShowRoomOffers/PostSearchHistory")
    suspend fun postSearh(
        @Body postuserSearch: PostuserSearch
    ): Response<OfferWindowCommonResponse>

    @POST("UserManagement/PostProfileUpdate")
    suspend fun submitProfileUpdateData(
        @Body profileUpdateRequest: ProfileUpdateRequest
    ): Response<OfferWindowCommonResponse>

    @POST("UserManagement/PostUserTransactions")
    suspend fun submitUserIntrest(
        @Body postUserIntrest: PostUserIntrest
    ): Response<OfferWindowCommonResponse>

    @Multipart
    @POST("UserManagement/PostProfileUpdate")
    suspend fun submitProfileUpdateData(
        @Part part: MultipartBody.Part?,
        @Part("FormData") formDataBody: RequestBody
    ): Response<ProfileUpdateResponse>


    @GET("ShowRoomOffers/GetOfferTypeDetails")
    suspend fun getFilterData(
    ): Response<CommonMasterResponse>


    @GET("ShowRoomOffers/GetOfferTypeDetails?")
    suspend fun GetOfferTypeDetails(
        @Query("lServiceId") mastertype: String
    ): Response<OfferTypeResponse>


    @GET("ShowRoomOffers/GetOfferServiceDetails?")
    suspend fun GetOfferServiceDetails(
        @Query("iOfferTypeId") iOfferTypeId: String
    ): Response<ServicesResponse>
}