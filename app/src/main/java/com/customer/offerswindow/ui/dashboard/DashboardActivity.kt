package com.customer.offerswindow.ui.dashboard

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.constant.Constants.UPDATE_REQUEST_CODE
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.ActivityDashboardBinding
import com.customer.offerswindow.ui.onboarding.OnBoardingActivity
import com.customer.offerswindow.utils.PermissionsUtil
import com.customer.offerswindow.utils.showToast
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDashboardBinding


    private lateinit var drawerLayout: DrawerLayout
    private val vm: DashBoardViewModel by viewModels()
    private lateinit var navController: NavController
    val REQUEST_READ_PHONE_STATE = 110
    private val REQUEST_ACCESS_FINE_LOCATION: Int = 111
    val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.CALL_PHONE,
    )

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        // handle callback
        if (result.data == null) return@registerForActivityResult
        if (result.resultCode == UPDATE_REQUEST_CODE) {
            run {
                showToast(getString(R.string.downloading_start))
            }
            if (result.resultCode != Activity.RESULT_OK) {
                showToast(getString(R.string.update_failed))
            }
        }
    }

    private val updateResultStarter =
        IntentSenderForResultStarter { intent, _, fillInIntent, flagsMask, flagsValues, _, _ ->
            val request = IntentSenderRequest.Builder(intent).setFillInIntent(fillInIntent)
                .setFlags(flagsValues, flagsMask).build()

            updateLauncher.launch(request)
        }


    private val appUpdateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Snackbar.make(
                binding.root, getString(R.string.new_app_ready), Snackbar.LENGTH_INDEFINITE
            ).setAction(getString(R.string.restart)) {
                appUpdateManager?.completeUpdate()
            }.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = vm
        setSupportActionBar(binding.appBarDashboard.toolbar)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkUpdate()
        appUpdateManager.registerListener(appUpdateListener)
        setSupportActionBar(binding.appBarDashboard.toolbar)
        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        PermissionsUtil.askPermissions(this)
        PermissionsUtil.checkPermissions(this, *permissions)
        checkPermissions()
        navView.setupWithNavController(navController)
        vm.username.set(AppPreference.read(Constants.NAME, "") ?: "")
        vm.profilepic.set(AppPreference.read(Constants.PROFILEPIC, "") ?: "")

        navView.setNavigationItemSelectedListener { item ->
            val id = navController.currentDestination?.id ?: -1
            if (id == item.itemId) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                when (item.itemId) {
                    R.id.nav_home -> {
                        navController.navigate(R.id.nav_home)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    R.id.menu_rewards -> {
                        navController.navigate(R.id.nav_rewardshistory)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.menu_logout -> {
                        drawerLayout.closeDrawer(GravityCompat.START)
                        val intent = Intent(this, OnBoardingActivity::class.java)
                        intent.putExtra(
                            Constants.MOBILENO,
                            AppPreference.read(Constants.MOBILENO, "") ?: ""
                        )
                        AppPreference.write(Constants.ISLOGGEDIN, false)
                        AppPreference.clearAll()
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }

                    else -> showToast("Under Development")
                }
            }
            true
        }
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {
//                vm.profileUrl.set(AppPreference.read(AppPreference.PROFILE_IMAGE_URL, ""))
//                vm.modifiedDate.set(AppPreference.read(AppPreference.PROFILE_UPDATED_DATE, ""))

            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerStateChanged(newState: Int) {
            }

        })
    }

    fun closeDrawer(view: View?) {
        drawerLayout.closeDrawer(GravityCompat.START)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {


//        if (this::drawerLayout.isInitialized) {
        when {
//                drawerLayout.isDrawerOpen(GravityCompat.START) -> {
//                    drawerLayout.closeDrawer(GravityCompat.START)
//                }

            navController.currentDestination?.id == R.id.nav_home -> {
                finishAffinity()
                finish()
            }

            else -> {
                super.onBackPressed()
                overridePendingTransition(R.anim.animation_enter, R.anim.animation_exit)
            }
        }
//        }
    }

    open fun checkPermissions() {
        try {
            val hasPermissionPhoneState = ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermissionPhoneState) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_READ_PHONE_STATE
                )
            }
            val hasPermissionLocation = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermissionLocation) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_ACCESS_FINE_LOCATION
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun checkUpdate() {
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                )
            ) {
                try {
                    appUpdateManager?.startUpdateFlowForResult(
                        appUpdateInfo,
                        updateResultStarter,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                        UPDATE_REQUEST_CODE
                    )
                } catch (exception: IntentSender.SendIntentException) {
                    showToast(exception.message.toString())
                }
            }
        }
    }

    override fun onDestroy() {
        try {
            appUpdateManager?.unregisterListener(appUpdateListener)
        } catch (e: Exception) {
            //e.printStackTrace()
        }
        super.onDestroy()
    }

}