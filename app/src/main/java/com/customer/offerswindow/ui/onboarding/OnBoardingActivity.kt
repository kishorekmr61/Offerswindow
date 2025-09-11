package com.customer.offerswindow.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.ActivityOnboardingBinding
import com.customer.offerswindow.ui.dashboard.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setSupportActionBar(binding.toolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerViewOnBoarding) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.onboarding_navigation)
        if (AppPreference.read(Constants.SKIPSIGNIN, false)) {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        } else if (!AppPreference.read(
                Constants.ISLOGGEDIN, false
            )
        ) {
            val bundle = Bundle()
            if (intent.getStringExtra(Constants.ISFROM) == "LOGOUT") {
                bundle.putString(Constants.MOBILENO, intent.getStringExtra(Constants.MOBILENO))
                bundle.putString("Message", intent.getStringExtra("Message"))
                navGraph.setStartDestination(R.id.nav_intro)
            } else {
                navGraph.setStartDestination(R.id.nav_intro)
            }
            navController.setGraph(navGraph, bundle)
        } else {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }


}