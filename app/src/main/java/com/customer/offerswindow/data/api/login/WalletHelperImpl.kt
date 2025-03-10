package com.customer.offerswindow.data.api.login

import com.customer.offerswindow.data.api.login.apiServices.WalletApiService
import com.customer.offerswindow.model.wallet.PostRedemptionApproval
import com.customer.offerswindow.model.wallet.RedemptionRequestBody
import javax.inject.Inject

class WalletHelperImpl @Inject constructor(private val walletApiService: WalletApiService) {

    suspend fun getWalletHistoryData(lUserUID: String, lMaximumTransactionID: Int) =
        walletApiService.getWalletHistory(lUserUID, lMaximumTransactionID)

    suspend fun getWalletBalnceData(lUserUID: String) = walletApiService.getWalletBalance(lUserUID)

    suspend fun getRewardsHistoryData(lUserUID: String, lMaximumTransactionID: Int) =
        walletApiService.getRewardsPointHistoryBalance(lUserUID, lMaximumTransactionID)

    suspend fun getRewardBalanceData(lUserUID: String) =
        walletApiService.getRewardsBalance(lUserUID)

    suspend fun postRedemptionApprovalData(postRedemptionApproval: PostRedemptionApproval) =
        walletApiService.postRedemptionApprovalData(postRedemptionApproval)

    suspend fun postRedemptionRequestData(redemptionRequestBody: RedemptionRequestBody) =
        walletApiService.postRedemptionRequestData(redemptionRequestBody)

//    suspend fun postWalletTransferData(postWalletTransfer: PostWalletTransfer) =
//        walletApiService.postWalletTransferData(postWalletTransfer)
//
//    suspend fun postWalletTransferApprovalData(postWalletTransfersApprovals: PostWalletTransfersApprovals) =
//        walletApiService.postWalletTApprovalData(postWalletTransfersApprovals)
}