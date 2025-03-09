package com.customer.offerswindow.data.api.login

import com.customer.offerswindow.data.api.login.apiServices.CustomerApiService
import com.customer.offerswindow.data.api.login.apiServices.MasterApiService
import com.customer.offerswindow.model.PostPhoneNumber
import com.customer.offerswindow.model.customersdata.PostSignUp
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class CustomerHelperImpl @Inject constructor(
    private val customerApiService: CustomerApiService,
    private val masterApiService: MasterApiService
) {
    suspend fun getCustomerList(userid: String, imaxid: Int) =
        customerApiService.getCustomerList(userid, imaxid)

    suspend fun getCustomerDetailsById(userid: String) =
        customerApiService.getCustomerDeatilsByid(userid)



//    suspend fun submitProfileUpdateData(
//        part: MultipartBody.Part?,
//        formDataBody: RequestBody
//    ) = customerApiService.submitProfileUpdateData(part, formDataBody)

//    suspend fun getReferenceHistory(userid: String) = customerApiService.getReferenceHistory(userid)
//    suspend fun getPurchaseHistory(userid: String, imaxid: String) =
//        customerApiService.getPurchaseHistory(userid, imaxid)
//
//    suspend fun getCustomerActivitiesHistory(userid: String, fromDate: String, toDate: String) =
//        customerApiService.getCustomerActivitiesHistory(userid, fromDate, toDate)
//
//    suspend fun getEventsList(imaxid: Long, userid: String) =
//        customerApiService.getEventsList(userid, imaxid)
//
//    suspend fun getInvoiceList(imaxid: Long, userid: String) =
//        customerApiService.getInvoiceList(userid, imaxid)
//
//    suspend fun getEventDetails(eventid: Long, imaxid: Long) =
//        customerApiService.getEventsDetails(eventid, imaxid)
//
//    suspend fun getGraphHistory(userid: String, imaxid: String) =
//        customerApiService.getGraphHistory(userid, imaxid)
//
//    suspend fun getGraphHistory(userid: String, flag: String, imaxid: String) =
//        customerApiService.getGraphHistory(userid, flag, imaxid)
//
//    suspend fun getCaloriesCalculatorDetails(userid: String) =
//        customerApiService.getCaloriesCalculatorDetails(userid)
//
//
//    suspend fun getUserCaloriesCalculatorData(userid: String) =
//        customerApiService.getUserCaloriesCalculatorDetails(userid)
//
//    suspend fun getAchievementData(userid: String, lMaximumTransactionID: Long) =
//        customerApiService.getAchievementData(userid, lMaximumTransactionID)
//
//    suspend fun getCustomerPrograms(
//        iProgramCode: String,
//        programname: String,
//        lCustomerUID: String,
//        lMaximumProgramID: Long
//    ) =
//        customerApiService.getCustomerPrograms(
//            iProgramCode,
//            programname,
//            lCustomerUID,
//            lMaximumProgramID
//        )

//    suspend fun getCustomerplans(lUserID: String) =
//        customerApiService.getCustomerPlans(lUserID)

//    suspend fun getReportIssues(mastertype: String, iParentMasterID: Long) =
//        customerApiService.getReportIssues(mastertype, iParentMasterID)
//
//    suspend fun getTasksData(mastertype: String, parentid: Long) =
//        customerApiService.getTasks(mastertype, parentid)
////
//    suspend fun getTasksData(
//        part: MultipartBody.Part?,
//        formDataBody: RequestBody
//    ) = customerApiService.postTask(part, formDataBody)

    suspend fun postSignUp(postSignUp: PostSignUp) =
        customerApiService.postSignUp(postSignUp)


    suspend fun postOTPVerificationData(mobileno: String, otp: String) =
        customerApiService.postVerifyOTPData(mobileno, otp)

    suspend fun postOTPData(postPhoneNumber: PostPhoneNumber) =
        customerApiService.postOTPData(postPhoneNumber)

    //    suspend fun getConfirmedTickets(userid: String, lMaximumTransactionID: Long) =
//        customerApiService.getConfirmedTickets(userid, lMaximumTransactionID)
    suspend fun getCommonMasterData(mastertype: String, parentid: String) =
        masterApiService.getCommonMaster(mastertype, parentid)


}