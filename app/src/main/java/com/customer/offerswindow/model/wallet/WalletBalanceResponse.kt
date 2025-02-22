package com.customer.offerswindow.model.wallet

data class WalletBalanceResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<Data>
)

data class Data(
    var User_ID: Int,
    var Wallet_Balance: Double,
    var Wallet_Value: Double
)
