package com.customer.offerswindow.repositry

import com.customer.offerswindow.data.api.login.CustomerHelperImpl
import com.customer.offerswindow.helper.BaseApiResponse
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.PostPhoneNumber
import com.customer.offerswindow.model.OfferWindowCommonResponse
import com.customer.offerswindow.model.PostOfferWindowCommonResponse
import com.customer.offerswindow.model.customersdata.PostSignUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CustomerListRepository @Inject constructor(
    private val customerHelperImpl: CustomerHelperImpl
) : BaseApiResponse() {


    suspend fun postSignUp(postSignUp: PostSignUp): Flow<NetworkResult<PostOfferWindowCommonResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.postSignUp(postSignUp) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postOTPData(postPhoneNumber: PostPhoneNumber): Flow<NetworkResult<OfferWindowCommonResponse>> {
        return flow {
            emit(safeApiCall { customerHelperImpl.postOTPData(postPhoneNumber) })
        }.flowOn(Dispatchers.IO)
    }

}