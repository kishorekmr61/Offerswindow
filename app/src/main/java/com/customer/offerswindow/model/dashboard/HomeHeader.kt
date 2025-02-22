package com.customer.offerswindow.model.dashboard

import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel

data class HomeHeader(
    var label: String,
    var image: Int
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.coach_layout
    }
}