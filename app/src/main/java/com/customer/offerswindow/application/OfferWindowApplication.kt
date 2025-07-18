package com.customer.offerswindow.application

import android.app.Application
import androidx.multidex.MultiDex
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.pushNotification.OneSignalNotificationOpenHandler
import com.onesignal.OneSignal
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OfferWindowApplication : Application() {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    val ONESIGNAL_APP_ID = "7b903495-1dc6-49f9-a3bf-19309adf3281"

    override fun onCreate() {
        super.onCreate()
        // Required initialization logic here!
        AppPreference.init(applicationContext)
        context = this
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setNotificationOpenedHandler(OneSignalNotificationOpenHandler(this))
        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
        MultiDex.install(this)
        OneSignal.promptForPushNotifications();
//        OneSignal.setNotificationOpenedHandler(OneSignalNotificationOpenHandler(this))
        // OneSignal Initialization
        MultiDex.install(this)
//        OneSignal.promptForPushNotifications();
//        val crashlytics = Firebase.crashlytics
//        crashlytics.setCustomKeys {
//            key("Mobilenumber", AppPreference.read(Constants.MOBILENO,"") ?:"Unable to get phone No")
//            key("USERID", AppPreference.read(Constants.USERUID,"") ?:"Unable to get UserID")
//            key("NAME", AppPreference.read(Constants.NAME,"") ?:"Unable to get Name")
//            key("BUILDVERSION", BuildConfig.VERSION_NAME)
//        }


    }

    companion object {
        lateinit var context: OfferWindowApplication
    }

}