package com.customer.offerswindow.model.customersdata

import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.utils.getDateTime

data class PostUserIntrest(
    val ShowRoomId : String,
    val BranchId : String,
    val OfferId : String,
    val OptionSelected : String,
    val CustomerId : String,
    val CreatedBy : String =AppPreference.read(Constants.USERUID, "") ?: "0",
    val CreatedDateTime : String = getDateTime(),
    val UpdatedBy : String = AppPreference.read(Constants.USERUID, "") ?: "0",
    val UpdatedDateTime : String = getDateTime()
)
