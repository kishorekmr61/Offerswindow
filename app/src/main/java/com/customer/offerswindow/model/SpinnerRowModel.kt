package com.customer.offerswindow.model

import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel

data class SpinnerRowModel(
    var title: String,
    var ischecked: Boolean,
    var showColoursView: Boolean,
    var tintColorCode: String ? = "0",
    var mstCode: String = "",
    var custImagePath :String ="",
    var Redemption_Points: String = "",
    var Redemption_Value: String = "",
) : WidgetViewModel {
    override fun layoutId(): Int {
//        if (showColoursView)
//            return R.layout.spinners_colours
        return R.layout.spinners_resultsrow
    }


}
