package com.customer.offerswindow.model

import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class issueResponse(
    @Expose
    @SerializedName("Status")
    var Status: Int,
    @Expose
    @SerializedName("Message")
    val Message: String,
    @Expose
    @SerializedName("Data")
    val issuedta : ArrayList<issuedata>,
)

data class issuedata(
    var issueid: Long,
    var errortype: String,
    var date: String,
    var remarks: String,
    var status: String,
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.row_events
    }
}
