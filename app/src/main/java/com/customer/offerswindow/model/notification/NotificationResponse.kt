package com.customer.offerswindow.model.notification

import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @Expose @SerializedName("Status") var Status: Int,
    @Expose @SerializedName("Message") val Message: String,
    @SerializedName("Data") var data: ArrayList<NotificationsData>
)

data class NotificationsData(
    var Notification_ID: String,
    var Notification_Desc: String,
    var Notification_Sub: String,
    var Offer_UID: String
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.row_notifications
    }
}