package com.customer.offerswindow.data.api.login

import com.customer.offerswindow.data.api.login.ApiServices.CustomerApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class CustomerHelperImpl @Inject constructor(private val customerApiService: CustomerApiService) {
    suspend fun getCustomerList(userid: String, imaxid: Int) =
        customerApiService.getCustomerList(userid, imaxid)

    suspend fun getCustomerDetailsById(userid: String) =
        customerApiService.getCustomerDeatilsByid(userid)


    suspend fun submitProfileUpdateData(
        part: MultipartBody.Part,
        CustomerUID: RequestBody,
        CustomerCategory: RequestBody,
        CustomerName: RequestBody,
        SurName: RequestBody,
        MobileNo: RequestBody,
        DoB: RequestBody,
        EmailID: RequestBody,
        MarriageAnniversaryDate: RequestBody,
        FitnessGoal: RequestBody,
        CustomerHeight: RequestBody,
        CustomerWeight: RequestBody,
        CustomerPhotoFilePath: RequestBody,
        CreatedBy: RequestBody,
        CreatedDateTime: RequestBody,
        UpdatedBy: RequestBody,
        UpdatedDateTime: RequestBody
    ) = customerApiService.submitProfileUpdateData(
        part,
        CustomerUID,
        CustomerCategory,
        CustomerName,
        SurName,
        MobileNo,
        DoB,
        EmailID,
        MarriageAnniversaryDate,
        FitnessGoal,
        CustomerHeight,
        CustomerWeight,
        CustomerPhotoFilePath,
        CreatedBy,
        CreatedDateTime,
        UpdatedBy,
        UpdatedDateTime
    )

//    suspend fun submitProfileUpdateData(
//        part: MultipartBody.Part?,
//        formDataBody: RequestBody
//    ) = customerApiService.submitProfileUpdateData(part, formDataBody)

    suspend fun getReferenceHistory(userid: String) = customerApiService.getReferenceHistory(userid)
    suspend fun getPurchaseHistory(userid: String, imaxid: String) =
        customerApiService.getPurchaseHistory(userid, imaxid)

    suspend fun getCustomerActivitiesHistory(userid: String, fromDate: String, toDate: String) =
        customerApiService.getCustomerActivitiesHistory(userid, fromDate, toDate)

    suspend fun getEventsList(imaxid: Long, userid: String) =
        customerApiService.getEventsList(userid, imaxid)

    suspend fun getInvoiceList(imaxid: Long, userid: String) =
        customerApiService.getInvoiceList(userid, imaxid)

    suspend fun getEventDetails(eventid: Long, imaxid: Long) =
        customerApiService.getEventsDetails(eventid, imaxid)

    suspend fun getGraphHistory(userid: String, imaxid: String) =
        customerApiService.getGraphHistory(userid, imaxid)

    suspend fun getGraphHistory(userid: String, flag: String, imaxid: String) =
        customerApiService.getGraphHistory(userid, flag, imaxid)

    suspend fun getCaloriesCalculatorDetails(userid: String) =
        customerApiService.getCaloriesCalculatorDetails(userid)


    suspend fun getUserCaloriesCalculatorData(userid: String) =
        customerApiService.getUserCaloriesCalculatorDetails(userid)

    suspend fun getAchievementData(userid: String, lMaximumTransactionID: Long) =
        customerApiService.getAchievementData(userid, lMaximumTransactionID)

    suspend fun getCustomerPrograms(
        iProgramCode: String,
        programname: String,
        lCustomerUID: String,
        lMaximumProgramID: Long
    ) =
        customerApiService.getCustomerPrograms(
            iProgramCode,
            programname,
            lCustomerUID,
            lMaximumProgramID
        )

    suspend fun getCustomerplans(lUserID: String) =
        customerApiService.getCustomerPlans(lUserID)

    suspend fun getReportIssues(mastertype: String, iParentMasterID: Long) =
        customerApiService.getReportIssues(mastertype, iParentMasterID)

    suspend fun getTasksData(mastertype: String, parentid: Long) =
        customerApiService.getTasks(mastertype, parentid)
//
//    suspend fun getTasksData(
//        part: MultipartBody.Part?,
//        formDataBody: RequestBody
//    ) = customerApiService.postTask(part, formDataBody)

//    suspend fun postNewEnquiry(postNewEnquiry: PostNewEnquiry) =
//        customerApiService.postNewEnquiry(postNewEnquiry)

//    suspend fun postSignUp(postNewEnquiry: PostNewEnquiry) =
//        customerApiService.postSignUP(postNewEnquiry)


    suspend fun postOTPVerificationData(mobileno: String, otp: String) =
        customerApiService.postVerifyOTPData(mobileno, otp)

    suspend fun getConfirmedTickets(userid: String, lMaximumTransactionID: Long) =
        customerApiService.getConfirmedTickets(userid, lMaximumTransactionID)


}