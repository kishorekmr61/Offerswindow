package com.customer.offerswindow.ui.dashboard

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
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
import com.customer.offerswindow.utils.openURL
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
    private var selectedIndex = 0
    private var currentIndex = 0


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
        val navView: NavigationView = binding.navView
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkUpdate()
        appUpdateManager.registerListener(appUpdateListener)
        setSupportActionBar(binding.appBarDashboard.toolbar)
        drawerLayout = binding.drawerLayout
        PermissionsUtil.askPermissions(this)
        PermissionsUtil.checkPermissions(this, *permissions)
        checkPermissions()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.app_navigation)
        navGraph.setStartDestination(R.id.nav_home)
        navView.setupWithNavController(navController)
        navController.setGraph(navGraph, null)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
            vm.username.set(AppPreference.read(Constants.NAME, "") ?: "")
            vm.profilepic.set(AppPreference.read(Constants.PROFILEPIC, "") ?: "")
        }
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
                        navController.navigate(R.id.nav_bookings)
                    } else {
                        var bundle = Bundle()
                        navController.navigate(R.id.nav_sign_in, bundle)
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.menu_Redeem -> {
                    if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                        navController.navigate(R.id.nav_rewardshistory)
                    } else {
                        var bundle = Bundle()
                        navController.navigate(R.id.nav_sign_in, bundle)
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.menu_info -> {
                    if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                        binding.drawerLayout.openDrawer(GravityCompat.START);
                        //  navController.navigate(R.id.nav_webview)
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

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    shouldExitApp()
                }
            })
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.nav_home ->
                    binding.appBarDashboard.bottomNavigationView.menu[0].isChecked = true

                R.id.nav_categories ->
                    binding.appBarDashboard.bottomNavigationView.menu[1].isChecked = true

                R.id.nav_bookings ->
                    binding.appBarDashboard.bottomNavigationView.menu[2].isChecked = true

                R.id.nav_rewardshistory ->
                    binding.appBarDashboard.bottomNavigationView.menu[3].isChecked = true

                R.id.nav_webview ->
                    binding.appBarDashboard.bottomNavigationView.menu[4].isChecked = true
            }
        }


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

                    R.id.menu_slotbooking -> {
                        navController.navigate(R.id.nav_bookings)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.menu_Rewards -> {
                        navController.navigate(R.id.nav_rewardshistory)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.menu_mybookings -> {
                        navController.navigate(R.id.nav_bookings)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.menu_wishlist -> {
                        navController.navigate(R.id.nav_wishlist)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.menu_gold -> {
                        openURL((AppPreference.read(Constants.GOLDTRENDREPORT, "") ?: "").toUri())
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.menu_aboutus -> {
                        navController.navigate(R.id.nav_webview)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.menu_contactus -> {
                        navController.navigate(R.id.nav_webview)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.menu_addpost -> {
                        navController.navigate(R.id.nav_addpost)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.menu_notification -> {
                        navController.navigate(R.id.nav_notifications)
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
                        intent.putExtra(
                            Constants.MASTERDATA,
                            AppPreference.read(Constants.MASTERDATA, "")
                        )
                        AppPreference.clearAll()
                        AppPreference.write(
                            Constants.MASTERDATA,
                            intent.getStringExtra(Constants.MASTERDATA) ?: ""
                        )
                        intent.putExtra(Constants.ISFROM, "LOGOUT")
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
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


    override fun onSupportNavigateUp()
            : Boolean {
        val navController = findNavController(R.id.nav_host)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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


    fun shouldExitApp() {
        when {
            navController.currentDestination?.id == R.id.nav_home -> {
                finishAffinity()
                finish()
            }

            else -> {
                this.overridePendingTransition(
                    R.anim.animation_enter,
                    R.anim.animation_exit
                )
            }
        }

    }


}