package com.customer.offerswindow.model.customersdata

data class PostRewardRedemption(
    var customerId: String,
    var redemptionDate: String,
    var rewardPoints: String,
    var transactionType: String,
    var transactionDescription: String,
    var referenceTransactionId: String,
    var referenceCustomerId: String,
    var createdBy: String,
    var createdDateTime: String,
    var updatedBy: String,
    var updatedDateTime: String,
)
