package com.customer.offerswindow.data.api.login.apiServices

import com.customer.offerswindow.model.CustomerDataResponse
import com.customer.offerswindow.model.OfferWindowCommonResponse
import com.customer.offerswindow.model.PostOfferWindowCommonResponse
import com.customer.offerswindow.model.customersdata.PostOfferBooking
import com.customer.offerswindow.model.customersdata.PostSlotBooking
import com.customer.offerswindow.model.dashboard.BookingsResponse
import com.customer.offerswindow.model.dashboard.DashBoardDataResponse
import com.customer.offerswindow.model.dashboard.ProfileUpdateResponse
import com.customer.offerswindow.model.dashboard.SlotsDataResponse
import com.customer.offerswindow.model.dashboard.WishListResponse
import com.customer.offerswindow.model.masters.ShowRoomsResponse
import com.customer.offerswindow.model.offerdetails.OfferDeatilsResponse
import com.customer.offerswindow.model.offerdetails.SearchCriteriaResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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
        @Query("iCategoryId") iCategoryId: String,
        @Query("iCityId") iCityId: String,
        @Query("lCustomerId") lCustomerId: String,
        @Query("lMaximumTransactionId") lMaximumTransactionId: String,
    ): Response<DashBoardDataResponse>


    @GET("ShowRoomOffers/GetShowroomList?")
    suspend fun getShowrooms(
        @Query("iServiceType") iServiceType: String,
        @Query("lLocationID") lLocationID: String,
        @Query("lShowRoomID") lShowRoomID: String,
    ): Response<ShowRoomsResponse>

    @GET("ShowRoomOffers/GetIndividualOfferDetails?")
    suspend fun getIndividualOfferDetails(
        @Query("lRecordId") lRecordId: String,  @Query("lCustomerID") lCustomerID: String
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
    ): Response<PostOfferWindowCommonResponse>

    @GET("ShowRoomOffers/GetSlotBookings?")
    suspend fun getBookings(
        @Query("lCustomerID") lCustomerID: String,
    ): Response<BookingsResponse>


    @GET("ShowRoomOffers/GetWishlist?")
    suspend fun getWishList(
        @Query("lCustomerID") lCustomerID: String,
        @Query("iCategoryType") iCategoryType: String,
    ): Response<WishListResponse>


    @POST("ShowRoomOffers/PostOfferBooking")
    suspend fun postOfferBooking(
        @Body postOfferBooking: PostOfferBooking
    ): Response<PostOfferWindowCommonResponse>


    @GET("ShowRoomOffers/GetOfferBookings?")
    suspend fun getOffersData(
        @Query("lCustomerID") lCustomerID: String,
    ): Response<BookingsResponse>

    @POST("ShowRoomOffers/PostDeleteWishlist?")
    suspend fun removeWishListIteam(
        @Query("lOfferId") lOfferId: String,
        @Query("lCustomerId") lCustomerId: String,
    ): Response<PostOfferWindowCommonResponse>

    @GET("ShowRoomOffers/GetLatestSearchCriteria?")
    suspend fun getSearchCriteria(
        @Query("lCustomerID") lCustomerID: String
    ): Response<SearchCriteriaResponse>


    @Multipart
    @POST("ShowRoomOffers/PostOfferRequest")
    suspend fun submitPost(
        @Part part: MultipartBody.Part?,
        @Part("FormData") formDataBody: RequestBody
    ): Response<ProfileUpdateResponse>
}