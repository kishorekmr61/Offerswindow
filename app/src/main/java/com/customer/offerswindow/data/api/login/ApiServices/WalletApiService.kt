package com.customer.offerswindow.data.api.login.ApiServices

import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.model.wallet.*
import com.customer.offerswindow.model.wallet.RewardPointsHistoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WalletApiService {
    @GET("Transactions/RewardsHistory?")
    suspend fun getRewardsPointHistoryBalance(
        @Query("lUserUID") lUserUID: String,
        @Query("lMaximumTransactionID") lMaximumTransactionID: Int
    ): Response<RewardPointsHistoryResponse>

    @GET("Transactions/RewardsBalance?")
    suspend fun getRewardsBalance(
        @Query("lUserID") lUserUID: String
    ): Response<RewardBalanceResponse>

    @GET("Transactions/WalletHistory?")
    suspend fun getWalletBalance(
        @Query("lUserID") lUserUID: String
    ): Response<WalletBalanceResponse>

    @GET("Transactions/WalletHistory?")
    suspend fun getWalletHistory(
        @Query("lUserUID") lUserUID: String,
        @Query("lMaximumTransactionID") lMaximumTransactionID: Int
    ): Response<WalletHistoryResponse>

    @GET("Transactions/RewardPointRedemptionDetails?")
    suspend fun getRedemptionApprovalHistory(
        @Query("lUserUID") lUserUID: String,
        @Query("lMaximumTransactionID") lMaximumTransactionID: Int
    ): Response<RedemptionApprovalResponse>

    @GET("Transactions/WalletPendingApprovalRequestDetails?")
    suspend fun getWalletApprovalHistory(
        @Query("lUserID") lUserUID: String,
        @Query("lMaximumTransactionID") lMaximumTransactionID: Long
    ): Response<WalletApprovalResponse>
//
//    @POST("Transactions/WalletTransfer?")
//    suspend fun postWalletTransferData(
//        @Body postWalletTransfer: PostWalletTransfer
//    ): Response<PostFollowUpResponse>
//
//    @POST("Transactions/WalletApproval")
//    suspend fun postWalletTApprovalData(
//        @Body postWalletTransfersApprovals: PostWalletTransfersApprovals
//    ): Response<PostFollowUpResponse>

    @POST("Transactions/RewardRedemptionApproval?")
    suspend fun postRedemptionApprovalData(
        @Body postRedemptionApproval: PostRedemptionApproval
    ): Response<StockPurchsasePostingResponse>

    @POST("CustomerTransactions/RewardRedemptionRequest")
    suspend fun postRedemptionRequestData(
        @Body redemptionRequestBody: RedemptionRequestBody
    ): Response<StockPurchsasePostingResponse>
}