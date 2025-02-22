package com.customer.offerswindow.model.wallet

import android.graphics.Color
import com.customer.offerswindow.R
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.customer.offerswindow.utils.resource.WidgetViewModel
import java.util.*

data class RedemptionApprovalResponse(
    @Expose
    @SerializedName("Status")
    var Status: Int,
    @Expose
    @SerializedName("Message")
    val Message: String,
    @Expose
    @SerializedName("Data")
    var resdemptionapproval: ArrayList<RedemptionApproval>
)

data class RedemptionApproval(
    @Expose
    @SerializedName("Rec_ID")
    val Rec_ID: String,
    @Expose
    @SerializedName("Redemption_Req_ID")
    val Redemption_Req_ID: String,
    @Expose
    @SerializedName("Cust_UID")
    val Cust_UID: String,
    @Expose
    @SerializedName("Cust_Name")
    val Cust_Name: String,
    @Expose
    @SerializedName("Redemption_Date")
    val Redemption_Date: String,
    @Expose
    @SerializedName("Redemption_Purpose_Code")
    val Redemption_Purpose_Code: String,
    @Expose
    @SerializedName("Redemption_Purpose_Desc")
    val Redemption_Purpose_Desc: String,
    @Expose
    @SerializedName("Reward_Points")
    val Reward_Points: String,
    @Expose
    @SerializedName("Redemption_Status")
    val Redemption_Status: String,

    ) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.redemptionapproval_row
    }

    fun setTextColor(status: String): Int {

        if (status.equals("Approved")) {
            return Color.parseColor("#09B967")
        }
        if (status.equals("Pending")) {
            return Color.parseColor("#FFCB01")
        }
        if (status.equals("Rejected")) {
            return Color.parseColor("#F23B3B")
        }
        return R.color.color_CCCCCC
    }
}