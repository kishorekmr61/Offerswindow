package com.customer.offerswindow.model.wallet

data class PostWalletTransfer(
    var FromCustomerUID: String,
    var ToCustomerUID: String,
    var TransactionDate: String,
    var TransactionType: String,
    var Amount: Double,
    var Remarks: String,
    var CreatedBy: String,
    var CreatedDateTime: String,
    var UpdatedBy: String,
    var UpdatedDateTime: String,
)
