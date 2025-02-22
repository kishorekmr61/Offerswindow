package com.customer.offerswindow.model.wallet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RewardBalanceResponse(
    @Expose
    @SerializedName("Status")
    var Status: Int,
    @Expose
    @SerializedName("Message")
    val Message: String,
    @Expose
    @SerializedName("Data")
    var rewardsHistory: ArrayList<RewardBalance>
)

data class RewardBalance(
    @Expose
    @SerializedName("User_ID")
    val UserID: String,
    @Expose
    @SerializedName("Rewards_Balance")
    val RewardsBalance: String,
    @Expose
    @SerializedName("Rewards_Value")
    val RewardsValue: String,
)
