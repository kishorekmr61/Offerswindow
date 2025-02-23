package com.customer.offerswindow.ui.splash

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.ui.onboarding.OnBoardingActivity
import com.customer.offerswindow.utils.setStatusBar


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Do something if permission granted
            if (isGranted) {
                navigatetoNext()
            } else {
                requestPermissions()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(android.R.style.Theme_NoTitleBar_Fullscreen)
        }else {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setContentView(R.layout.activity_splash)
        setStatusBar(R.color.transparent, false)

        goToNextPage()
    }

    private fun goToNextPage() {
        if (NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
            navigatetoNext()
        } else {
            requestPermission.launch(POST_NOTIFICATIONS)
        }
    }

    private fun navigatetoNext() {
        val intent = Intent(this@SplashActivity, OnBoardingActivity::class.java)
        Handler(Looper.getMainLooper()).postDelayed({
            var notiintent = getIntent()
            if (notiintent.extras?.getString(Constants.ISFROM) == "NOTIFICATIONS") {
                AppPreference.write(Constants.ISFROM, "NOTIFICATIONS")
                AppPreference.write(
                    Constants.Screen_Code,
                    notiintent?.extras?.get(Constants.Screen_Code).toString()
                )
                if (notiintent.extras?.get(Constants.Screen_Code) == "2005") {
                    AppPreference.write(
                        Constants.Focused_Group_Code,
                        notiintent?.extras?.getString(Constants.Focused_Group_Code) ?: ""
                    )
                    AppPreference.write(
                        Constants.Focused_Group_name,
                        notiintent?.extras?.get(Constants.Focused_Group_name).toString()
                    )
                }
            }
            startActivity(intent)
            finish()
        }, 1000)
    }


    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@SplashActivity,
                POST_NOTIFICATIONS
            )
        ) {
        } else {
            ActivityCompat.requestPermissions(
                this@SplashActivity,
                arrayOf(POST_NOTIFICATIONS),
                1
            )
        }
    }


}

