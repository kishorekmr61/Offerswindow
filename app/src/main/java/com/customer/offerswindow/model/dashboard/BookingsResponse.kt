package com.customer.offerswindow.model.dashboard

import android.annotation.SuppressLint
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.utils.convertDate
import com.customer.offerswindow.utils.resource.WidgetViewModel
import java.text.SimpleDateFormat
import java.util.Date


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
    val Offer_UID: String,
    val Offer_Type_Code: String,
    val Offer_Type_Desc: String,
    val Offer_Details: String,
    val Slot_UID: String,
    val Slot_Desc: String,
    val Slot_From_Time: String,
    val Slot_To_Time: String,
    val Booking_Date: String,
    val No_Of_Guests: String = "0",
    val Website_link: String,
    val Display_Date: String,
    val Effective_Date: String,
    val End_Date: String,
    val Offer_Status: String,
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.row_mybookings
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateTime(): String {
        var goal = ""
        if (!Booking_Date.isNullOrEmpty()) {
            val inFormat = SimpleDateFormat(Constants.YYYYMMDDTHH)
            val date: Date? = inFormat.parse(Booking_Date)
            val outFormat = SimpleDateFormat("EEE")
            goal = date?.let { outFormat.format(it) }.toString()
        }
        return "$goal, " + convertDate(
            Booking_Date,
            Constants.YYYYMMDDTHH,
            "dd MMM, yyyy"
        ) + " | " + Slot_Desc + " | " +
                "$No_Of_Guests People"

    }
}
