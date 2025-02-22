package com.customer.offerswindow.model.wallet

import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel

data class RewardPointsHistoryResponse(
    var Status: Int,
    val Message: String,
    var Data: RewardsData
)

data class RewardsData(
    var Table: ArrayList<RewardTable> = arrayListOf(),
    var Table1: ArrayList<RewardHistory> = arrayListOf()
)

data class RewardTable(
    var Rewards_Balance: String? = "0"
)


data class RewardHistory(
    val Rec_ID: String,
    val Cust_UID: String,
    val Cust_Name: String,
    val Transaction_Date: String,
    val No_of_Reward_points: String,
    val Trans_Type: String,
    val Description: String,

    ) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.rewardshistory_row
    }
}

