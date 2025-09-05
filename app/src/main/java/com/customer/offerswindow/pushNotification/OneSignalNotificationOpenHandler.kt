package com.customer.offerswindow.pushNotification

import android.content.Context
import android.content.Intent
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.ui.splash.SplashActivity
import com.onesignal.OSNotificationOpenedResult
import com.onesignal.OneSignal

class OneSignalNotificationOpenHandler(private val context: Context) :
    OneSignal.OSNotificationOpenedHandler {

    override fun notificationOpened(result: OSNotificationOpenedResult?) {
        if (result == null) return
        val type = result.action.type
        val data = result.notification.additionalData
        val intent = Intent(context, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Constants.Screen_Code, data.get("Screen_Code").toString())
        if (data.get("Screen_Code").toString() == "1") {
            intent.putExtra(Constants.Offer_id, data.get("Offer_Id").toString())

        }
        intent.putExtra(Constants.ISFROM, "NOTIFICATIONS")
        context.startActivity(intent)
    }
}