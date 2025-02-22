package com.customer.offerswindow.repositry

import com.customer.offerswindow.data.api.login.DashBoardHelperImpl
import com.customer.offerswindow.helper.BaseApiResponse
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.CustomerListResponse
import com.customer.offerswindow.model.dashboard.DashBoardBannerData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DashBoardRepositry @Inject constructor(
    private val dashBoardHelperImpl: DashBoardHelperImpl
) : BaseApiResponse() {


    suspend fun getCustomerData(
        Userid: String
    ): Flow<NetworkResult<CustomerListResponse>> {
        return flow {
            emit(safeApiCall { dashBoardHelperImpl.getCustomerData(userid = Userid) })
        }.flowOn(Dispatchers.IO)
    }



    suspend fun getBannersData(
        Userid: String
    ): Flow<NetworkResult<DashBoardBannerData>> {
        return flow {
            emit(safeApiCall { dashBoardHelperImpl.getBannerdata(userid = Userid, "0") })
        }.flowOn(Dispatchers.IO)
    }

}