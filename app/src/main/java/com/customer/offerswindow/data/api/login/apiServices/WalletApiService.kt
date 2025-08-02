package com.customer.offerswindow.data.api.login.apiServices

import com.customer.offerswindow.model.OfferWindowCommonResponse
import com.customer.offerswindow.model.wallet.PostRedemptionApproval
import com.customer.offerswindow.model.wallet.RedemptionRequestBody
import com.customer.offerswindow.model.wallet.RewardBalanceResponse
import com.customer.offerswindow.model.wallet.RewardPointsHistoryResponse
import com.customer.offerswindow.model.wallet.WalletBalanceResponse
import com.customer.offerswindow.model.wallet.WalletHistoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WalletApiService {
    @GET("ShowRoomOffers/GetRewards??")
    suspend fun getRewardsPointHistoryBalance(
        @Query("lCustomerID") lUserUID: String,
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
//
//    @GET("Transactions/RewardPointRedemptionDetails?")
//    suspend fun getRedemptionApprovalHistory(
//        @Query("lUserUID") lUserUID: String,
//        @Query("lMaximumTransactionID") lMaximumTransactionID: Int
//    ): Response<RedemptionApprovalResponse>
//
//    @GET("Transactions/WalletPendingApprovalRequestDetails?")
//    suspend fun getWalletApprovalHistory(
//        @Query("lUserID") lUserUID: String,
//        @Query("lMaximumTransactionID") lMaximumTransactionID: Long
//    ): Response<WalletApprovalResponse>
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
    ): Response<OfferWindowCommonResponse>

    @POST("ShowRoomOffers/PostRewardRedemption")
    suspend fun postRedemptionRequestData(
        @Body redemptionRequestBody: RedemptionRequestBody
    ): Response<OfferWindowCommonResponse>
}