package com.customer.offerswindow.model.dashboard

import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel

data class DashBoardBannerData(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<BannerData>
)

data class BannerData(

    val Notification_ID: String,
    val Subject: String,
    var Effective_Date: String,
    var End_Date: String,
    var Image_Path: String,
    var Notification_Type: String,
    var Message_Desc: String,
): WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.bannernotificationsrow
    }
}
