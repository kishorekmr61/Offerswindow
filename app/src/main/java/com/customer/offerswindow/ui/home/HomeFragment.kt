package com.customer.offerswindow.ui.home

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.MenuProvider
import androidx.core.view.isEmpty
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.customer.offerswindow.BR
import com.customer.offerswindow.BuildConfig
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentHomeCustomerBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.SpinnerRowModel
import com.customer.offerswindow.model.customersdata.PostUserIntrest
import com.customer.offerswindow.model.customersdata.PostWishlist
import com.customer.offerswindow.model.dashboard.CategoriesData
import com.customer.offerswindow.model.dashboard.DashboardData
import com.customer.offerswindow.model.dashboard.FilterData
import com.customer.offerswindow.model.dashboard.Images
import com.customer.offerswindow.model.masters.CommonDataResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.utils.MultiViewPagingRecyclerAdapter
import com.customer.offerswindow.utils.MultiViewPagingRecyclerFooterAdapter
import com.customer.offerswindow.utils.PermissionsUtil
import com.customer.offerswindow.utils.bottomsheet.OnItemSelectedListner
import com.customer.offerswindow.utils.bottomsheet.SpinnerBottomSheet
import com.customer.offerswindow.utils.navigateToGoogleMap
import com.customer.offerswindow.utils.notifyDataChange
import com.customer.offerswindow.utils.openDialPad
import com.customer.offerswindow.utils.openURL
import com.customer.offerswindow.utils.openWhatsAppConversation
import com.customer.offerswindow.utils.openYoutube
import com.customer.offerswindow.utils.resource.WidgetViewModel
import com.customer.offerswindow.utils.setToolbarVisibility
import com.customer.offerswindow.utils.setUpMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setUpPagingMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setUpViewPagerAdapter
import com.customer.offerswindow.utils.shareImageFromUrl
import com.customer.offerswindow.utils.showLongToast
import com.customer.offerswindow.utils.showToast
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.onesignal.OneSignal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment(), MenuProvider {

    private val homeViewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeCustomerBinding? = null
    private val binding get() = _binding!!
    private val vm: DashBoardViewModel by activityViewModels()
    var categoryList = ArrayList<CategoriesData>()
    var locationList = arrayListOf<SpinnerRowModel>()
    var cityList = arrayListOf<SpinnerRowModel>()
    var showroomList = arrayListOf<SpinnerRowModel>()
    var offertypeList = arrayListOf<FilterData>()
    var otherServicesList = arrayListOf<CommonDataResponse>()
    private lateinit var imageViewPagerAdapter: SliderAdapter
    var locationId = "0"
    var showroomid = "0"
    var service = "0"
    var iCityId = "30"
    var cityname = "Hyderabad"
    var categoryid = "0"
    var customerid = "0"
    var previouscat = 0
    private lateinit var adapter: PagingDataAdapter<WidgetViewModel, MultiViewPagingRecyclerAdapter.ViewHolder<ViewDataBinding>>

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        setObserver()
        setRecyclervewData()
        vm.hidetoolbar.value = false
        homeViewModel.isloading.set(true)
        binding.versionTextview.text =
            getString(R.string.version).plus(" ( " + BuildConfig.VERSION_NAME + " ) ")
        handleNotificationClick()
        setListeners()
        vm.isvisble.value = true
        if (AppPreference.read(Constants.TOKEN, "").isNullOrEmpty()) {
            homeViewModel.getToken(
                AppPreference.read(Constants.LOGINUSERNAME, Constants.DEFAULTUSERMOBILE)
                    ?: Constants.DEFAULTUSERMOBILE,
                AppPreference.read(Constants.LOGINPASSWORD, Constants.DEFAULTUSERKEY)
                    ?: Constants.DEFAULTUSERKEY
            )
        } else {
            homeViewModel.isloading.set(true)
            homeViewModel.getMstData()
        }
//        }

        binding.viewallTxt.setOnClickListener {
            findNavController().navigate(R.id.nav_categories)
        }
        binding.locationTxt.setOnClickListener {
            locationList = homeViewModel.getLocationWIthFromCities(iCityId)
            if (locationList.isEmpty()) {
                showToast("No data")
            } else {
                activity?.let { it1 ->
                    val modalBottomSheet = SpinnerBottomSheet.newInstance(
                        Constants.FILTER,
                        binding.locationTxt.text.toString(), locationList, false, object :
                            OnItemSelectedListner {
                            override fun onItemSelectedListner(
                                titleData: SpinnerRowModel?,
                                datevalue: String
                            ) {
                                if (titleData != null) {
                                    binding.locationTxt.setText(titleData.title)
                                    locationId = titleData.mstCode
                                    dashboardOffersList()
                                }
                            }

                            override fun onItemmultipleSelectedListner(
                                titleData: ArrayList<SpinnerRowModel>?,
                                value: ArrayList<SpinnerRowModel>
                            ) {

                            }
                        }, headerlbl = "Location"
                    )
                    modalBottomSheet.show(it1.supportFragmentManager, SpinnerBottomSheet.TAG)
                }
            }
        }
        binding.cityTxt.setOnClickListener {
            if (cityList.isNullOrEmpty()) {
                showToast("No data")
            } else {
                activity?.let { it1 ->
                    val modalBottomSheet = SpinnerBottomSheet.newInstance(
                        Constants.FILTER,
                        binding.cityTxt.text.toString(), cityList, false, object :
                            OnItemSelectedListner {
                            override fun onItemSelectedListner(
                                titleData: SpinnerRowModel?,
                                datevalue: String
                            ) {
                                if (titleData != null) {
                                    binding.cityTxt.setText(titleData.title)
                                    homeViewModel.isloading.set(true)

                                    locationList = homeViewModel.getLocationWIthFromCities(iCityId)
                                    locationId = "0"
                                    if (locationList.isNotEmpty())
                                        binding.locationTxt.text = locationList[0].title

                                    iCityId = titleData.mstCode
                                    dashboardOffersList()
                                }
                            }

                            override fun onItemmultipleSelectedListner(
                                titleData: ArrayList<SpinnerRowModel>?,
                                value: ArrayList<SpinnerRowModel>
                            ) {

                            }
                        }, headerlbl = "City"
                    )
                    modalBottomSheet.show(it1.supportFragmentManager, SpinnerBottomSheet.TAG)
                }
            }
        }
        binding.searchedit.setOnClickListener {
            if (showroomList.isEmpty()) {
                showToast("No data to search")
            } else {
                activity?.let { it1 ->
                    val modalBottomSheet = SpinnerBottomSheet.newInstance(
                        Constants.FILTER,
                        binding.searchedit.text.toString(), showroomList, false, object :
                            OnItemSelectedListner {
                            override fun onItemSelectedListner(
                                titleData: SpinnerRowModel?,
                                datevalue: String
                            ) {
                                if (titleData != null) {
                                    binding.searchedit.setText(titleData.title)
                                    showroomid = titleData.mstCode
                                    if (binding.locationTxt.text.toString().isNullOrEmpty()) {
                                        binding.clearImg.visibility = View.GONE
                                    } else {
                                        binding.clearImg.visibility = View.VISIBLE
                                    }
                                    dashboardOffersList()
                                }
                            }

                            override fun onItemmultipleSelectedListner(
                                titleData: ArrayList<SpinnerRowModel>?,
                                value: ArrayList<SpinnerRowModel>
                            ) {

                            }
                        }, headerlbl = "Showrooms"
                    )
                    modalBottomSheet.show(it1.supportFragmentManager, SpinnerBottomSheet.TAG)
                }
            }
        }
        binding.clearImg.setOnClickListener {
            binding.searchedit.text = ""
            showroomid = "0"
            binding.clearImg.visibility = View.GONE
            dashboardOffersList()
        }
        binding.goldratesLyout.goldcLyout.setOnClickListener {
            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                openURL(Uri.parse(AppPreference.read(Constants.GOLDTRENDREPORT, "") ?: ""))
            } else {
                findNavController().navigate(R.id.nav_sign_in)
            }
        }
    }

    private fun getDataIntent(item: CategoriesData, position: Int, mpreviouscat: Int) {
        if (arguments?.getString("ISFROM") == "CATEGORY") {
            var categoryselected = Gson().fromJson(
                arguments?.getString("Category"),
                CategoriesData::class.java
            )
            previouscat = mpreviouscat
            categoryList.forEach { _ ->
                if (categoryselected.category_id == item.category_id) {
                    service = item.category_id ?: categoryid
                    categoryList[previouscat].isselected = false
                    previouscat = position
                    categoryList[previouscat].isselected = true
                    item.isselected = true
                    binding.rvCategories.scrollToPosition(position)
                    return
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(100)
            activity?.setToolbarVisibility(View.GONE)
        }
    }


    private fun setObserver() {
        homeViewModel.tokenResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        binding.goldratesLyout.updatelblTxt.setOnClickListener {
                            homeViewModel.isloading.set(true)
                            homeViewModel.getGoldRatesData()
                        }
                        homeViewModel.isloading.set(true)
                        homeViewModel.getMstData()

                    }
                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
                }

                else -> {}
            }
        }
        homeViewModel.customerinfo.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        homeViewModel.isloading.set(false)
                        binding.customerdata = resposnes.Data?.firstOrNull()
                        AppPreference.write(
                            Constants.NAME, resposnes?.Data?.firstOrNull()?.Cust_Name ?: ""
                        )
                        AppPreference.write(
                            Constants.USERUID, resposnes?.Data?.firstOrNull()?.Cust_UID ?: "0"
                        )
                        OneSignal.setEmail(
                            resposnes.Data?.firstOrNull()?.Email_ID ?: ""
                        );
                        OneSignal.sendTag(
                            "CustomerUID",
                            resposnes.Data?.firstOrNull()?.Cust_UID ?: ""
                        )
                        AppPreference.write(
                            Constants.MOBILENO, resposnes?.Data?.firstOrNull()?.Mobile_No ?: ""
                        )
                        AppPreference.write(
                            Constants.PIN, resposnes?.Data?.firstOrNull()?.Pin_No ?: ""
                        )
                        AppPreference.write(
                            Constants.PROFILEPIC,
                            resposnes?.Data?.firstOrNull()?.Cust_Image_URL ?: ""
                        )
                        homeViewModel.profilepic.set(AppPreference.read(Constants.PROFILEPIC, ""))
                        locationId = resposnes.Data?.firstOrNull()?.Sub_Location_Code ?: locationId
                        iCityId = resposnes.Data?.firstOrNull()?.Location_Code ?: iCityId
                        binding.cityTxt.text = resposnes.Data?.firstOrNull()?.Location_Desc
                        binding.locationTxt.text = resposnes.Data?.firstOrNull()?.Sub_Location_Desc
                        dashboardOffersList()
                    }
                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
                }

                else -> {}
            }
        }
        homeViewModel.masterdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    otherServicesList.clear()
                    cityList.clear()
                    var imagelist = ArrayList<String>()
                    cityList.add(SpinnerRowModel("All", false, false, mstCode = "0"))
                    response.data?.let { resposnes ->
                        response?.data?.data?.forEach {
                            if (it.MstType == "City") {
                                cityList.add(
                                    SpinnerRowModel(
                                        it.MstDesc,
                                        false,
                                        false,
                                        mstCode = it.MstCode
                                    )
                                )
                            }

//                            locationList = homeViewModel.getLocationWIthFromCities(iCityId)
                            if (it.MstType == "Web_Link_Offers") {
                                otherServicesList.add(
                                    CommonDataResponse(
                                        MstCode = it.MstCode,
                                        MstDesc = it.MstDesc,
                                        Image_path = it.Image_path, URL = it.URL
                                    )
                                )
                            }
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
                    homeViewModel.getGoldRatesData()
                    if (!otherServicesList.isNullOrEmpty()) {
                        binding.otherserviceLyout.visibility = View.VISIBLE
                        loadViewPager(otherServicesList)
                    } else {
                        binding.otherserviceLyout.visibility = View.GONE
                    }
                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
                    homeViewModel.isloading.set(true)
                    homeViewModel.getOfferServiceDetails("0")
                }

                else -> {}
            }
        }

        homeViewModel.goldratesdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        val gold24 = "Gold 24K :  ".plus(
                            ("₹ " + resposnes.data.firstOrNull()?.Gold_24c)
                        )
                        val gold22 = " Gold 22K :  ".plus(
                            ("₹ " + resposnes.data.firstOrNull()?.Gold_22c)
                        )
                        val gold18 = " Gold 28K : ".plus(
                            ("₹ " + resposnes.data.firstOrNull()?.Gold_18c)
                        )
                        val silver = " Silver :".plus(
                            ("₹ " + resposnes.data.firstOrNull()?.Silver)
                        )
                        val diamond = " Diamond :  ".plus(
                            ("₹ " + resposnes.data.firstOrNull()?.Diamonds)
                        )
                        binding.pricerates = resposnes.data.firstOrNull()
                        binding.goldratesLyout.goldcLyout.visibility = View.VISIBLE
                        binding.goldratesTxt.text =
                            gold24.plus(gold22).plus(gold18).plus(silver).plus(diamond)
                        binding.goldratesTxt.isSelected = true
                        binding.goldratesTxt.visibility = View.VISIBLE

                    }
                    homeViewModel.getOfferServiceDetails("0")
                }

                is NetworkResult.Error -> {
                    homeViewModel.getOfferServiceDetails("0")
                }

                else -> {}
            }
        }

        homeViewModel.dashboardresponse.observe(viewLifecycleOwner) { response ->
            response.let { resposnes ->
                homeViewModel.isloading.set(false)
                lifecycleScope.launch {
                    try {
                        adapter.run { submitData(response as PagingData<WidgetViewModel>) }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }
        homeViewModel.showRoomdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    showroomList.clear()
                    response.data?.Data?.forEach {
                        showroomList.add(
                            SpinnerRowModel(
                                it.Showroom_Name,
                                false,
                                false,
                                mstCode = it.Showroom_UID
                            )
                        )
                    }
                    if (!binding.offertypeChips.isEmpty()) {
                        val firstChip = binding.offertypeChips.getChildAt(0) as? Chip
                        firstChip?.isChecked = true
                    }
                    getLoginuserData()
                }

                is NetworkResult.Error -> {
                    if (!binding.offertypeChips.isEmpty()) {
                        val firstChip = binding.offertypeChips.getChildAt(0) as? Chip
                        firstChip?.isChecked = true
                    }
                    getLoginuserData()
                }

                else -> {}
            }
        }
        homeViewModel.postwishlistdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    homeViewModel.isloading.set(false)
                    showLongToast(response.message ?: "")
                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
                    showLongToast(response.message ?: "")
                }

                else -> {}
            }
        }

        homeViewModel.offertypeResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    offertypeList.clear()
                    offertypeList.add(FilterData("All", "0", true))
                    response.data?.Data?.forEach {
                        offertypeList.add(FilterData(it.Mst_Desc, it.Mst_Code))
                    }
                    loadFilters()
                    homeViewModel.getShowRoomsData("0", "0", "0")
                }

                is NetworkResult.Error -> {
                    homeViewModel.getShowRoomsData("0", "0", "0")
                }

                else -> {}
            }
        }
        homeViewModel.serviceResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    categoryList.clear()
                    categoryList.add(
                        CategoriesData(
                            false,
                            "https://cdn.pixabay.com/photo/2021/10/11/23/49/app-6702045_1280.png",
                            "All",
                            "0",
                        )
                    )
                    response.data?.Data?.forEach {
                        categoryList.add(
                            CategoriesData(
                                false,
                                it.Image_path,
                                it.Mst_Desc,
                                it.Mst_Code,

                                )
                        )
                    }
                    loadServices()
                    homeViewModel.getOfferTypeResponse("0")

                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
                    homeViewModel.isloading.set(true)
                    homeViewModel.getOfferTypeResponse("0")
                }

                else -> {}
            }
        }

    }

    private fun getLoginuserData() {
        AppPreference.read(Constants.MOBILENO, "")
            ?.let {
                homeViewModel.getUserInfo(
                    AppPreference.read(
                        Constants.MOBILENO,
                        ""
                    ) ?: ""
                )
            }
        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
            binding.loginusername.text = AppPreference.read(Constants.NAME, "")
        } else {
            binding.loginusername.text = getString(R.string.signin)
            binding.locationTxt.text = "All"
        }
    }

    fun dashboardOffersList() {
        homeViewModel.nodata.set(false)
        homeViewModel.isloading.set(true)
        homeViewModel.getDashboardData(
            showroomid,
            locationId,
            service, categoryid, iCityId,
            AppPreference.read(Constants.USERUID, "0") ?: "0",
            "0"
        )
    }

    private fun setRecyclervewData() {
        adapter =
            binding.rvOfferslist.setUpPagingMultiViewRecyclerAdapter { item: WidgetViewModel, binder: ViewDataBinding, position: Int ->
                binder.setVariable(BR.item, item)
                var datavalues = item as DashboardData
                var viewpager = binder.root.findViewById<ViewPager2>(R.id.viewPager)
                var tabview = binder.root.findViewById<TabLayout>(R.id.tab_layout)
                viewpager.setUpViewPagerAdapter(
                    getImageList(datavalues.ImagesList) ?: arrayListOf()
                ) { item: Images, binder: ViewDataBinding, position: Int ->
                    binder.setVariable(BR.item, item)
                    binder.setVariable(BR.onItemClick, View.OnClickListener {
                        when (it.id) {
                            R.id.img -> {
                                navigateOfferDeatils(datavalues)
                            }
                        }
                        binder.executePendingBindings()
                    })
                }
                TabLayoutMediator(tabview, viewpager) { tab, position ->
                }.attach()
                binder.setVariable(BR.onItemClick, View.OnClickListener {
                    when (it.id) {
                        R.id.favourite -> {
                            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                                if (datavalues.isfavourite) {
                                    datavalues.isfavourite = false
                                } else {
                                    datavalues.isfavourite = true
                                }
                                val postdata = PostWishlist(
                                    datavalues.id,
                                    AppPreference.read(Constants.USERUID, "") ?: ""
                                )
                                homeViewModel.isloading.set(true)
                                homeViewModel.postWishListItem(postdata)
                                binding.rvOfferslist.notifyDataChange()
                            } else {
                                findNavController().navigate(R.id.nav_sign_in)
                            }
                        }

                        R.id.title_txt -> {
                            navigateOfferDeatils(datavalues)
                        }

                        R.id.share_img -> {
                            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                                getUserIntrestOnclick("Share", datavalues)
                                activity?.shareImageFromUrl(
                                    requireActivity(),
                                    datavalues.id,
                                    datavalues.ImagesList?.firstOrNull()?.imagepath ?: ""
                                )
                            } else {
                                findNavController().navigate(R.id.nav_sign_in)
                            }
                        }

                        R.id.call_img -> {
                            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                                getUserIntrestOnclick("Call", datavalues)
                                activity?.openDialPad(datavalues.contact)
                            } else {
                                findNavController().navigate(R.id.nav_sign_in)
                            }
                        }

                        R.id.directions_img -> {
                            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                                getUserIntrestOnclick("Direction", datavalues)
                                activity?.navigateToGoogleMap(datavalues.GoogleLocation)
                            } else {
                                findNavController().navigate(R.id.nav_sign_in)
                            }
                        }

                        R.id.website_img -> {
                            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                                getUserIntrestOnclick("Website", datavalues)
                                openURL(Uri.parse(datavalues?.Website_link ?: ""))
                            } else {
                                findNavController().navigate(R.id.nav_sign_in)
                            }
                        }

                        R.id.whatsapp_img -> {
                            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                                getUserIntrestOnclick("Whatsapp", datavalues)
                                activity?.openWhatsAppConversation(
                                    datavalues.contact,
                                    getString(R.string.whatsappmsg)
                                )
                            } else {
                                findNavController().navigate(R.id.nav_sign_in)
                            }
                        }

                        R.id.video_img -> {
                            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                                getUserIntrestOnclick("Video", datavalues)
                                activity?.openYoutube(datavalues.Video_Link)
                            } else {
                                findNavController().navigate(R.id.nav_sign_in)
                            }
                        }
                    }
                    binder.executePendingBindings()
                })
                binder.executePendingBindings()
            }

        adapter.addLoadStateListener {
            if (adapter.snapshot().items.isEmpty()) {
                binding.nodataavaliable.nodataLayout.visibility = View.VISIBLE
                binding.rvOfferslist.visibility = View.GONE
            } else {
                binding.nodataavaliable.nodataLayout.visibility = View.GONE
                binding.rvOfferslist.visibility = View.VISIBLE
            }
        }

        val concatAdapter = adapter.withLoadStateFooter(
            footer = MultiViewPagingRecyclerFooterAdapter()
        )
        binding.rvOfferslist.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }

    }

    fun loadFilters() {

        offertypeList.firstOrNull()?.filetrselection = true
        binding.offertypeChips.isSingleLine = true
        binding.offertypeChips.isSingleSelection = true
        binding.offertypeChips.setOnCheckedStateChangeListener { group, checkedIds ->
            var selectedchip = ""
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i) as Chip
                chip.isChecked = checkedIds.contains(chip.id)
                if (chip.isChecked) {
                    selectedchip = chip.id.toString()
                    chip.chipStrokeColor =
                        activity?.let { it1 -> ColorStateList.valueOf(it1.getColor(R.color.primary)) }
                    chip.chipBackgroundColor =
                        activity?.let { it1 -> ColorStateList.valueOf(it1.getColor(R.color.primary)) }
                    activity?.getColor(R.color.white)?.let { it1 -> chip.setTextColor(it1) }
                } else {
                    chip.chipStrokeColor =
                        activity?.let { it1 -> ColorStateList.valueOf(it1.getColor(R.color.transparent)) }
                    chip.chipBackgroundColor =
                        activity?.let { it1 -> ColorStateList.valueOf(it1.getColor(R.color.white)) }
                    activity?.getColor(R.color.black)?.let { it1 -> chip.setTextColor(it1) }
                }
            }

            categoryid = selectedchip
            dashboardOffersList()
        }
        binding.offertypeChips.removeAllViews()
        var i = 0
        offertypeList.forEach {
            binding.offertypeChips.addView(createChip(it.filtercategory_desc ?: "", i))
            binding.executePendingBindings()
            i++
        }
    }

    fun loadServices() {

        categoryList.firstOrNull()?.isselected = true
        if (!categoryList.isNullOrEmpty()) {
            binding.categoriesTxt.visibility = View.VISIBLE
            binding.viewallTxt.visibility = View.VISIBLE
        }
        binding.rvCategories.setUpMultiViewRecyclerAdapter(
            categoryList
        ) { item: CategoriesData, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            getDataIntent(item, position, previouscat)
            if (arguments?.getString("ISFROM") == "CATEGORY") {
                dashboardOffersList()
            }
            binder.setVariable(BR.onItemClick, View.OnClickListener {
                when (it.id) {
                    R.id.category_item -> {
                        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                            categoryList[previouscat].isselected = false
                            previouscat = position
                            categoryList[previouscat].isselected = true
                            homeViewModel.isloading.set(true)
                            service = item.category_id ?: categoryid
                            arguments?.putString("ISFROM", "")
                            homeViewModel.getOfferTypeResponse(item.category_id ?: categoryid)
                            dashboardOffersList()
                        } else {

                            findNavController().navigate(R.id.nav_sign_in)
                        }

                        binding.rvCategories.notifyDataChange()
                    }
                }
                binder.executePendingBindings()
            })
        }
    }


    private fun loadViewPager(commonDataResponse: ArrayList<CommonDataResponse>) {
        val imageList = ArrayList<SlideModel>()
        commonDataResponse.forEach {
            imageList.add(SlideModel(it.Image_path, it.MstDesc, ScaleTypes.FIT))
        }
        if (!imageList.isNullOrEmpty()) {
            binding.slider.setImageList(imageList)
        }
        binding.slider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                navigatepagerClick(position)
            }

            override fun doubleClick(position: Int) {

            }
        })

    }

    private fun navigateOfferDeatils(datavalues: DashboardData) {
        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
            var bundle = Bundle()
            bundle.putString("OfferID", datavalues.id)
            bundle.putString(
                "Imagepath",
                datavalues.ImagesList?.firstOrNull()?.imagepath
            )
            findNavController().navigate(R.id.nav_offer_details, bundle)
        } else {

            findNavController().navigate(R.id.nav_sign_in)
        }
    }

    private fun getImageList(imagesList: ArrayList<Images>?): ArrayList<Images>? {
        if (imagesList.isNullOrEmpty()) {
            imagesList?.add(Images("0", ""))
        }
        return imagesList
    }


    private fun handleNotificationClick() {

        try {
            if (AppPreference?.read("ISFROM", "") == "NOTIFICATIONS") {
                AppPreference.write("ISFROM", "")

                if ((AppPreference?.read(Constants.Screen_Code, "") ?: "") == "2003") {
                    findNavController().navigate(R.id.nav_notifications)
                    AppPreference.write(Constants.Screen_Code, "")
                }
                if ((AppPreference?.read(Constants.Screen_Code, "") ?: "") == "2004") {
                    findNavController().navigate(R.id.nav_notifications)
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


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getData(data: NetworkResult<CommonMasterResponse>, s: String): CommonDataResponse? {
        return data.data?.data?.find {
            s == it.MstType
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_notification) {
            showToast("Notification clicked")
            return true
        }
        return false
    }

    private fun setListeners() {
        binding.setVariable(BR.onItemClick, View.OnClickListener {
            when (it.id) {
                R.id.imageView14 -> {
                    if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                        findNavController().navigate(R.id.nav_customerProfileFragment)
                    } else {
                        var bundle = Bundle()
                        bundle.putBoolean("isFrom", true)
                        findNavController().navigate(R.id.nav_sign_in, bundle)
                    }
                }

                R.id.notofication_img -> {
                    if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                        findNavController().navigate(R.id.nav_notifications)
                    } else {
                        var bundle = Bundle()
                        bundle.putBoolean("isFrom", true)
                        findNavController().navigate(R.id.nav_sign_in, bundle)
                    }
                }

                R.id.favourite_img -> {
                    if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                        findNavController().navigate(R.id.nav_wishlist)
                    } else {

                        var bundle = Bundle()
                        bundle.putBoolean("isFrom", true)
                        findNavController().navigate(R.id.nav_sign_in, bundle)
                    }
                }
            }

        })
    }

    fun getUserIntrestOnclick(flag: String, datavalues: DashboardData) {
        var posuserintrest = PostUserIntrest(
            datavalues.showroomid,
            datavalues.locationid,
            datavalues.id,
            flag,
            AppPreference.read(Constants.USERUID, "") ?: ""
        )
        homeViewModel.getUserInterest(posuserintrest)
    }


    fun navigatepagerClick(position: Int) {
        var bundle = Bundle()
        bundle.putString(
            Constants.WEB_URL,
            otherServicesList[position].URL
        )
        findNavController().navigate(R.id.nav_webview, bundle)
    }


    private fun createChip(label: String, indexvalue: Int): Chip {
        val chip = Chip(activity, null, R.style.My_Widget_MaterialComponents_Chip_Choice)
        chip.id = indexvalue
        chip.tag = indexvalue
        chip.text = label
        chip.isCloseIconVisible = false
        chip.isChipIconVisible = false
        chip.isCheckable = true
        chip.isClickable = true
        chip.gravity = Gravity.CENTER
        chip.isCheckedIconVisible = false
        chip.isSingleLine = true
        return chip
    }
}
