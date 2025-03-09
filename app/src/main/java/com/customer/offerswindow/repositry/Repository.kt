package com.customer.offerswindow.repositry

import com.customer.offerswindow.data.api.login.LoginHelperImpl
import com.customer.offerswindow.helper.BaseApiResponse
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.OTPResponse
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.model.TokenResponse
import com.customer.offerswindow.model.UserResponse
import com.customer.offerswindow.model.customersdata.PostSignUp
import com.customer.offerswindow.model.masters.CommonLocationMasterResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.model.masters.HubMaster
import com.customer.offerswindow.model.notification.NotificationResponse
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
        /*mobilenumber: String,
        password: String*/
    ): Flow<NetworkResult<TokenResponse>> {
        return flow {
            emit(safeApiCall { loginHelperImpl.getUserToken(/*mobilenumber, password*/) })
        }.flowOn(Dispatchers.IO)
    }



    suspend fun getCommonLocationMaster(mastertype: String): Flow<NetworkResult<CommonLocationMasterResponse>> {
        return flow {
            emit(safeApiCall { loginHelperImpl.getCommonLocationMasterData(mastertype) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getOtp(
        mobilenumber: String
    ): Flow<NetworkResult<UserResponse>> {
        return flow {
            emit(safeApiCall { loginHelperImpl.getOtp(mobilenumber) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun validateOTP(
        mobilenumber: String,otp : String
    ): Flow<NetworkResult<OTPResponse>> {
        return flow {
            emit(safeApiCall { loginHelperImpl.validateOTP(mobilenumber,otp) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postSignUp(
       postSignUp: PostSignUp
    ): Flow<NetworkResult<StockPurchsasePostingResponse>> {
        return flow {
            emit(safeApiCall { loginHelperImpl.postSignUp(postSignUp)})
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

    suspend fun getCommonMaster(mastertype: String): Flow<NetworkResult<CommonMasterResponse>> {
        return flow {
            emit(safeApiCall { loginHelperImpl.getCommonMasterData(mastertype,"0") })
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getNotifications(lCustomerID: String): Flow<NetworkResult<NotificationResponse>> {
        return flow {
            emit(safeApiCall { loginHelperImpl.getNotifications(lCustomerID) })
        }.flowOn(Dispatchers.IO)
    }

}
