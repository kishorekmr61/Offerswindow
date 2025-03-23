package com.customer.offerswindow.model.wallet

import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.utils.getDateTime
import io.ktor.util.date.getTimeMillis

data class RedemptionRequestBody(
    var RequestID: String = getTimeMillis().toString(),
    var CustomerId: String = AppPreference.read(Constants.USERUID, "") ?: "0",
    var RewardPoints: String = "",
    var TransactionType: String = "",
    var RedemptionValue: String = "",
    var AccountNo: String = "",
    var CreatedBy: String = AppPreference.read(Constants.USERUID, "") ?: "0",
    var CreatedDateTime: String = getDateTime()

)