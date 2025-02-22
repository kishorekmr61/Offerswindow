package com.customer.offerswindow.model.wallet

import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.utils.getDateTime
import io.ktor.util.date.getTimeMillis

data class RedemptionRequestBody(
    var RequestID: String = getTimeMillis().toString(),
    var CustomerUID: String= AppPreference.read(Constants.USERUID, "") ?: "0",
    var RewardPoints: String="",
    var PurposeCode: String="",
    var RedemptionDate: String= getDateTime(),
    var CreatedBy: String = AppPreference.read(Constants.USERUID, "") ?: "0",
    var CreatedDateTime: String = getDateTime()

)