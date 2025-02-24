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
    ): Response<CustomerListResponse>

    @GET("Transactions/CustomersDetailsByID?")
    suspend fun getCustomerDeatilsByid(
        @Query("lUserID") userid: String,
    ): Response<CustomerListResponse>

    @POST("Transactions/customerCheck")
    suspend fun submitCustomerCheckUp(@Body customerCheckUpRequest: CustomerCheckUpRequest): Response<ProfileUpdateResponse>

    @POST("Transactions/CustomerFamily")
    suspend fun submitFamilyDetails(@Body familyMemberPostRequest: FamilyMemberPostRequest): Response<ProfileUpdateResponse>

    @Multipart
    @POST("Transactions/ProfileUpdate")
    suspend fun submitProfileUpdateData(
        @Part photo: MultipartBody.Part,
        @Part("CustomerUID") CustomerUID: RequestBody,
        @Part("CustomerCategory") CustomerCategory: RequestBody,
        @Part("CustomerName") CustomerName: RequestBody,
        @Part("SurName") SurName: RequestBody,
        @Part("MobileNo") MobileNo: RequestBody,
        @Part("DoB") DoB: RequestBody,
        @Part("EmailID") EmailID: RequestBody,
        @Part("MarriageAnniversaryDate") MarriageAnniversaryDate: RequestBody,
        @Part("FitnessGoal") FitnessGoal: RequestBody,
        @Part("CustomerHeight") CustomerHeight: RequestBody,
        @Part("CustomerWeight") CustomerWeight: RequestBody,
        @Part("CustomerPhotoFilePath") CustomerPhotoFilePath: RequestBody,
        @Part("CreatedBy") CreatedBy: RequestBody,
        @Part("CreatedDateTime") CreatedDateTime: RequestBody,
        @Part("UpdatedBy") UpdatedBy: RequestBody,
        @Part("UpdatedDateTime") UpdatedDateTime: RequestBody
    ): Response<ProfileUpdateResponse>

    @Multipart
    @POST("Transactions/ProfileUpdate")
    suspend fun submitProfileUpdateData(
        @Part part: MultipartBody.Part?,
        @Part("FormData") formDataBody: RequestBody
    ): Response<ProfileUpdateResponse>

//    @GET("Transactions/ReferenceListStatus?")
//    suspend fun getReferenceHistory(@Query("lUserID") userid: String): Response<CustomerReferenceHistoryListResponse>
//
//
//    @GET("Transactions/PurchaseHistoryDetails?")
//    suspend fun getPurchaseHistory(
//        @Query("lUserID") userid: String,
//        @Query("lMaximumTransactionID") imaxid: String
//    ): Response<PurchaseHistoryResponse>
//
//
//    @GET("Transactions/CustomerActivitiesHistory?")
//    suspend fun getCustomerActivitiesHistory(
//        @Query("lUserID") userId: String,
//        @Query("sFromDate") fromDate: String,
//        @Query("sToDate") toDate: String,
//        @Query("lMaximumTransactionID") lMaximumTransactionID: String = "0"
//    ): Response<ActivitiesResponse>
//
//    @GET("CustomerTransactions/EventsList?")
//    suspend fun getEventsList(
//        @Query("lUserID") lUserID: String,
//        @Query("lMaximumTransactionID") imaxid: Long
//    ): Response<EventsResponse>
//
//
//    @GET("Transactions/InvoiceDetails?")
//    suspend fun getInvoiceList(
//        @Query("lUserID") lUserID: String,
//        @Query("lMaximumTransactionID") imaxid: Long
//    ): Response<CustomerInvoiceResponse>
//
//
//    @GET("CustomerTransactions/EventDetails?")
//    suspend fun getEventsDetails(
//        @Query("lEventID") lEventID: Long,
//        @Query("lMaximumTransactionID") imaxid: Long
//    ): Response<EventsResponse>
//
//    @GET("CustomerTransactions/CustomerWeightDetails?")
//    suspend fun getGraphHistory(
//        @Query("lUserID") userid: String,
//        @Query("lMaximumTransactionID") lMaximumTransactionID: String
//    ): Response<GraphResponse>
//
//    @GET("Transactions/HealthCheckGraph?")
//    suspend fun getGraphHistory(
//        @Query("lUserUID") userid: String,
//        @Query("sHealthParameter") sHealthParameter: String,
//        @Query("lMaximumTransactionID") lMaximumTransactionID: String
//    ): Response<GraphResponse>


//    @GET("Transactions/CaloriesCalculatorDetails?")
//    suspend fun getCaloriesCalculatorDetails(
//        @Query("lUserUID") userid: String
//    ): Response<CalorieCaliculationdetailsResponse>
//
//    @GET("CustomerTransactions/CaloriesPlanningDetails?")
//    suspend fun getUserCaloriesCalculatorDetails(
//        @Query("lCustomerUID") userid: String
//    ): Response<CalorieDataPostingResponse>
//
//    @POST("Transactions/CaloriePlanning?")
//    suspend fun getCaloriesCalculatorData(
//        @Body calorieDataPost: CalorieDataPost
//    ): Response<CalorieDataPostingResponse>


//    @GET("CustomerTransactions/MyAchievements?")
//    suspend fun getAchievementData(
//        @Query("lUserID") userid: String,
//        @Query("lMaximumTransactionID") lMaximumTransactionID: Long
//    ): Response<AchievementResponse>

//    @GET("CustomerTransactions/ProgramsList?")
//    suspend fun getCustomerPrograms(
//        @Query("iProgramCode") iProgramCode: String,
//        @Query("sProgramName") sProgramName: String,
//        @Query("lCustomerUID") lCustomerUID: String,
//        @Query("lMaximumProgramID") lMaximumTransactionID: Long
//    ): Response<MyProgramResponse>

//    @GET("Masters/Masters")
//    suspend fun getCustomerPlans(
//        @Query("sMasterType") mastertype: String,
//        @Query("iParentMasterID") parentid: Long,
//        @Query("lUserID") lUserID: String
//    ): Response<CommonPlanMst>
//
//    @GET("CustomerTransactions/PlansList?")
//    suspend fun getCustomerPlans(
//        @Query("lCustomerUID") lUserID: String
//    ): Response<CommonPlanMst>

//    @GET("CustomerTransactions/IssueStatusDetails?")
//    suspend fun getReportIssues(
//        @Query("lCustomerUID") lCustomerUID: String,
//        @Query("lMaximumTransactionID") lMaximumTransactionID: Long
//    ): Response<ReportResponse>

//    @GET("Masters/Masters")
//    suspend fun getTasks(
//        @Query("sMasterType") mastertype: String, @Query("iParentMasterID") parentid: Long
//    ): Response<TaskResponse>


    @Multipart
    @POST("CustomerTransactions/TaskDetails")
    suspend fun postTask(
        @Part part: MultipartBody.Part?,
        @Part("FormData") formDataBody: RequestBody
    ): Response<StockPurchsasePostingResponse>

    @POST("Transactions/NewEnquiry?")
    suspend fun postNewEnquiry(
        @Body postNewEnquiry: PostNewEnquiry
    ): Response<StockPurchsasePostingResponse>

    @POST("Transactions/UserSignUp")
    suspend fun postSignUP(
        @Body postNewEnquiry: PostNewEnquiry
    ): Response<StockPurchsasePostingResponse>

//    @POST("CustomerTransactions/TicketBookingConfirmation?")
//    suspend fun postTicketConfirmation(
//        @Body postTicketConfirmation: PostTicketConfirmation
//    ): Response<EventTicketDetailsResponse>

//    @POST("CustomerTransactions/CustomerWeight")
//    suspend fun postCustomeWeightData(
//        @Body postcustomerweight: postCustomerWeight
//    ): Response<StockPurchsasePostingResponse>

    @Multipart
    @POST("CustomerTransactions/ReportAnIssue")
    suspend fun postissueData(
        @Part part: MultipartBody.Part?,
        @Part("FormData") formDataBody: RequestBody
    ): Response<StockPurchsasePostingResponse>

    @POST("UserManagement/OTPDetails")
    suspend fun postOTPData(
        @Body postPhoneNumber: PostPhoneNumber
    ): Response<StockPurchsasePostingResponse>

    @GET("UserManagement/OTPValidation?")
    suspend fun postVerifyOTPData(
        @Query("sCustomerMobileNo") sCustomerMobileNo: String,
        @Query("sOTP") sOTP: String,
    ): Response<StockPurchsasePostingResponse>


//    @GET("CustomerTransactions/EventBookingDetails?")
//    suspend fun getConfirmedTickets(
//        @Query("lCustomerUID") lCustomerUID: String,
//        @Query("lMaximumTransactionID") lMaximumTransactionID: Long,
//    ): Response<ConfirmetTicketsResponse>

}