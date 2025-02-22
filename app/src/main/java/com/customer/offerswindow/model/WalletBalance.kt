package com.customer.offerswindow.model

import android.graphics.drawable.Drawable
import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel

data class WalletBalance(
    var name: String,
    var type: String,
    var transaction: String,
    var amount: String,
    var transactiondate: String,
    var image: Drawable ?
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.walletbalance_row
    }
}