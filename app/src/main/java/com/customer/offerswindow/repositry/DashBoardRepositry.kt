package com.customer.offerswindow.repositry

import com.customer.offerswindow.data.api.login.DashBoardHelperImpl
import com.customer.offerswindow.helper.BaseApiResponse
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.CustomerDataResponse
import com.customer.offerswindow.model.dashboard.DashBoardDataResponse
import com.customer.offerswindow.model.offerdetails.OfferDeatilsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DashBoardRepositry @Inject constructor(
    private val dashBoardHelperImpl: DashBoardHelperImpl
) : BaseApiResponse() {


    suspend fun getCustomerData(
        mobileno: String
    ): Flow<NetworkResult<CustomerDataResponse>> {
        return flow {
            emit(safeApiCall { dashBoardHelperImpl.getCustomerData(userid = mobileno) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getDashBoardOffersList(
        lShowroomId: String, lLocationId: String, lServiceId: String
    ): Flow<NetworkResult<DashBoardDataResponse>> {
        return flow {
            emit(safeApiCall {
                dashBoardHelperImpl.getDashBoardOffersList(
                    lShowroomId,
                    lLocationId,
                    lServiceId
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getIndividualOfferDetails(
        lRecordId: String
    ): Flow<NetworkResult<OfferDeatilsResponse>> {
        return flow {
            emit(safeApiCall {
                dashBoardHelperImpl.getIndividualOfferDetails(
                    lRecordId
                )
            })
        }.flowOn(Dispatchers.IO)
    }


}