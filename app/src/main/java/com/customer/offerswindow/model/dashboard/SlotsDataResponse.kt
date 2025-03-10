package com.customer.offerswindow.model.dashboard

import android.graphics.Color
import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel

data class SlotsDataResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<SlotsData>?
)

data class SlotsData(
    val Slot_UID: String,
    val Slot_Desc: String,
    val From_Time: String,
    val To_Time: String,
    val Slot_Colour: String,
    var isselected: Boolean = false,
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.row_slotimings
    }


    fun getColor(colorname: String): Int {
        if (colorname == "Green") {
            return Color.parseColor("#ff0f9d58")//R.color.car_green_500
        } else if (colorname == "Yellow") {
            return Color.parseColor("#FBBA18")//R.color.color_FBBA18
        }
        if (isselected) {
            return Color.parseColor("#4C1012")
        }
        return Color.parseColor("#CDCDCD")
    }


}
