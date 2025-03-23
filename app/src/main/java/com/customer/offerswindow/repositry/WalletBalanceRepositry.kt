package com.customer.offerswindow.repositry

import com.customer.offerswindow.data.api.login.WalletHelperImpl
import com.customer.offerswindow.helper.BaseApiResponse
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.model.wallet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WalletBalanceRepositry @Inject constructor(
    private val walletHelperImpl: WalletHelperImpl
) : BaseApiResponse() {


    suspend fun getRewardsHistoryData(
        userid: String, imaxid: Int
    ): Flow<NetworkResult<RewardPointsHistoryResponse>> {
        return flow {
            emit(safeApiCall { walletHelperImpl.getRewardsHistoryData(userid, imaxid) })
        }.flowOn(Dispatchers.IO)
    }



    suspend fun postRedemptionApprovalData(
        postRedemptionApproval: PostRedemptionApproval
    ): Flow<NetworkResult<StockPurchsasePostingResponse>> {
        return flow {
            emit(safeApiCall { walletHelperImpl.postRedemptionApprovalData(postRedemptionApproval) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postRedemptionRequestData(
        redemptionRequestBody: RedemptionRequestBody
    ): Flow<NetworkResult<StockPurchsasePostingResponse>> {
        return flow {
            emit(safeApiCall { walletHelperImpl.postRedemptionRequestData(redemptionRequestBody) })
        }.flowOn(Dispatchers.IO)
    }
//    suspend fun postWalletTransferApprovalData(
//        postWalletTransfersApprovals: PostWalletTransfersApprovals
//    ): Flow<NetworkResult<PostFollowUpResponse>> {
//        return flow {
//            emit(safeApiCall { walletHelperImpl.postWalletTransferApprovalData(postWalletTransfersApprovals) })
//        }.flowOn(Dispatchers.IO)
//    }
}