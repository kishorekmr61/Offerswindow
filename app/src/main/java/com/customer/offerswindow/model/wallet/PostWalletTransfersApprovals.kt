package com.customer.offerswindow.model.wallet

import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.utils.getDateTime

data class PostWalletTransfersApprovals(
    var LocalRemoteID: String = "",
    var RequestedBy: String = "",
    var CustomerUID: String = "",
    var RequestedDateTime: String = "",
    var TransactionType: String = "",
    var Amount: String = "",
    var ApprovalStatus: String = "",
    var ApprovalDoneBy: String = AppPreference.read(Constants.USERUID, "") ?: "0",
    var ApprovalDoneDateTime: String = getDateTime(),
    var CreatedBy: String = AppPreference.read(Constants.USERUID, "") ?: "0",
    var CreatedDateTime: String = getDateTime()
)
