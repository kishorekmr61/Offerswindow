package com.customer.offerswindow.model.dashboard

import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel

data class FilterData(
    var filtercategory_desc: String,
    var filtercode: String,
    var filetrselection: Boolean = false,
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.filters
    }
}
