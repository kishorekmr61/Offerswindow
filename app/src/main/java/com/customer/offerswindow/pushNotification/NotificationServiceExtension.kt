package com.customer.offerswindow.pushNotification

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.core.app.NotificationCompat
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal.OSRemoteNotificationReceivedHandler
import java.math.BigInteger


class NotificationServiceExtension : OSRemoteNotificationReceivedHandler {
    override fun remoteNotificationReceived(
        context: Context?,
        notificationReceivedEvent: OSNotificationReceivedEvent
    ) {
        val notification = notificationReceivedEvent.notification

        val mutableNotification = notification.mutableCopy()
        mutableNotification.setExtender { builder: NotificationCompat.Builder ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                builder.color = BigInteger("#F00FF00", 16).intValueExact()
            }
            val spannableTitle: Spannable = SpannableString(notification.title)
            spannableTitle.setSpan(
                ForegroundColorSpan(Color.RED),
                0,
                notification.title.length,
                0
            )
            builder.setContentTitle(spannableTitle)
            val spannableBody: Spannable = SpannableString(notification.body)
            spannableBody.setSpan(
                ForegroundColorSpan(Color.BLUE),
                0,
                notification.body.length,
                0
            )
            builder.setContentText(spannableBody)
            builder.setTimeoutAfter(30000)
            builder
        }
        val data = notification.additionalData
        Log.i("OneSignalExample", "Received Notification Data: $data")
        notificationReceivedEvent.complete(mutableNotification)
    }
}