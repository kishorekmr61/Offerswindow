package com.customer.offerswindow.model.customersdata

import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.utils.getDateTime

data class PostOfferBooking(
    var BookingDate: String = "",
    var ShowRoomId: String = "",
    var LocationId: String = "",
    var ServiceId: String = "",
    var OfferId: String = "",
    var CustomerId: String = AppPreference.read(Constants.USERUID,"") ?:"",
    var CreatedBy: String = AppPreference.read(Constants.USERUID, "") ?: "",
    var CreatedDateTime: String = getDateTime(),
    var UpdatedBy: String = AppPreference.read(Constants.USERUID, "") ?: "",
    var UpdatedDateTime: String = getDateTime(),
)
