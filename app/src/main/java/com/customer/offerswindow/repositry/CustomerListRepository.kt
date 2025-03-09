package com.customer.offerswindow.repositry

import com.customer.offerswindow.data.api.login.CustomerHelperImpl
import com.customer.offerswindow.helper.BaseApiResponse
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.PostPhoneNumber
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.model.customersdata.PostSignUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CustomerListRepository @Inject constructor(
    private val customerHelperImpl: CustomerHelperImpl
) : BaseApiResponse() {


//    suspend fun submitProfileUpdateData(
//        part: MultipartBody.Part,
//        CustomerUID: RequestBody,
//        CustomerCategory: RequestBody,
//        CustomerName: RequestBody,
//        SurName: RequestBody,
//        MobileNo: RequestBody,
//        DoB: RequestBody,
//        EmailID: RequestBody,
//        MarriageAnniversaryDate: RequestBody,
//        FitnessGoal: RequestBody,
//        CustomerHeight: RequestBody,
//        CustomerWeight: RequestBody,
//        CustomerPhotoFilePath: RequestBody,
//        CreatedBy: RequestBody,
//        CreatedDateTime: RequestBody,
//        UpdatedBy: RequestBody,
//        UpdatedDateTime: RequestBody
//    ): Flow<NetworkResult<ProfileUpdateResponse>> {
//        return flow {
//            emit(safeApiCall {
//                customerHelperImpl.submitProfileUpdateData(
//                    part,
//                    CustomerUID, CustomerCategory,
//                    CustomerName,
//                    SurName, MobileNo,
//                    DoB,
//                    EmailID,
//                    MarriageAnniversaryDate,
//                    FitnessGoal,
//                    CustomerHeight,
//                    CustomerWeight,
//                    CustomerPhotoFilePath,
//                    CreatedBy,
//                    CreatedDateTime,
//                    UpdatedBy,
//                    UpdatedDateTime
//                )
//            })
//        }.flowOn(Dispatchers.IO)
//    }

    //    suspend fun submitProfileUpdateData(
//        part: MultipartBody.Part?,
//        formDataBody: RequestBody
//    ): Flow<NetworkResult<ProfileUpdateResponse>> {
//        return flow {
//            emit(safeApiCall { customerHelperImpl.submitProfileUpdateData(part, formDataBody) })
//        }.flowOn(Dispatchers.IO)
//    }
    suspend fun postSignUp(postSignUp: PostSignUp): Flow<NetworkResult<StockPurchsasePostingResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.postSignUp(postSignUp) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postOTPData(postPhoneNumber: PostPhoneNumber): Flow<NetworkResult<StockPurchsasePostingResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.postOTPData(postPhoneNumber) })
        }.flowOn(Dispatchers.IO)
    }

}