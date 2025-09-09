package com.customer.offerswindow.ui.splash

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.masters.CommonDataResponse
import com.customer.offerswindow.ui.dashboard.DashboardActivity
import com.customer.offerswindow.utils.setStatusBar
import com.google.gson.Gson
import com.onesignal.OneSignal
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {


    private val vm: SplashViewModel by viewModels()

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

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_splash)
        setStatusBar(R.color.transparent, false)
        setObserver()
        if (AppPreference.read(Constants.TOKEN, "").isNullOrEmpty()) {
            vm.isloading.set(true)
            vm.getToken(
                AppPreference.read(Constants.LOGINUSERNAME, Constants.DEFAULTUSERMOBILE)
                    ?: Constants.DEFAULTUSERMOBILE,
                AppPreference.read(Constants.LOGINPASSWORD, Constants.DEFAULTUSERKEY)
                    ?: Constants.DEFAULTUSERKEY
            )
        } else {
            vm.isloading.set(true)
            vm.getMstData()
        }
    }

    private fun setObserver() {
        vm.tokenResponse.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        vm.getMstData()
                    }
                }

                is NetworkResult.Error -> {
                    vm.getMstData()
                }

                else -> {}
            }
        }
        vm.masterdata.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        response?.data?.data?.forEach {
                            val json = Gson().toJson(
                                response.data?.data ?: arrayListOf<CommonDataResponse>()
                            )
                            AppPreference.write(Constants.MASTERDATA, json)
                            if (it.MstType == "About_us_url") {
                                AppPreference.write(Constants.ABOUTUS, it.MstDesc)
                            }
                            if (it.MstType == "Privacy_Policy") {
                                AppPreference.write(Constants.PRIVACYPOLICY, it.Image_path)
                            }
                            if (it.MstType == "Gold_Trend_Report") {
                                AppPreference.write(Constants.GOLDTRENDREPORT, it.Image_path)
                            }
                        }
                    }
                    vm.getUserInfo(AppPreference.read(Constants.MOBILENO, "") ?: "")
                }

                is NetworkResult.Error -> {
                    vm.getUserInfo(AppPreference.read(Constants.MOBILENO, "") ?: "")
                    goToNextPage()
                }

                else -> {}
            }
        }
        vm.customerinfo.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        AppPreference.write(
                            Constants.NAME,
                            resposnes?.Data?.firstOrNull()?.Cust_Name ?: ""
                        )
                        AppPreference.write(Constants.ISLOGGEDIN, true)
                        AppPreference.write(
                            Constants.USERUID,
                            resposnes?.Data?.firstOrNull()?.Cust_UID ?: "0"
                        )
                        OneSignal.setEmail(resposnes.Data?.firstOrNull()?.Email_ID ?: "");
                        OneSignal.sendTag(
                            "CustomerUID",
                            resposnes.Data?.firstOrNull()?.Cust_UID ?: ""
                        )
                        AppPreference.write(
                            Constants.MOBILENO,
                            resposnes?.Data?.firstOrNull()?.Mobile_No ?: ""
                        )
                        AppPreference.write(
                            Constants.PIN,
                            resposnes?.Data?.firstOrNull()?.Pin_No ?: ""
                        )
                        AppPreference.write(
                            Constants.PROFILEPIC,
                            resposnes?.Data?.firstOrNull()?.Cust_Image_URL ?: ""
                        )
                    }
                    vm.isloading.set(false)
                    goToNextPage()
                }

                is NetworkResult.Error -> {
                    vm.isloading.set(false)
                    goToNextPage()
                }

                else -> {}
            }
        }
    }

    private fun goToNextPage() {
        if (NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
            navigatetoNext()
        } else {
            requestPermission.launch(POST_NOTIFICATIONS)
        }
    }

    private fun navigatetoNext() {
        val intent = Intent(this@SplashActivity, DashboardActivity::class.java)
        Handler(Looper.getMainLooper()).postDelayed({
            var notiintent = getIntent()
            if (notiintent.extras?.getString(Constants.ISFROM) == "NOTIFICATIONS") {
                AppPreference.write(Constants.ISFROM, "NOTIFICATIONS")
                AppPreference.write(
                    Constants.Screen_Code,
                    notiintent?.extras?.get(Constants.Screen_Code).toString()
                )
                if (notiintent.extras?.get(Constants.Screen_Code) == "1") {
                    AppPreference.write(
                        Constants.Offer_id,
                        notiintent?.extras?.getString(Constants.Offer_id) ?: ""
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

