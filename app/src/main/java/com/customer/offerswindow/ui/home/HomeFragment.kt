package com.customer.offerswindow.ui.home

import android.Manifest
import android.Manifest.permission.READ_PHONE_STATE
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.customer.offerswindow.BR
import com.customer.offerswindow.BuildConfig
import com.customer.offerswindow.R
import com.customer.offerswindow.R.id.nav_myplans
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentHomeCustomerBinding
import com.customer.offerswindow.databinding.ImageLayoutBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.CustomerData
import com.customer.offerswindow.model.CustomerFeedBackHome
import com.customer.offerswindow.model.EventsList
import com.customer.offerswindow.model.customersdata.CaloriesCaliculationData
import com.customer.offerswindow.model.customersdata.GraphData
import com.customer.offerswindow.model.dashboard.HomeHeader
import com.customer.offerswindow.model.dashboard.postCustomerWeight
import com.customer.offerswindow.model.masters.CommonDataResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.ui.calorie.CalorieViewModel
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.ui.eventslist.EventsListViewModel
import com.customer.offerswindow.ui.onboarding.OnBoardingActivity
import com.customer.offerswindow.ui.onboarding.signIn.SignInViewModel
import com.customer.offerswindow.ui.reportissue.ReportIssueViewModel
import com.customer.offerswindow.utils.PermissionsUtil
import com.customer.offerswindow.utils.ShowFullToast
import com.customer.offerswindow.utils.bottomsheet.SpinnerBottomSheet
import com.customer.offerswindow.utils.convertDate
import com.customer.offerswindow.utils.customSeekBar.ProgressItem
import com.customer.offerswindow.utils.getDateTime
import com.customer.offerswindow.utils.setToolbarVisibility
import com.customer.offerswindow.utils.setUpMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.showCommonCustomIOSDialog
import com.customer.offerswindow.utils.showLongToast
import com.customer.offerswindow.utils.showToast
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val eventsViewModel: EventsListViewModel by viewModels()
    private val caloriesviewModel: CalorieViewModel by viewModels()
    private val reportissueviewModel: ReportIssueViewModel by viewModels()
    private val signInViewModel: SignInViewModel by viewModels()
    private var _binding: FragmentHomeCustomerBinding? = null
    private val binding get() = _binding!!
    var modalBottomSheet: SpinnerBottomSheet? = null
    private val vm: DashBoardViewModel by activityViewModels()//9848599596
    private lateinit var imageViewPagerAdapter: SliderAdapter
    private lateinit var eventsViewPagerAdapter: SliderAdapter
    lateinit var dates: ArrayList<String>
    var Eventid: Long = 0
    var Eventprice: Double = 0.0
    private var progressItemList: ArrayList<ProgressItem>? = arrayListOf()
    var soruces_link = ""
    val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.CALL_PHONE,
    )

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
        AppPreference.write(Constants.SIMNUMBER, GetNumber())
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
                            binding.customerdata = resposnes.Data?.firstOrNull()
                            binding.customerdata = resposnes.Data?.firstOrNull()
                            var customername = ""
                            if (!resposnes?.Data?.firstOrNull()?.Cust_Name.isNullOrEmpty()) {
                                customername = resposnes?.Data?.firstOrNull()?.Cust_Name ?: ""
                            }
                            if (!resposnes?.Data?.firstOrNull()?.Sur_Name.isNullOrEmpty()) {
                                customername += " " + resposnes?.Data?.firstOrNull()?.Sur_Name ?: ""
                            }
                            homeViewModel.name.set(customername)
                            homeViewModel.userbmi.set(resposnes.Data?.firstOrNull()?.BMI)
                            initDataToSeekbar()
                            AppPreference.write(
                                Constants.WALLETBALANCE,
                                resposnes.Data?.firstOrNull()?.Wallet_Balance ?: "0.0"
                            )
                            binding.currentweightTxt.text =
                                (resposnes.Data?.firstOrNull()?.Last_Body_Weight ?: 0).toString()
                            homeViewModel.getTestmonialData(
                                AppPreference.read(
                                    Constants.USERUID,
                                    "0"
                                ) ?: "0"
                            )
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
        homeViewModel.dashboartestmonialddata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            homeViewModel.isloading.set(false)
                            bindTestmonialData(response.data?.Data ?: arrayListOf())
                            homeViewModel.getBannerInfo(
                                AppPreference.read(Constants.USERUID, "0") ?: "0"
                            )
                        } else {
                            ShowFullToast(response.data?.Message ?: "")
                        }
                    }
                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
                    response.message?.let { ShowFullToast(response.message) }
                }

                is NetworkResult.Loading -> {
                    homeViewModel.isloading.set(true)
                }
            }
        }
        homeViewModel.bannerResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        homeViewModel.isloading.set(false)
                        if (resposnes?.Status == 200) {
                            if (!response.data?.Data.isNullOrEmpty()) {
                                binding.clAdv.visibility = View.VISIBLE
                                binding.view3.visibility = View.VISIBLE
                                var images = ArrayList<String>()
                                response.data?.Data?.forEach {
                                    if (it.Notification_Type == "Banner") {
                                        images.add(it.Image_Path)
                                    }
                                }
                                loadViewPager(images)
                            }

                        } else {
                            ShowFullToast(response.data?.Message ?: "")
                        }
                    }
                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
                }

                is NetworkResult.Loading -> {
                    homeViewModel.isloading.set(true)
                }
            }
        }
        homeViewModel.graphdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            filterData(response.data?.Data)

                        } else {
                            ShowFullToast(response.data?.Message ?: "")
                        }
                        eventsViewModel.getDashboardEventList(0)
                    }
                }

                is NetworkResult.Error -> {
//                    viewModel.isloading.set(false)
                    response.message?.let { ShowFullToast(response.message) }
                    eventsViewModel.getDashboardEventList(0)
                }

                is NetworkResult.Loading -> {
//                    viewModel.isloading.set(true)
                }
            }
        }
        eventsViewModel.dashboardeventsListdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            loadEventViewpager(resposnes.Data)
                        } else {
                            ShowFullToast(response.data?.Message ?: "")
                        }
                    }
                }

                is NetworkResult.Error -> {
//                    viewModel.isloading.set(false)
                    response.message?.let { ShowFullToast(response.message) }
                }

                is NetworkResult.Loading -> {
//                    viewModel.isloading.set(true)
                }
            }
        }
        caloriesviewModel.calorieCaliculatordata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            if (response.data?.data?.firstOrNull() == null) {
                                binding.coachRowdsad.caloriesLyout.visibility = View.GONE
                                binding.weightprogressView.visibility = View.GONE
                            } else {
                                binding.coachRowdsad.caloriesLyout.visibility = View.VISIBLE
                                binding.weightprogressView.visibility = View.VISIBLE
                                loadPieChartData(resposnes.data)
                            }
                        } else {
                            ShowFullToast(response.data?.Message ?: "")
                        }
                    }
                }

                is NetworkResult.Error -> {
//                    viewModel.isloading.set(false)
                    response.message?.let { ShowFullToast(response.message) }
                }

                is NetworkResult.Loading -> {
//                    viewModel.isloading.set(true)
                }
            }
        }
        homeViewModel.postweightResponse.observe(viewLifecycleOwner) { weightresponse ->
            when (weightresponse) {
                is NetworkResult.Success -> {
                    binding.currentweightTxt.text = binding.weightEdt.text.toString()
                    binding.weightEdt.setText("")
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

    private fun loadEventViewpager(eventslist: ArrayList<EventsList>) {
        homeViewModel.isloading.set(false)
        if (!eventslist.isNullOrEmpty()) {
            binding.eventsschduleLyout.visibility = View.VISIBLE
            binding.clEvents.visibility = View.VISIBLE
            binding.eventnameLbl.visibility = View.VISIBLE
            binding.viewalleventsBtn.visibility = View.VISIBLE
            binding.view8.visibility = View.VISIBLE
            var images = ArrayList<String>()
            eventslist.forEach {
                images.add(it.Image_URL)
            }
            eventsViewPagerAdapter = SliderAdapter(images)
            binding.eventviewpager.adapter = eventsViewPagerAdapter
            binding.eventviewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            val currentPageIndex = 1
            binding.eventviewpager.currentItem = currentPageIndex
            binding.eventviewpager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                var SCROLLING_RIGHT = 0
                var SCROLLING_LEFT = 1
                var SCROLLING_UNDETERMINED = 2
                var currentScrollDirection = 2
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    if (isScrollDirectionUndetermined) {
                        setScrollingDirection(positionOffset)
                    }
                    if (isScrollingLeft) {
                        updateEventPosition(position)

                    }
                    if (isScrollingRight) {
                        updateEventPosition(position)
                    }
                }

                private fun setScrollingDirection(positionOffset: Float) {
                    if (1 - positionOffset >= 0.5) {
                        currentScrollDirection = SCROLLING_RIGHT
                    } else if (1 - positionOffset <= 0.5) {
                        currentScrollDirection = SCROLLING_LEFT
                    }
                }

                private val isScrollDirectionUndetermined: Boolean
                    private get() = currentScrollDirection == SCROLLING_UNDETERMINED
                private val isScrollingRight: Boolean
                    private get() = currentScrollDirection == SCROLLING_RIGHT
                private val isScrollingLeft: Boolean
                    private get() = currentScrollDirection == SCROLLING_LEFT

                override fun onPageSelected(position: Int) {}
                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        currentScrollDirection = SCROLLING_UNDETERMINED
                    }
                }
            })
            if (!images.isNullOrEmpty() && images.size > 1) {
                runEventsTimer(binding.eventviewpager, images, eventslist)
                TabLayoutMediator(
                    binding.eventstabLayout,
                    binding.eventviewpager
                ) { tab, position ->
                    binding.eventnameLbl.text = eventslist[position].Event_Name
                    binding.eventdateTxt.text = convertDate(
                        eventslist[position].Event_Date,
                        Constants.YYYYMMDDTHH,
                        Constants.DDMMMYYYY
                    )
                    binding.eventpriceTxt.text = eventslist[position].Ticket_Cost
                }.attach()
            }
            binding.bookticketsBtn.setOnClickListener {
                var bundle = Bundle()
                bundle.putLong(Constants.EVENTID, Eventid)
                bundle.putDouble(Constants.EVENTPRICE, Eventprice)
                findNavController().navigate(R.id.nav_eventDetails, bundle)
            }
        }
    }

    private fun updateEventPosition(position: Int) {
        var eventdata = eventsViewModel.dashboardeventsListdata.value?.data?.Data?.get(position)
        binding.eventnameLbl.text = eventdata?.Event_Name
        binding.eventdateTxt.text = convertDate(
            eventdata?.Event_Date ?: "",
            Constants.YYYYMMDDTHH,
            Constants.DDMMMMYYYY
        )
        Eventid = eventdata?.Event_ID?.toLong() ?: 0
        Eventprice = eventdata?.Ticket_Cost?.toDouble() ?: 0.0
        binding.eventpriceTxt.text = eventdata?.Ticket_Cost
    }

    private fun runEventsTimer(
        viewPager2: ViewPager2,
        images: ArrayList<String>,
        eventslist: ArrayList<EventsList>
    ) {
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                viewPager2.post {


                }
            }
        }
        var timer = Timer()
        timer?.schedule(timerTask, 10000, 10000)
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

    private fun loadViewPager(images: ArrayList<String>) {
        if (!images.isNullOrEmpty()) {
            binding.clAdv.visibility = View.VISIBLE
            binding.view3.visibility = View.VISIBLE
            imageViewPagerAdapter = SliderAdapter(images)
            binding.viewpager.adapter = imageViewPagerAdapter
            binding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            val currentPageIndex = 1
            binding.viewpager.currentItem = currentPageIndex
            if (!images.isNullOrEmpty() && images.size > 1) {
                runTimer(binding.viewpager, images)
            }
            TabLayoutMediator(binding.tablyout, binding.viewpager) { tab, position ->
            }.attach()
        } else {
            binding.clAdv.visibility = View.GONE
            binding.view3.visibility = View.GONE
        }

    }


    private fun setListeners() {
        binding.setVariable(BR.onItemClick, View.OnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.ISFROM, "")
            when (it.id) {
                //Coach View
                R.id.walletbalance_img -> {
                    findNavController().navigate(R.id.nav_walletBalanceFragment, bundle)
                }

                R.id.notifications -> {
                    findNavController().navigate(R.id.nav_notifications)
                }

                R.id.logout -> {
                    val intent = Intent(requireActivity(), OnBoardingActivity::class.java)
                    intent.putExtra(
                        Constants.MOBILENO,
                        AppPreference.read(Constants.MOBILENO, "") ?: ""
                    )
                    intent.putExtra(Constants.ISFROM, "LOGOUT")
                    AppPreference.write(Constants.ISLOGGEDIN, false)
                    AppPreference.clearAll()
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }

                R.id.rewardpoints_img -> {
                    findNavController().navigate(R.id.nav_rewardshistory, bundle)
                }

                R.id.myprogramsheader -> {
                    if (validatePlanStatus(homeViewModel.customerinfo.value?.data?.Data?.firstOrNull())) {
                        findNavController().navigate(R.id.nav_myprograms)
                    }
                }

                R.id.mytasks -> {
                    if (PermissionsUtil.checkPermissions(requireActivity(), *permissions)) {
                        findNavController().navigate(R.id.nav_customerActivitiesFragment)
                    } else {
                        showLongToast("Please enable permissions from settings to access this feature")
                    }
                }

                R.id.mytask_btn -> {
                    findNavController().navigate(R.id.nav_customerActivitiesFragment)
                }

                R.id.challenges -> {
                    findNavController().navigate(R.id.nav_challengesFragment)
                }

                R.id.challenge_btn -> {
                    findNavController().navigate(R.id.nav_challengesFragment)
                }

                R.id.checkuphistory -> {
                    findNavController().navigate(R.id.nav_customerCheckUpsFragment)
                }

                R.id.checkup_btn -> {
                    findNavController().navigate(R.id.nav_customerCheckUpsFragment)
                }

                R.id.viewplan -> {
                    findNavController().navigate(nav_myplans)
                }

                R.id.viewplan_btn -> {
                    findNavController().navigate(nav_myplans)
                }

                R.id.reportissue_lbl -> {
                    findNavController().navigate(R.id.nav_reportissue)
                }

                R.id.resetpswrd_lbl -> {
                    findNavController().navigate(R.id.nav_resetpassword)
                }

                R.id.about_btn -> {
                    findNavController().navigate(R.id.nav_Webpage)
                }

                R.id.shareapp_btn -> {
                    findNavController().navigate(R.id.nav_shareapp)
                }

                R.id.virtual_lyout -> {
                    var bundle = Bundle()
                    bundle.putString(Constants.TOOLBARTITLE, "Virtual Service")
                    bundle.putString(Constants.SERVICELIST, "Virtual_Services_List")
                    bundle.putString(Constants.SERVICEDESC, "Virtual_Services_Desc")
                    findNavController().navigate(R.id.nav_bmsservices, bundle)
                }

                R.id.physical_lyout -> {
                    var bundle = Bundle()
                    bundle.putString(Constants.TOOLBARTITLE, "Physical Service")
                    bundle.putString(Constants.SERVICELIST, "Physical_Services_List")
                    bundle.putString(Constants.SERVICEDESC, "Physical_Services_Desc")
                    findNavController().navigate(R.id.nav_bmsservices, bundle)
                }

                R.id.viewallevents_btn -> {
                    findNavController().navigate(R.id.nav_eventsListFragment)
                }

                R.id.imageView14 -> {
                    if (PermissionsUtil.checkPermissions(requireActivity(), *permissions)) {
                        var bundle = Bundle()
                        bundle.putString(
                            Constants.Customertype,
                            Gson().toJson(homeViewModel.customerinfo.value?.data?.Data?.firstOrNull())
                        )
                        findNavController().navigate(R.id.nav_customerProfileFragment, bundle)
                    } else {
                        showLongToast("Please enable permissions from settings to access this feature")
                    }
                }

                R.id.refresh_img -> {
                    homeViewModel.getCustomerGraph(AppPreference.read(Constants.USERUID, "") ?: "")
                }

                R.id.bmilbl_txt -> {
                    if (soruces_link.isEmpty()) {
                        showToast("unable to proceed.")
                    } else {
                        requireActivity().startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(soruces_link)
                            )
                        )
                    }
                }
            }
        })

    }

    private fun validatePlanStatus(customerData: CustomerData?): Boolean {
        if (customerData?.Last_Plan_Start_Date.isNullOrEmpty() || customerData?.Last_Plan_Expiry_Date.isNullOrEmpty()) {
            showCommonCustomIOSDialog(
                requireActivity(),
                "Note",
                "You have to subscribe for Physical/Virtual programs to access this feature.",
                getString(R.string.okay),
                {},
                "",
                {}, false
            )
            return false
        } else if (!checkUserPlanStatus()) {
            showCommonCustomIOSDialog(
                requireActivity(),
                "Note",
                "Your membership plan got expired on ".plus(
                    convertDate(
                        homeViewModel.customerinfo.value?.data?.Data?.firstOrNull()?.Last_Plan_Expiry_Date
                            ?: "", Constants.YYYYMMDDTHH, Constants.DDMMMMYYYY
                    )
                )
                    .plus(", please renew your membership plan to access this feature"),
                getString(R.string.okay),
                {},
                "",
                {}, false
            )
            return false
        }
        return true
    }

    private fun checkUserPlanStatus(): Boolean {
        var startdate = getDate(
            convertDate(
                homeViewModel.customerinfo.value?.data?.Data?.firstOrNull()?.Last_Plan_Start_Date
                    ?: "", Constants.YYYYMMDDTHH, Constants.YYY_HIFUN_MM_DD
            ).plus(" 00:00:00")
        )
        var enddate = getDate(
            convertDate(
                homeViewModel.customerinfo.value?.data?.Data?.firstOrNull()?.Last_Plan_Expiry_Date
                    ?: "", Constants.YYYYMMDDTHH, Constants.YYY_HIFUN_MM_DD
            ).plus(" 23:59:00")
        )
        return if (startdate != null && enddate != null) {
            Date().after(startdate) && Date().before(enddate)
        } else
            false
    }

    private fun getDate(givenDate: String?): Date? {
        val format = SimpleDateFormat(Constants.YYYYMMDDHH)
        return try {
            format.parse(givenDate)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }//2024-04-19  yyyy-MM-dd HH:mm:ss

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindHeaderData()
        setObserver()
        vm.hidetoolbar.value = false
        homeViewModel.isloading.set(true)
        signInViewModel.getMstData()
        homeViewModel.getDashboardData(AppPreference.read(Constants.USERUID, "0") ?: "0")
        homeViewModel.getCustomerGraph(AppPreference.read(Constants.USERUID, "0") ?: "0")
        caloriesviewModel.getUserDailyCaloriesCalculator(
            AppPreference.read(Constants.USERUID, "0") ?: "0"
        )
        binding.imageView21.setOnClickListener {
            if (!binding.weightEdt.text.toString()
                    .isNullOrEmpty()
            ) {
                homeViewModel.isloading.set(true)
                val postweights = postCustomerWeight(
                    AppPreference.read(Constants.USERUID, "0") ?: "0",
                    getDateTime(),
                    binding.weightEdt.text.toString().toDouble(),
                    AppPreference.read(Constants.USERUID, "") ?: "",
                    getDateTime()
                )
                homeViewModel.postWeightData(postweights)
            } else {
                ShowFullToast("Please enter valid weight")
            }
        }
        setListeners()
        binding.versionTextview.text = "Version :".plus(" ( " + BuildConfig.VERSION_NAME + " ) ")
        handleNotificationClick()


    }

    private fun bindTestmonialData(customerFeedBackHomes: ArrayList<CustomerFeedBackHome>) {
        binding.rvTestmonials.setUpMultiViewRecyclerAdapter(
            customerFeedBackHomes
        ) { item: CustomerFeedBackHome, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            binder.setVariable(BR.onItemClick, View.OnClickListener {
                when (it.id) {
                    R.id.circleImageView -> {
                        openImage(item.Image_URL)
                    }
                }
                binder.executePendingBindings()
            })
        }
    }

    private fun bindHeaderData() {
        binding.rvheader.setUpMultiViewRecyclerAdapter(
            homeViewModel.getHomeData()
        ) { item: HomeHeader, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            binder.setVariable(BR.onItemClick, View.OnClickListener {
//                var itemdata = item
                when (item.label) {
                    "Focus \n Group" -> {
//                        if (PermissionsUtil.checkPermissions(requireActivity(), *permissions)) {
                        var bundle = Bundle()
                        bundle.putLong(
                            Constants.GroupID,
                            homeViewModel.customerinfo.value?.data?.Data?.firstOrNull()?.User_UID?.toLong()
                                ?: 0L
                        )
                        bundle.putString(
                            Constants.Focused_Group_name,
                            homeViewModel.customerinfo.value?.data?.Data?.firstOrNull()?.Cust_Name
                                ?: ""
                        )
                        findNavController().navigate(R.id.nav_chatFragment, bundle)
//                        } else {
//                            showLongToast("Please enable permissions from settings to access this feature")
//                        }
                    }

                    "My \n Achievements" -> {
                        findNavController().navigate(R.id.nav_achievementFragment)
                    }

                    "Reference \n History" -> {
                        findNavController().navigate(R.id.nav_myrefereces)
                    }

                    "Diet Plan \n request" -> {
                        if (homeViewModel.customerinfo.value?.data?.Data?.firstOrNull()?.PDD_Status != "N") {
                            findNavController().navigate(R.id.nav_customerstockSalesFragment)
                        } else {
                            showCommonCustomIOSDialog(
                                requireActivity(),
                                "Note",
                                "You have to attend the PDD program to access this feature on the app",
                                getString(R.string.okay),
                                {},
                                "",
                                {}, false
                            )
                        }
                    }

                    "Stock \n Purchases" -> {
                        if (homeViewModel.customerinfo.value?.data?.Data?.firstOrNull()?.Sale_Started != "N") {
                            if (homeViewModel.customerinfo.value?.data?.Data?.firstOrNull()?.Cust_Category == "Preferred Customer") {
                                findNavController().navigate(R.id.nav_stockPurchasesFragment)
                            } else {
                                showCommonCustomIOSDialog(
                                    requireActivity(),
                                    "Note",
                                    "This screen is allowed only for preferred Customers",
                                    getString(R.string.okay),
                                    {},
                                    "",
                                    {}, false
                                )
                            }
                        } else {
                            showCommonCustomIOSDialog(
                                requireActivity(),
                                "Note",
                                "You can't access this screen without buying a product from your coach",
                                getString(R.string.okay),
                                {},
                                "",
                                {}, false
                            )
                        }
                    }

                    "Invoices" -> {//9949215226
                        findNavController().navigate(R.id.nav_invoiceFragment)
                    }

                    "Wallet \n Balances" -> {
                        findNavController().navigate(R.id.nav_walletBalanceFragment)
                    }

                    "Rewards" -> {
                        findNavController().navigate(R.id.nav_rewardshistory)
                    }

                    "Notifications" -> {
                        findNavController().navigate(R.id.nav_notifications)
                    }
                }
            })
            binder.executePendingBindings()
        }
//        binding.progress.setMax(totalSpace.toFloat())
//        binding.progress.setPrimaryProgress(usedSpace.toFloat())
//        binding.progress.setSecondaryProgress(picturesSize.toFloat())
//        binding.progress.setThirdProgress(downloadsSize.toFloat())
    }


    @RequiresApi(Build.VERSION_CODES.O)
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


    private fun filterData(data: ArrayList<GraphData>?) {
        loadEntries(data)
    }

    private fun loadEntries(data: ArrayList<GraphData>?) {
        if (!data.isNullOrEmpty()) {
            updateChart(data)
        }
    }

    fun updateChart(datavalue: ArrayList<GraphData>?) {
        try {
            setUpLineChart(datavalue)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpLineChart(datavalue: ArrayList<GraphData>?) {
        with(binding.lineChart) {

            val linevalues = ArrayList<Entry>()
            val labels = ArrayList<String>()
            var i = 0f
            datavalue?.forEach {
                linevalues.add(Entry(i, it.Weight?.toFloat() ?: 0f))
                labels.add(convertDate(it.Created_Date ?: " ", Constants.YYYYMMDDTHH, "dd/MMM"))
                i++
            }
            xAxis.valueFormatter = IndexAxisValueFormatter(getDate(labels));
            val linedataset = LineDataSet(linevalues, "")
            linedataset.lineWidth = 2f
            linedataset.valueTextSize = 10f
            linedataset.mode = LineDataSet.Mode.CUBIC_BEZIER
            binding.lineChart.description.text = ""

            val xaxis: XAxis = binding.lineChart.xAxis
            xaxis.setDrawGridLines(false)
//            xaxis.mAxisMaximum = labels.size.toFloat()
            xaxis.labelCount = linevalues?.size ?: 0
            xaxis.setDrawLabels(true)
            xaxis.setDrawAxisLine(false)
            xaxis.axisMinimum = -1f;
            xAxis.axisMaximum = linedataset.xMax
            xaxis.setDrawGridLines(false)
            val yAxisLeft: YAxis = binding.lineChart.axisLeft
            yAxisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            yAxisLeft.setDrawGridLines(false)
            yAxisLeft.setDrawAxisLine(false)
            yAxisLeft.isEnabled = false
            yAxisLeft.textSize = 10f

//            binding.lineChart.isDoubleTapToZoomEnabled= false
            binding.lineChart.axisRight.isEnabled = false
            binding.lineChart.extraBottomOffset = 10f
            binding.lineChart.extraLeftOffset = 10f
            binding.lineChart.description.isEnabled = false
            binding.lineChart.setDrawGridBackground(false)


            linedataset.color = ContextCompat.getColor(requireActivity(), R.color.color_D1F6E4)
            linedataset.valueTextColor =
                ContextCompat.getColor(requireActivity(), R.color.white)
            linedataset.setCircleColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.color_0CA95B
                )
            )
            linedataset.circleHoleColor =
                ContextCompat.getColor(requireActivity(), R.color.color_FFCB01)
            linedataset.setDrawCircleHole(true)

            //We connect our data to the UI Screen
            val data = LineData(linedataset)
            binding.lineChart.data = data
//            binding.lineChart.setBackgroundColor(resources.getColor(R.color.white))
            binding.lineChart.animateXY(2000, 2000, Easing.EaseInCubic)


        }
    }


    private fun loadPieChartData(data: ArrayList<CaloriesCaliculationData>) {

        val pieEntries = ArrayList<PieEntry>()
        val typeAmountMap: MutableMap<String, Int> = HashMap()
        typeAmountMap["Fats"] = data.firstOrNull()?.Fat_Calories?.toInt() ?: 0
        typeAmountMap["Proteins"] = data.firstOrNull()?.Protien_Calories?.toInt() ?: 0
        typeAmountMap["Carbs"] = data.firstOrNull()?.Cabs_Calories?.toInt() ?: 0
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(data.firstOrNull()?.Fat_Calories ?: 0F, "Fats"))
        entries.add(PieEntry(data.firstOrNull()?.Protien_Calories ?: 0F, "Proteins"))
        entries.add(PieEntry(data.firstOrNull()?.Cabs_Calories ?: 0F, "Carbs"))

        val colors = ArrayList<Int>()
        colors.add(Color.parseColor("#09B967"))
        colors.add(Color.parseColor("#F5991C"))
        colors.add(Color.parseColor("#ED5822"))
        for (type in typeAmountMap.keys) {
            pieEntries.add(PieEntry(typeAmountMap[type]?.toFloat() ?: 0f, type))
        }


        //collecting the entries with label name
        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.colors = colors
        //grouping the data set from entry to chart
        val pieData = PieData(pieDataSet)
        var desc = Description()
        desc.text = ""
        binding.coachRowdsad.pieChartView.description = desc
        pieData.setDrawValues(true)
        pieDataSet.valueFormatter = PercentFormatter(binding.coachRowdsad.pieChartView)
        pieDataSet.valueTextSize = 10f
        pieDataSet.valueTextColor = Color.WHITE
        binding.coachRowdsad.pieChartView.centerText =
            (data.firstOrNull()?.Total_Calories ?: "0").plus(" Calories")
        binding.coachRowdsad.pieChartView.data = pieData
        binding.coachRowdsad.pieChartView.invalidate()
        binding.coachRowdsad.pieChartView.animateY(1400, Easing.EaseInOutQuad)


        binding.coachRowdsad.pieChartView.legend.isEnabled = true


        var proteins = data.firstOrNull()?.Protien_Grams
        if (proteins.isNullOrEmpty()) {
            proteins = "0"
        }
        var cabs = data.firstOrNull()?.Cabs_Grams
        if (cabs.isNullOrEmpty()) {
            cabs = "0"
        }
        var fat = data.firstOrNull()?.Fat_Grams
        if (fat.isNullOrEmpty()) {
            fat = "0"
        }
        binding.coachRowdsad.proteinsTxt.text = proteins.plus("gm")
        binding.coachRowdsad.carbsTxt.text = cabs.plus("gm")
        binding.coachRowdsad.fatTxt.text = fat.plus("gm")
        if (data.firstOrNull()?.Current_Weight.isNullOrEmpty()) {
            binding.coachRowdsad.currentwieghtTxt.text = "N/A"
        } else {
            binding.coachRowdsad.currentwieghtTxt.text =
                data.firstOrNull()?.Current_Weight.plus("kg")
        }
        if (data.firstOrNull()?.Target_Weight.isNullOrEmpty()) {
            binding.coachRowdsad.tergetweightTxt.text = "N/A"
        } else {
            binding.coachRowdsad.tergetweightTxt.text =
                data.firstOrNull()?.Target_Weight.plus("kg")
        }

        if (data.firstOrNull()?.Total_Weight.isNullOrEmpty()) {
            binding.coachRowdsad.totalweightloss.text = "N/A"
        } else {
            binding.coachRowdsad.totalweightloss.text =
                data.firstOrNull()?.Total_Weight.plus("kg")
        }
        binding.coachRowdsad.targetweightlossLbl.text = getLabelText(data.firstOrNull())
        if (data.firstOrNull()?.Weight_Loss_Per_Week.isNullOrEmpty()) {
            binding.coachRowdsad.weeklyweightlossTxt.text = "N/A"
        } else {
            binding.coachRowdsad.weeklyweightlossTxt.text =
                data.firstOrNull()?.Weight_Loss_Per_Week.plus("kg")
        }
        if (data.firstOrNull()?.Goal_End_Date.isNullOrEmpty()) {
            binding.coachRowdsad.goalendTxt.text = "N/A"
        } else {
            binding.coachRowdsad.goalendTxt.text = convertDate(
                data.firstOrNull()?.Goal_End_Date ?: " ",
                Constants.YYYYMMDDTHH,
                Constants.DDMMMMYYYY
            )
        }
        var days = data.firstOrNull()?.No_Of_Days
        if (days.isNullOrEmpty()) {
            days = "N/A"
        }
        binding.coachRowdsad.noofdaysTxt.text = days
        binding.coachRowdsad.pieChartView.isRotationEnabled = false
    }

    private fun initDataToSeekbar() {
        progressItemList = ArrayList<ProgressItem>()
        // red span
        var mProgressItem = ProgressItem()
        mProgressItem.progressItemPercentage = 18.5f
        mProgressItem.color = Color.parseColor("#2B83ED")
        progressItemList?.add(mProgressItem)


        // blue span
        mProgressItem = ProgressItem()
        mProgressItem.progressItemPercentage = 6.5f
        mProgressItem.color = Color.parseColor("#09B967")
        progressItemList?.add(mProgressItem)
        // green span
        mProgressItem = ProgressItem()
        mProgressItem.progressItemPercentage = 5f
        mProgressItem.color = Color.parseColor("#FFCB01")
        progressItemList?.add(mProgressItem)

        //white span
        mProgressItem = ProgressItem()
        mProgressItem.progressItemPercentage = 5f
        mProgressItem.color = Color.parseColor("#ffa700")
        progressItemList?.add(mProgressItem)

        mProgressItem = ProgressItem()
        mProgressItem.progressItemPercentage = 5f
        mProgressItem.color = Color.parseColor("#ED8609")
        progressItemList?.add(mProgressItem)

        mProgressItem = ProgressItem()
        mProgressItem.progressItemPercentage = 5f
        mProgressItem.color = Color.parseColor("#DA736C")
        progressItemList?.add(mProgressItem)
        binding.seekBar0.initData(progressItemList)
        binding.seekBar0.progress = homeViewModel.userbmi.get()?.roundToInt() ?: 0
        binding.seekBar0.thumb = requireActivity().getDrawable(R.drawable.line)
        binding.seekBar0.isEnabled = false
        binding.weightstsTxt.text = getWeightType(homeViewModel.userbmi.get())
        binding.seekBar0.invalidate()
    }

    private fun getWeightType(bmiwiight: Double?): String {
        when (bmiwiight ?: 0.0) {
            in 0.0..18.5 -> {
                binding.weightstsTxt.setTextColor(requireActivity().getColor(R.color.color_2B83ED))
                return " UnderWeight"
            }

            in 18.5..24.9 -> {
                binding.weightstsTxt.setTextColor(requireActivity().getColor(R.color.primary))
                return " Normal"
            }

            in 25.0..29.9 -> {
                binding.weightstsTxt.setTextColor(requireActivity().getColor(R.color.color_FFCB01))
                return " OverWeight"
            }

            in 30.0..24.9 -> {
                binding.weightstsTxt.setTextColor(requireActivity().getColor(R.color.color_ED8609))
                return " Obese"
            }

            in 35.0..100.0 -> {
                binding.weightstsTxt.setTextColor(requireActivity().getColor(R.color.color_DA736C))
                return " Extremely Obese"
            }
        }
        return ""
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

                if ((AppPreference?.read(Constants.Screen_Code, "") ?: "") == "2005") {
                    if (PermissionsUtil.checkPermissions(requireActivity(), *permissions)) {
                        var bundle = Bundle()
                        var groupid = AppPreference.read(Constants.Focused_Group_Code, "0") ?: "0"
                        bundle.putLong(
                            Constants.GroupID, groupid.toLong()
                        )
                        bundle.putString(Constants.ISFROM, "Challenge")
                        bundle.putString(
                            Constants.Focused_Group_name,
                            AppPreference.read(Constants.Focused_Group_name, " ") ?: ""
                        )
                        findNavController().navigate(R.id.nav_chatFragment, bundle)
                        AppPreference.write(Constants.Screen_Code, "")
                        AppPreference.write("Focused_Group_Code", "")
                        AppPreference.write("Focused_Group_name", "")
                    } else {
                        showLongToast("Please enable permissions from settings to access this feature")
                    }
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

    fun openImage(imageUrl: String) {
        reportissueviewModel.image.set(imageUrl)
        val dialog = Dialog(requireActivity())
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val imaggebinding = ImageLayoutBinding.inflate(LayoutInflater.from(requireActivity()))
        dialog.setContentView(imaggebinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        );
        imaggebinding.viewModel = reportissueviewModel
        imaggebinding.closeImg.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getLabelText(caloridata: CaloriesCaliculationData?): String? {
        try {
            return if (!caloridata?.Target_Weight.isNullOrEmpty()) {
                if ((caloridata?.Target_Weight?.toDouble()
                        ?: 0.0) >= (caloridata?.Current_Weight?.toDouble() ?: 0.0)
                ) {
                    "Total Weight Gain"
                } else "Total Weight Loss"
            } else {
                "Total Weight Loss"
            }
        } catch (e: Exception) {
            return "Total Weight Loss"
        }
    }

    fun getDate(dateslist: ArrayList<String>): ArrayList<String> {
        val label = ArrayList<String>()
        for (i in 0 until dateslist.size) label.add(dateslist.get(i))
        return label
    }

    private fun getData(data: NetworkResult<CommonMasterResponse>, s: String): CommonDataResponse? {
        return data?.data?.data?.find {
            s == it.MstType
        }
    }

//    @SuppressLint("MissingPermission")
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    fun isValidMobileNumber() {
////        val subscriptionManager = SubscriptionManager.from(requireActivity())
////        var sim1 = subscriptionManager.getPhoneNumber(0)
////        var sim2 = subscriptionManager.getPhoneNumber(1)
////        Log.v("Kishore:", sim1)
////        Log.v("Kishore1:", sim2)
//        val slotIndex = 1
//        val subscriptionId = SubscriptionManager.from(requireActivity())
//            .getActiveSubscriptionInfoForSimSlotIndex(slotIndex).subscriptionId
//        try {
//            val c = Class.forName("android.telephony.TelephonyManager")
//            val m: Method =
//                c.getMethod("getSubscriberId", *arrayOf<Class<*>?>(Int::class.javaPrimitiveType))
//            val o: Any? = m.invoke(
//                requireActivity()
//                    .getSystemService(Context.TELEPHONY_SERVICE), arrayOf<Any>(subscriptionId)
//            )
//            val subscriberId = o as String
//            Log.v("Kishore1:", subscriberId)
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//
//    }


    @SuppressLint("HardwareIds")
    fun GetNumber(): String {
        // Check for necessary permissions
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // permission not granted
            return "0"
        }

        var phoneNumber: String? = null
        try {
            phoneNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13 and above (Tiramisu)
                Log.v("testing1", "one")
                val subscriptionMgr =
                    context?.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
                subscriptionMgr.getPhoneNumber(SubscriptionManager.DEFAULT_SUBSCRIPTION_ID)

            } else {
                // For devices below Android 13
                Log.v("testing1", "two")
                val phoneMgr =
                    context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                phoneMgr.line1Number
            }
        } catch (e: SecurityException) { // Handle permission related errors
            e.printStackTrace()
        } catch (e: Exception) { // Handle any other exceptions
            e.printStackTrace()
        }
        // Remove any leading "+" from phone number if it's present
        if (phoneNumber != null) {
            phoneNumber = phoneNumber.replace("+", "")
        }
        // Return the phone number if it's available, otherwise return null
        return (if (!TextUtils.isEmpty(phoneNumber)) phoneNumber else null)!!
    }

}