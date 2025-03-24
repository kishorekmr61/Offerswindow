package com.customer.offerswindow.ui.home

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.MenuProvider
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
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
import com.customer.offerswindow.utils.openNativeSharingDialog
import com.customer.offerswindow.utils.openURL
import com.customer.offerswindow.utils.openWhatsAppConversation
import com.customer.offerswindow.utils.resource.WidgetViewModel
import com.customer.offerswindow.utils.setToolbarVisibility
import com.customer.offerswindow.utils.setUpMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setUpPagingMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setUpViewPagerAdapter
import com.customer.offerswindow.utils.showLongToast
import com.customer.offerswindow.utils.showToast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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
    var mlocationList = arrayListOf<String>()
    var offertypeList = arrayListOf<FilterData>()
    var dashboaroffersList = arrayListOf<DashboardData>()
    var locationId = "0"
    var showroomid = "0"
    var service = "0"
    var iCityId = "0"
    var categoryid = "0"
    var customerid = "0"
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
//        if (isAccessTokenExpired()) {
        homeViewModel.getToken(
            AppPreference.read(Constants.LOGINUSERNAME, "8374810383") ?: "8374810383", "welcome"
        )
//        }
        homeViewModel.getMstData()
        binding.goldratesLyout.updatelblTxt.setOnClickListener {
            homeViewModel.isloading.set(true)
            homeViewModel.getGoldRatesData()
        }
        binding.viewallTxt.setOnClickListener {
            findNavController().navigate(R.id.nav_categories)
        }
        binding.locationTxt.setOnClickListener {
            activity?.let { it1 ->
                val modalBottomSheet = SpinnerBottomSheet.newInstance(Constants.STATUS,
                    binding.locationTxt.text.toString(), locationList, false, object :
                        OnItemSelectedListner {
                        override fun onItemSelectedListner(
                            titleData: SpinnerRowModel?,
                            datevalue: String
                        ) {
                            if (titleData != null) {
                                binding.locationTxt.setText(titleData.title)
                                homeViewModel.isloading.set(true)
                                locationId = titleData.mstCode
                                homeViewModel.nodata.set(false)
                                homeViewModel.getDashboardData(
                                    titleData.mstCode,
                                    locationId,
                                    service, categoryid, iCityId,
                                    AppPreference.read(Constants.USERUID, "0") ?: "0", "0"
                                )
                            }
                        }

                        override fun onItemmultipleSelectedListner(
                            titleData: ArrayList<SpinnerRowModel>?,
                            value: ArrayList<SpinnerRowModel>
                        ) {

                        }
                    })
                modalBottomSheet.show(it1.supportFragmentManager, SpinnerBottomSheet.TAG)
            }
        }
        binding.cityTxt.setOnClickListener {
            activity?.let { it1 ->
                val modalBottomSheet = SpinnerBottomSheet.newInstance(Constants.STATUS,
                    binding.cityTxt.text.toString(), cityList, false, object :
                        OnItemSelectedListner {
                        override fun onItemSelectedListner(
                            titleData: SpinnerRowModel?,
                            datevalue: String
                        ) {
                            if (titleData != null) {
                                binding.cityTxt.setText(titleData.title)
                                homeViewModel.isloading.set(true)
                                iCityId = titleData.mstCode
                                homeViewModel.nodata.set(false)
                                homeViewModel.getDashboardData(
                                    titleData.mstCode,
                                    locationId,
                                    service, categoryid, iCityId,
                                    AppPreference.read(Constants.USERUID, "0") ?: "0", "0"
                                )
                            }
                        }

                        override fun onItemmultipleSelectedListner(
                            titleData: ArrayList<SpinnerRowModel>?,
                            value: ArrayList<SpinnerRowModel>
                        ) {

                        }
                    })
                modalBottomSheet.show(it1.supportFragmentManager, SpinnerBottomSheet.TAG)
            }
        }
        binding.searchedit.setOnClickListener {
            activity?.let { it1 ->
                val modalBottomSheet = SpinnerBottomSheet.newInstance(Constants.STATUS,
                    binding.searchedit.text.toString(), showroomList, false, object :
                        OnItemSelectedListner {
                        override fun onItemSelectedListner(
                            titleData: SpinnerRowModel?,
                            datevalue: String
                        ) {
                            if (titleData != null) {
                                binding.searchedit.setText(titleData.title)
                                showroomid = titleData.mstCode
                                homeViewModel.isloading.set(true)
                                homeViewModel.nodata.set(false)
                                if (binding.locationTxt.text.toString().isNullOrEmpty()) {
                                    binding.clearImg.visibility = View.GONE
                                } else {
                                    binding.clearImg.visibility = View.VISIBLE
                                }
                                homeViewModel.getDashboardData(
                                    titleData.mstCode,
                                    locationId,
                                    service, categoryid, iCityId,
                                    AppPreference.read(Constants.USERUID, "0") ?: "0", "0"
                                )
                            }
                        }

                        override fun onItemmultipleSelectedListner(
                            titleData: ArrayList<SpinnerRowModel>?,
                            value: ArrayList<SpinnerRowModel>
                        ) {

                        }
                    })
                modalBottomSheet.show(it1.supportFragmentManager, SpinnerBottomSheet.TAG)
            }
        }
        binding.clearImg.setOnClickListener {
            binding.searchedit.text = ""
            showroomid = "0"
            homeViewModel.isloading.set(true)
            binding.clearImg.visibility = View.GONE
            homeViewModel.nodata.set(false)
            homeViewModel.getDashboardData(
                showroomid,
                locationId,
                service, categoryid, iCityId,
                AppPreference.read(Constants.USERUID, "0") ?: "0", "0"
            )
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
                        homeViewModel.getMstData()
                        homeViewModel.getGoldRatesData()
                        homeViewModel.getShowRoomsData("0", "0", "0")
                        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                            AppPreference.read(Constants.MOBILENO, "")
                                ?.let {
                                    homeViewModel.getUserInfo(
                                        AppPreference.read(
                                            Constants.MOBILENO,
                                            ""
                                        ) ?: ""
                                    )
                                }
                        } else {
                            binding.loginusername.text = getString(R.string.signin)
                            homeViewModel.getDashboardData(
                                showroomid,
                                locationId,
                                service, categoryid, iCityId,
                                AppPreference.read(Constants.USERUID, "0") ?: "0",
                                "0"
                            )
                        }
                        homeViewModel.nodata.set(false)

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
                        AppPreference.write(
                            Constants.MOBILENO, resposnes?.Data?.firstOrNull()?.Mobile_No ?: ""
                        )
                        AppPreference.write(
                            Constants.PROFILEPIC,
                            resposnes?.Data?.firstOrNull()?.Cust_Image_URL ?: ""
                        )
                        homeViewModel.profilepic.set(AppPreference.read(Constants.PROFILEPIC, ""))
                        homeViewModel.getDashboardData(
                            showroomid,
                            locationId,
                            service, categoryid, iCityId,
                            AppPreference.read(Constants.USERUID, "0") ?: "0",
                            "0"
                        )
//                        homeViewModel.profilepic.set(
//                            resposnes.Data?.firstOrNull()?.Cust_Image_URL ?: ""
//                        )

//                        homeViewModel.profilepic.set("https://offerswindow.com/Offers_Window_API/Uploads/Customers/Photo/24_23_03_2025_16_51_01.jpg")
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
                    categoryList.clear()
                    offertypeList.clear()
                    locationList.clear()
                    categoryList.clear()
                    cityList.clear()
                    locationList.add(SpinnerRowModel("All", false, false, mstCode = "0"))
                    cityList.add(SpinnerRowModel("All", false, false, mstCode = "0"))
                    response.data?.let { resposnes ->
                        categoryList.add(
                            CategoriesData(
                                "https://cdn.pixabay.com/photo/2021/10/11/23/49/app-6702045_1280.png",
                                "All",
                                "0",
                                true
                            )
                        )
                        offertypeList.add(FilterData("All", "0", true))
                        response?.data?.data?.forEach {
                            if (it.MstType == "Service") {
                                categoryList.add(
                                    CategoriesData(
                                        it.Image_path, it.MstDesc, it.MstCode
                                    )
                                )
                            }
                            if (it.MstType == "Location") {
                                locationList.add(
                                    SpinnerRowModel(
                                        it.MstDesc,
                                        false,
                                        false,
                                        mstCode = it.MstCode
                                    )
                                )
                            }
                            if (it.MstType == "Offer_Type") {
                                offertypeList.add(FilterData(it.MstDesc, it.MstCode))
                            }
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
                        }
                    }
                    binding.cityTxt.text = cityList.firstOrNull()?.title
                    binding.locationTxt.text = cityList.firstOrNull()?.title
                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
                }

                else -> {}
            }
        }
        homeViewModel.goldratesdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    homeViewModel.isloading.set(false)
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
                    }
                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
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
//                        homeViewModel.nodata.set(adapter.itemCount == 0)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }
        homeViewModel.showRoomdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    homeViewModel.isloading.set(false)
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
                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
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
                                if (datavalues?.isfavourite == true) {
                                    datavalues.isfavourite = false
                                } else {
                                    datavalues?.isfavourite = true
                                }
                                var postdata = PostWishlist(
                                    datavalues.offertypecode,
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
                                getUserIntrestOnclick("Share")
                                activity?.openNativeSharingDialog(datavalues.Website_link)
                            } else {
                                findNavController().navigate(R.id.nav_sign_in)
                            }
                        }

                        R.id.call_img -> {
                            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                                getUserIntrestOnclick("Call")
                                activity?.openDialPad(datavalues.contact)
                            } else {
                                findNavController().navigate(R.id.nav_sign_in)
                            }
                        }

                        R.id.directions_img -> {
                            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                            getUserIntrestOnclick("Direction")
                            activity?.navigateToGoogleMap(datavalues.GoogleLocation)
                            } else {
                                findNavController().navigate(R.id.nav_sign_in)
                            }
                        }

                        R.id.website_img -> {
                            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                                getUserIntrestOnclick("Website")
                                openURL(Uri.parse(datavalues?.Website_link ?: ""))
                            } else {
                                findNavController().navigate(R.id.nav_sign_in)
                            }
                        }

                        R.id.whatsapp_img -> {
                            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                                getUserIntrestOnclick("Whatsapp")
                                activity?.openWhatsAppConversation(datavalues.contact, "")
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
            if (it.source.refresh is LoadState.NotLoading &&
                it.append.endOfPaginationReached &&
                adapter.itemCount < 1
            ) {
                homeViewModel.nodata.set(adapter.itemCount == 0)
            }
        }

        val concatAdapter = adapter.withLoadStateFooter(
            footer = MultiViewPagingRecyclerFooterAdapter()
        )
        binding.rvOfferslist.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }


        var previouscat = 0
        categoryList.firstOrNull()?.isselected = true
        binding.rvCategories.setUpMultiViewRecyclerAdapter(
            categoryList
        ) { item: CategoriesData, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            binder.setVariable(BR.onItemClick, View.OnClickListener {
                when (it.id) {
                    R.id.category_item -> {
                        categoryList[previouscat].isselected = false
                        previouscat = position
                        categoryList[previouscat].isselected = true
                        homeViewModel.isloading.set(true)
                        service = item.category_id
                        homeViewModel.nodata.set(false)
                        homeViewModel.getDashboardData(
                            showroomid,
                            locationId,
                            service, categoryid, iCityId,
                            AppPreference.read(Constants.USERUID, "0") ?: "0", "0"
                        )
                        binding.rvCategories.notifyDataChange()
                    }
                }
                binder.executePendingBindings()
            })
        }
        var previousfilter = 0
        offertypeList.firstOrNull()?.filetrselection = true
        binding.rvFilter.setUpMultiViewRecyclerAdapter(
            offertypeList
        ) { item: FilterData, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            binder.setVariable(BR.onItemClick, View.OnClickListener {
                when (it.id) {
                    R.id.img_card -> {
                        offertypeList[previousfilter].filetrselection = false
                        previousfilter = position
                        offertypeList[previousfilter].filetrselection = true
                        categoryid = item.filtercode
                        homeViewModel.isloading.set(true)
                        homeViewModel.nodata.set(false)
                        homeViewModel.getDashboardData(
                            showroomid,
                            locationId,
                            service, categoryid, iCityId,
                            AppPreference.read(Constants.USERUID, "0") ?: "0", "0"
                        )
                        binding.rvFilter.notifyDataChange()
                    }
                }
                binder.executePendingBindings()
            })
        }
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

    fun getUserIntrestOnclick(flag: String) {
        var posuserintrest = PostUserIntrest(
            showroomid,
            locationId,
            "",
            flag,
            AppPreference.read(Constants.USERUID, "") ?: ""
        )
        homeViewModel.getUserInterest(posuserintrest)
    }
}