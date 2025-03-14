package com.customer.offerswindow.ui.slotbooking

import java.text.SimpleDateFormat
import java.util.*

data class CalendarDateModel(var data: Date, var isSelected: Boolean = false, var isenable : Boolean =false) {

    val calendarDay: String
        get() = SimpleDateFormat("EE", Locale.getDefault()).format(data)

    val calendarDate: String
        get() {
            val cal = Calendar.getInstance()
            cal.time = data
            return cal[Calendar.DAY_OF_MONTH].toString()
        }
}
