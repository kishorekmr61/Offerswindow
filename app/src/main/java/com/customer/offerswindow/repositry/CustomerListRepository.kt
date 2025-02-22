package com.customer.offerswindow.repositry

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.customer.offerswindow.data.api.login.CustomerHelperImpl
import com.customer.offerswindow.helper.BaseApiResponse
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.ActivitiesResponse
import com.customer.offerswindow.model.CustomerCheckUpSingleDataRes
import com.customer.offerswindow.model.CustomerListResponse
import com.customer.offerswindow.model.CustomerReferenceHistoryListResponse
import com.customer.offerswindow.model.EventsList
import com.customer.offerswindow.model.PostNewEnquiry
import com.customer.offerswindow.model.PostPhoneNumber
import com.customer.offerswindow.model.PurchaseHistorySingleObject
import com.customer.offerswindow.model.customersdata.CalorieCaliculationdetailsResponse
import com.customer.offerswindow.model.customersdata.CalorieDataPost
import com.customer.offerswindow.model.customersdata.CalorieDataPostingResponse
import com.customer.offerswindow.model.customersdata.CustomerCheckUpRequest
import com.customer.offerswindow.model.customersdata.GraphResponse
import com.customer.offerswindow.model.customersdata.ProfileUpdateResponse
import com.customer.offerswindow.model.dashboard.postCustomerWeight
import com.customer.offerswindow.model.events.ConfirmedTickets
import com.customer.offerswindow.model.events.EventTicketDetailsResponse
import com.customer.offerswindow.model.events.EventsResponse
import com.customer.offerswindow.model.events.PostTicketConfirmation
import com.customer.offerswindow.model.invoice.Invoicedata
import com.customer.offerswindow.model.masters.CommonPlanMst
import com.customer.offerswindow.model.masters.TaskResponse
import com.customer.offerswindow.model.myprogram.MyProgramResponse
import com.customer.offerswindow.model.myprogram.MyProgramdata
import com.customer.offerswindow.model.report.PostAnIssue
import com.customer.offerswindow.model.report.ReportData
import com.customer.offerswindow.model.stock.StockPurchsasePostingResponse
import com.customer.offerswindow.paging.ConfirmedTicketsPagenation
import com.customer.offerswindow.paging.CustomerInvoicePagenation
import com.customer.offerswindow.paging.CustomerProgramsPagenation
import com.customer.offerswindow.paging.CustomerPurchaseHistoryPagenation
import com.customer.offerswindow.paging.EventsPagenation
import com.customer.offerswindow.paging.ReportIssuesPagenation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class CustomerListRepository @Inject constructor(
    private val customerHelperImpl: CustomerHelperImpl
) : BaseApiResponse() {

    suspend fun getCustomerList(
        Userid: String, imaxid: Int
    ): Flow<NetworkResult<CustomerListResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.getCustomerList(userid = Userid, imaxid) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun submitCustomerCheckUp(customerCheckUpRequest: CustomerCheckUpRequest): Flow<NetworkResult<ProfileUpdateResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.submitCustomerCheckUp(customerCheckUpRequest) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCustomerCheckUp(
        userID: String
    ): Flow<NetworkResult<CustomerCheckUpSingleDataRes>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.getCustomerCheckUp(userID) })
        }.flowOn(Dispatchers.IO)
    }

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
    ): Flow<NetworkResult<ProfileUpdateResponse>> {
        return flow {
            emit(safeApiCall {
                customerHelperImpl.submitProfileUpdateData(
                    part,
                    CustomerUID, CustomerCategory,
                    CustomerName,
                    SurName, MobileNo,
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
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun submitProfileUpdateData(
        part: MultipartBody.Part?,
        formDataBody: RequestBody
    ): Flow<NetworkResult<ProfileUpdateResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.submitProfileUpdateData(part, formDataBody) })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getReferenceHistory(
        Userid: String
    ): Flow<NetworkResult<CustomerReferenceHistoryListResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.getReferenceHistory(userid = Userid) })
        }.flowOn(Dispatchers.IO)
    }

    fun getPurchaseHistory(Userid: String): Flow<PagingData<PurchaseHistorySingleObject>> =
        Pager(
            PagingConfig(1),
            pagingSourceFactory = {
                CustomerPurchaseHistoryPagenation(
                    Userid,
                    customerHelperImpl
                )
            }
        ).flow.flowOn(Dispatchers.IO)


    fun getEventsHistory(): Flow<PagingData<EventsList>> =
        Pager(
            PagingConfig(20),
            pagingSourceFactory = {
                EventsPagenation(
                    customerHelperImpl
                )
            }
        ).flow.flowOn(Dispatchers.IO)

    fun getInvoicesHistory(): Flow<PagingData<Invoicedata>> =
        Pager(
            PagingConfig(1),
            pagingSourceFactory = {
                CustomerInvoicePagenation(
                    customerHelperImpl
                )
            }
        ).flow.flowOn(Dispatchers.IO)

    fun getDashboardEventsHistory(
        maxid: Long,
        userid: String
    ): Flow<NetworkResult<EventsResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.getEventsList(maxid, userid) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getEventDetails(
        eventid: Long, imaxid: Long
    ): Flow<NetworkResult<EventsResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.getEventDetails(eventid, imaxid) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCustomerActivitiesHistory(
        userid: String,
        fromDate: String,
        toDate: String
    ): Flow<NetworkResult<ActivitiesResponse>> {
        return flow {
            emit(safeApiCall {
                customerHelperImpl.getCustomerActivitiesHistory(
                    userid,
                    fromDate,
                    toDate
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    fun getCustomerPrograms(
        sProgramName: String
    ): Flow<PagingData<MyProgramdata>> =
        Pager(
            PagingConfig(1),
            pagingSourceFactory = {
                CustomerProgramsPagenation(
                    customerHelperImpl, sProgramName
                )
            }
        ).flow.flowOn(Dispatchers.IO)

      fun getCustomerPrograms(sProgramName: String,lCustomerUID: String
      ): Flow<NetworkResult<MyProgramResponse>> {
          return flow {
              emit(safeApiCall { customerHelperImpl.getCustomerPrograms("0", sProgramName, lCustomerUID,0) })
          }.flowOn(Dispatchers.IO)
      }


    suspend fun getCustomerPlans(
        Userid: String
    ): Flow<NetworkResult<CommonPlanMst>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.getCustomerplans( Userid) })
        }.flowOn(Dispatchers.IO)
    }

    fun getReportedIssues(
    ): Flow<PagingData<ReportData>> =
        Pager(
            PagingConfig(1),
            pagingSourceFactory = {
                ReportIssuesPagenation(
                    customerHelperImpl
                )
            }
        ).flow.flowOn(Dispatchers.IO)


    suspend fun getGraphDetails(
        userid: String,
        imaxid: String
    ): Flow<NetworkResult<GraphResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.getGraphHistory(userid, imaxid) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getGraphDetails(
        userid: String,
        flag: String,
        imaxid: String
    ): Flow<NetworkResult<GraphResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.getGraphHistory(userid, flag, imaxid) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCaloriesCalculatorDetails(userid: String): Flow<NetworkResult<CalorieCaliculationdetailsResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.getCaloriesCalculatorDetails(userid) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCaloriesCalculatorData(calorieDataPost: CalorieDataPost): Flow<NetworkResult<CalorieDataPostingResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.getCaloriesCalculatorData(calorieDataPost) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getUserCaloriesCalculatorData(userid: String): Flow<NetworkResult<CalorieDataPostingResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.getUserCaloriesCalculatorData(userid) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCustomerData(Userid: String): Flow<NetworkResult<CustomerListResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.getCustomerDetailsById(userid = Userid) })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getTasksMaster(mastertype: String): Flow<NetworkResult<TaskResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.getTasksData(mastertype, 0) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postTaskData(
        part: MultipartBody.Part?,
        formDataBody: RequestBody
    ): Flow<NetworkResult<StockPurchsasePostingResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.getTasksData(part, formDataBody) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postAddLeadData(postNewEnquiry: PostNewEnquiry): Flow<NetworkResult<StockPurchsasePostingResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.postNewEnquiry(postNewEnquiry) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postSignUp(postNewEnquiry: PostNewEnquiry): Flow<NetworkResult<StockPurchsasePostingResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.postSignUp(postNewEnquiry) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postTicketConfirmationData(postTicketConfirmation: PostTicketConfirmation): Flow<NetworkResult<EventTicketDetailsResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.postTicketConfirmation(postTicketConfirmation) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postCustomerWeightData(postcustomerweight: postCustomerWeight): Flow<NetworkResult<StockPurchsasePostingResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.postCustomeWeightData(postcustomerweight) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postissueData(part: MultipartBody.Part?,
                              formDataBody: RequestBody): Flow<NetworkResult<StockPurchsasePostingResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.postissueData(part,formDataBody) })
        }.flowOn(Dispatchers.IO)
    }
    suspend fun postOTPData(postPhoneNumber: PostPhoneNumber): Flow<NetworkResult<StockPurchsasePostingResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.postOTPData(postPhoneNumber) })
        }.flowOn(Dispatchers.IO)
    }
    suspend fun postOTPVerificationData(mobileno: String, otp: String): Flow<NetworkResult<StockPurchsasePostingResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.postOTPVerificationData(mobileno , otp ) })
        }.flowOn(Dispatchers.IO)
    }

    fun getConfirmedTickets(
    ): Flow<PagingData<ConfirmedTickets>> =
        Pager(
            PagingConfig(1),
            pagingSourceFactory = {
                ConfirmedTicketsPagenation(
                    customerHelperImpl
                )
            }
        ).flow.flowOn(Dispatchers.IO)

}