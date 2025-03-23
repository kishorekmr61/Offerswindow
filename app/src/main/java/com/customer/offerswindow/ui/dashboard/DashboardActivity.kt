package com.customer.offerswindow.ui.dashboard

import android.Manifest
import android.app.Activity
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
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.constant.Constants.UPDATE_REQUEST_CODE
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.ActivityDashboardBinding
import com.customer.offerswindow.utils.PermissionsUtil
import com.customer.offerswindow.utils.showToast
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


    private val vm: DashBoardViewModel by viewModels()
    private lateinit var navController: NavController
    val permissions = arrayOf(
        Manifest.permission.CAMERA
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

        vm.username.set(AppPreference.read(Constants.NAME, "") ?: "")
        vm.profilepic.set(AppPreference.read(Constants.PROFILEPIC, "") ?: "")
        binding.appBarDashboard.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    navController.navigate(R.id.nav_home)
                    return@setOnItemSelectedListener true
                }

                R.id.menu_categories -> {
                    navController.navigate(R.id.nav_categories)
                    return@setOnItemSelectedListener true
                }

                R.id.menu_mybookings -> {
                    if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                        navController.navigate(R.id.nav_mybookings)
                    } else {
                        var bundle = Bundle()
                        navController.navigate(R.id.nav_sign_in, bundle)
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.menu_wishlist -> {
                    if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                        navController.navigate(R.id.nav_rewardshistory)
                    } else {
                        var bundle = Bundle()
                        navController.navigate(R.id.nav_sign_in, bundle)
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.menu_offers -> {
                    if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                        var bundle = Bundle()
                        bundle.putString("ISFROM", "OFFERBOOKING")
                        navController.navigate(R.id.nav_mybookings, bundle)
                    } else {
                        var bundle = Bundle()
                        navController.navigate(R.id.nav_sign_in, bundle)
                    }
                    return@setOnItemSelectedListener true
                }

            }
            return@setOnItemSelectedListener true
        }
        vm.isvisble.observe(this) {
            if (it) {
                binding.appBarDashboard.bottomNavigationView.visibility = View.VISIBLE
            } else {
                binding.appBarDashboard.bottomNavigationView.visibility = View.GONE
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        when {
            navController.currentDestination?.id == R.id.nav_home -> {
                finishAffinity()
                finish()
            }

            else -> {
                super.onBackPressed()
                overridePendingTransition(R.anim.animation_enter, R.anim.animation_exit)
            }
        }
    }

    open fun checkPermissions() {
        try {
            val hasPermissionLocation = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermissionLocation) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    11
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