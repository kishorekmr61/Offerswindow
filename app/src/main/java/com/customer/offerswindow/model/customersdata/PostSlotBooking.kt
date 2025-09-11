package com.customer.offerswindow.model.customersdata

import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.utils.getDateTime

data class PostSlotBooking(
    var BookingDate: String,
    var ShowRoomId: String,
    var LocationId: String,
    var serviceId: String,
    var OfferId: String,
    var slotId: String,
    var customerId: String = AppPreference.read(Constants.USERUID,"") ?:"",
    var createdBy: String =AppPreference.read(Constants.USERUID,"") ?:"",
    var createdDateTime: String = getDateTime(),
    var updatedBy: String= AppPreference.read(Constants.USERUID,"") ?:"",
    var updatedDateTime: String= getDateTime(),
)
