package com.customer.offerswindow.model.dashboard

import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.utils.convertDate
import com.customer.offerswindow.utils.resource.WidgetViewModel

data class BookingsResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<BookingData>?
)

data class BookingData(
    val Rec_ID: String,
    val Showroom_UID: String,
    val Showroom_Name: String,
    val Branch_UID: String,
    val Branch_Address: String,
    val Service_UID: String,
    val Service_Desc: String,
    val Slot_UID: String,
    val Slot_Desc: String,
    val Slot_From_Time: String,
    val Slot_To_Time: String,
    val Booking_Date: String,
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.row_mybookings
    }

    fun getDateTime(): String {
        return convertDate(Booking_Date, Constants.YYYYMMDDTHH, "dd MMM, yyyy").plus(" | ").plus(Slot_Desc)
    }
}
