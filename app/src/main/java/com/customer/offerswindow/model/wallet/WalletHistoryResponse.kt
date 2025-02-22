package com.customer.offerswindow.model.wallet

import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel

data class WalletHistoryResponse(

    var Status: Int,
    val Message: String,
    var Data: TableData

)

data class TableData(

    var Table: ArrayList<Table> = arrayListOf(),
    var Table1: ArrayList<Table1> = arrayListOf()
)

data class Table(

    var Wallet_Balance: String? = "0"

)

data class Table1(

    var Rec_ID: Int?,
    var Trans_Type: String?,
    var Pay_Type: String?,
    var Transaction_Date: String?,
    var Received_From: String? = "",
    var Cust_Name: String?,
    var Received_Amount: String = "0.0",
    var Created_By: String?,
    var Created_Date: String?,

    ) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.walletbalance_row
    }
}
