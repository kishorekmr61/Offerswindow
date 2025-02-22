package com.customer.offerswindow.model.wallet

import android.text.TextUtils
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.utils.resource.WidgetViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class WalletApprovalResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<WalletPendingData>
)

data class WalletPendingData(
    val Rec_ID: Long,
    val Local_Remote_ID: String,
    val Requested_By: String,
    val Requested_Date: String,
    val Transaction_Type: String,
    val Cust_UID: String,
    val Requested_By_Name: String,
    val Amount: String,
    val Approval_Status: String,
    val Approval_Date: String,
    val Approval_By: String,

    ) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.wallet_approval_row
    }

    fun convertToDate(datetime: String?): String? {
        if (!TextUtils.isEmpty(datetime)) {
            val simpleDateFormat = SimpleDateFormat(Constants.YYYYMMDDTHH, Locale.getDefault())
            val formatdate = try {
                SimpleDateFormat(
                    Constants.YYYYMMDDHH,
                    Locale.getDefault()
                ).format(simpleDateFormat.parse(datetime))
            } catch (e: Exception) {
                return datetime
            }
            return formatdate.toString()
        }
        return datetime
    }

}