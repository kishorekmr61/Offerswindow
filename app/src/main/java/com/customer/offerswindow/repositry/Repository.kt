package com.customer.offerswindow.repositry

import com.customer.offerswindow.data.api.login.LoginHelperImpl
import com.customer.offerswindow.helper.BaseApiResponse
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.model.TokenResponse
import com.customer.offerswindow.model.UserResponse
import com.customer.offerswindow.model.masters.CommonLocationMasterResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.model.masters.HubMaster
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


@ActivityRetainedScoped
class Repository @Inject constructor(
    private val loginHelperImpl: LoginHelperImpl
) : BaseApiResponse() {

    suspend fun getToken(
        mobilenumber: String,
        password: String
    ): Flow<NetworkResult<TokenResponse>> {
        return flow {
            emit(safeApiCall { loginHelperImpl.getUserToken(mobilenumber, password) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getHubMaster(mastertype: String): Flow<NetworkResult<List<HubMaster>>> {
        return flow {
            emit(safeApiCall { loginHelperImpl.getHubMasterData(mastertype, "0") })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCommonbMaster(mastertype: String): Flow<NetworkResult<CommonMasterResponse>> {
        return flow {
            emit(safeApiCall { loginHelperImpl.getCommonMasterData(mastertype, "0", "0") })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCommonLocationMaster(mastertype: String): Flow<NetworkResult<CommonLocationMasterResponse>> {
        return flow {
            emit(safeApiCall { loginHelperImpl.getCommonLocationMasterData(mastertype) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun verifyUser(
        mobilenumber: String,
        password: String
    ): Flow<NetworkResult<UserResponse>> {
        return flow {
            emit(safeApiCall { loginHelperImpl.getUserinfo(mobilenumber, password) })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun forgotPassword(
        mobilenumber: String
    ): Flow<NetworkResult<StockPurchsasePostingResponse>> {
        return flow {
            emit(safeApiCall { loginHelperImpl.getForgetPassword(mobilenumber) })
        }.flowOn(Dispatchers.IO)
    }

//    suspend fun resetPassword(
//        postResetPassword: PostResetPassword
//    ): Flow<NetworkResult<StockPurchsasePostingResponse>> {
//        return flow {
//            emit(safeApiCall { loginHelperImpl.postResetPassword(postResetPassword) })
//        }.flowOn(Dispatchers.IO)
//    }

    suspend fun deleteUserAccount(
        Userid: String
    ): Flow<NetworkResult<StockPurchsasePostingResponse>> {
        return flow {
            emit(safeApiCall { loginHelperImpl.deleteUserAccount(Userid) })
        }.flowOn(Dispatchers.IO)
    }

}
