package com.customer.offerswindow.model.dashboard

import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel

data class CategoriesData(
    var category_img : String,
    var category_desc : String,
    var category_id : String,
    var isselected : Boolean = false,
): WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.catrgoriesrow
    }
}
