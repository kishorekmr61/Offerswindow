package com.customer.offerswindow.model.customersdata

import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.utils.getDateTime

data class PostuserSearch(
    var userId: String,
    var showRoomId: String,
    var branchId: String,
    var serviceId: String,
    var offerTypeId: String,
    var createdBy: String = AppPreference.read(Constants.MOBILENO, "") ?: "",
    var createdDateTime: String = getDateTime(),
    var updatedBy: String = AppPreference.read(Constants.MOBILENO, "") ?: "",
    var updatedDateTime: String = getDateTime(),
)