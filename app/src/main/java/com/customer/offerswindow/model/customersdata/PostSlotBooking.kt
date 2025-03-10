package com.customer.offerswindow.model.customersdata

import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.utils.getDateTime

data class PostSlotBooking(
    var bookingDate: String,
    var showRoomId: String,
    var branchId: String,
    var serviceId: String,
    var slotId: String,
    var customerId: String = AppPreference.read(Constants.USERUID,"") ?:"",
    var createdBy: String =AppPreference.read(Constants.USERUID,"") ?:"",
    var createdDateTime: String = getDateTime(),
    var updatedBy: String= AppPreference.read(Constants.USERUID,"") ?:"",
    var updatedDateTime: String= getDateTime(),
)
