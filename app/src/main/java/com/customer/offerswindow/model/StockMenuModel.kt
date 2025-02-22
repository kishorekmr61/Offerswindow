package com.customer.offerswindow.model

import android.graphics.drawable.Drawable
import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel

data class StockMenuModel(
    var bgcolor: Int,
    var Image: Drawable?,
    var title: String
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.stock_menu_row
    }
}