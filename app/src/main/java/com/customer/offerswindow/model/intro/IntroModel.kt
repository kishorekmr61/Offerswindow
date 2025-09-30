package com.customer.offerswindow.model.intro

import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel

data class IntroModel(
    var maintitle: String,
    var mainDesc: String,
    var title: String,
    var Desc: String,
    var image: Int,
    var bg: Int,
    var align: Int
) : WidgetViewModel {
    override fun layoutId(): Int {
        if (align == 0) {
            return R.layout.intro_row_right
        } else {
            return R.layout.intro_row
        }
    }
}