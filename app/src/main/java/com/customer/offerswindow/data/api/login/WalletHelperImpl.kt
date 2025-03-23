package com.customer.offerswindow.data.api.login

import com.customer.offerswindow.data.api.login.apiServices.WalletApiService
import com.customer.offerswindow.model.wallet.PostRedemptionApproval
import com.customer.offerswindow.model.wallet.RedemptionRequestBody
import javax.inject.Inject

class WalletHelperImpl @Inject constructor(private val walletApiService: WalletApiService) {

    suspend fun getRewardsHistoryData(lUserUID: String, lMaximumTransactionID: Int) =
        walletApiService.getRewardsPointHistoryBalance(lUserUID, lMaximumTransactionID)


    suspend fun postRedemptionApprovalData(postRedemptionApproval: PostRedemptionApproval) =
        walletApiService.postRedemptionApprovalData(postRedemptionApproval)

    suspend fun postRedemptionRequestData(redemptionRequestBody: RedemptionRequestBody) =
        walletApiService.postRedemptionRequestData(redemptionRequestBody)

}