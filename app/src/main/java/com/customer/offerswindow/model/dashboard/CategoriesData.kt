package com.customer.offerswindow.model.dashboard

import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel

data class CategoriesData(
    var iscatefory: Boolean = false,
    var category_img: String,
    var category_desc: String,
    var category_id: String,
    var isselected: Boolean = false,
) : WidgetViewModel {
    override fun layoutId(): Int {
        if (iscatefory) {
            return R.layout.catrgoriesrow_main
        }
        return R.layout.catrgoriesrow
    }
}
