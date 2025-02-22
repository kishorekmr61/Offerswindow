package com.customer.offerswindow.model.wallet

data class PostRedemptionApproval(
    var RedemptionRequestID: Long =0L,
    var CustomerUID: Long=0L,
    var RewardPoints: String="",
    var Amount: Double= 0.0,
    var PurposeCode: String = "",
    var RedemptionDate: String="",
    var ApprovalStatus: String="",
    var CreatedBy: String="",
    var CreatedDateTime: String="",
    var UpdatedBy: String="",
    var UpdatedDateTime: String=""
)