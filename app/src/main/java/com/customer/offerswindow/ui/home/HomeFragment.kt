package com.customer.offerswindow.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.customer.offerswindow.BuildConfig
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentHomeCustomerBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.masters.CommonDataResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.ui.onboarding.OnBoardingActivity
import com.customer.offerswindow.ui.onboarding.signIn.SignInViewModel
import com.customer.offerswindow.utils.PermissionsUtil
import com.customer.offerswindow.utils.bottomsheet.SpinnerBottomSheet
import com.customer.offerswindow.utils.customSeekBar.ProgressItem
import com.customer.offerswindow.utils.setToolbarVisibility
import com.customer.offerswindow.utils.showToast
import com.github.mikephil.charting.data.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val signInViewModel: SignInViewModel by viewModels()
    private var _binding: FragmentHomeCustomerBinding? = null
    private val binding get() = _binding!!
    private val vm: DashBoardViewModel by activityViewModels()
    var soruces_link = ""


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedState: Bundle?,
    ): View {
        _binding = FragmentHomeCustomerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = homeViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        PermissionsUtil.askPermissions(requireActivity())
        return root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(100)
            activity?.setToolbarVisibility(View.GONE)
        }
    }


    private fun setObserver() {
        homeViewModel.customerinfo.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        if (resposnes.Data?.firstOrNull()?.Login_Status == "A") {
                            homeViewModel.isloading.set(false)


                        } else {
                            AppPreference.clearAll()
                            showToast("Looks you are not active user,please contact your coach to access app")
                            var intent = Intent(requireActivity(), OnBoardingActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
                }

                else -> {}
            }
        }

        signInViewModel.masterdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            var responsevalue = getData(response, "Soruces_Link")
                            soruces_link = responsevalue?.MstDesc ?: ""
                            if (soruces_link.isEmpty()) {
                                binding.bmilblTxt.visibility = View.GONE
                            }
                        }
                    }
                }

                is NetworkResult.Error -> {

                }

                is NetworkResult.Loading -> {
                }
            }
        }
    }


    private fun runTimer(viewPager2: ViewPager2, images: ArrayList<String>) {
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                viewPager2.post {
                    viewPager2.currentItem = (viewPager2.currentItem + 1) % images.size
                }
            }
        }
        var timer = Timer()
        timer?.schedule(timerTask, 30000, 3000)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        vm.hidetoolbar.value = false
        homeViewModel.isloading.set(true)


        binding.versionTextview.text = "Version :".plus(" ( " + BuildConfig.VERSION_NAME + " ) ")
        handleNotificationClick()


    }


    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notification -> {
                showToast("Notification clicked")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun handleNotificationClick() {

        try {
            if (AppPreference?.read("ISFROM", "") == "NOTIFICATIONS") {
                AppPreference.write("ISFROM", "")
                if ((AppPreference?.read(Constants.Screen_Code, "") ?: "") == "2001") {
                    findNavController().navigate(R.id.nav_orderHistoryFragment)
                    AppPreference.write(Constants.Screen_Code, "")
                }
                if ((AppPreference?.read(Constants.Screen_Code, "") ?: "") == "2002") {
                    findNavController().navigate(R.id.nav_walletpendingrequestFragment)
                    AppPreference.write(Constants.Screen_Code, "")
                }
                if ((AppPreference?.read(Constants.Screen_Code, "") ?: "") == "2003") {
                    findNavController().navigate(R.id.nav_notifications)
                    AppPreference.write(Constants.Screen_Code, "")
                }
                if ((AppPreference?.read(Constants.Screen_Code, "") ?: "") == "2004") {
                    findNavController().navigate(R.id.nav_notifications)
                    AppPreference.write(Constants.Screen_Code, "")
                }


                if ((AppPreference?.read(Constants.Screen_Code, "") ?: "") == "2006") {
                    findNavController().navigate(R.id.nav_ConfirmedTickets)
                    AppPreference.write(Constants.Screen_Code, "")
                }
                if ((AppPreference?.read(Constants.Screen_Code, "") ?: "") == "2008") {
                    findNavController().navigate(R.id.nav_rewardshistory)
                    AppPreference.write(Constants.Screen_Code, "")
                }

                if ((AppPreference?.read(Constants.Screen_Code, "") ?: "") == "2009") {
                    findNavController().navigate(R.id.nav_rewardshistory)
                    AppPreference.write(Constants.Screen_Code, "")
                }
                if ((AppPreference?.read(Constants.Screen_Code, "") ?: "") == "2010") {
                    findNavController().navigate(R.id.nav_invoiceFragment)
                    AppPreference.write(Constants.Screen_Code, "")
                }
                if ((AppPreference?.read(Constants.Screen_Code, "") ?: "") == "2011") {
                    findNavController().navigate(R.id.nav_walletBalanceFragment)
                    AppPreference.write(Constants.Screen_Code, "")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getData(data: NetworkResult<CommonMasterResponse>, s: String): CommonDataResponse? {
        return data?.data?.data?.find {
            s == it.MstType
        }
    }


}