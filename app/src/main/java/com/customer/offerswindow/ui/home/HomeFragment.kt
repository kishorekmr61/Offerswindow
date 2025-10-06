package com.customer.offerswindow.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.core.view.isEmpty
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.customer.offerswindow.ui.onboarding.OnBoardingActivity
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
import com.customer.offerswindow.utils.shareImageFromUrl
import com.customer.offerswindow.utils.showLongToast
import com.customer.offerswindow.utils.showToast
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

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
    var locationId = "0"
    var showroomid = "0"
    var iCityId = "30"
    var categoryid = "0"
    var subCateogory = "0"
    var customerid = "0"
    var previouscat = 0
    var skipCriteriasearch = true
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
//        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        setObserver()
        setRecyclervewData()
        vm.hidetoolbar.value = false
        homeViewModel.isloading.set(true)
        vm.btabselectedpostion.value = 0
        binding.versionTextview.text =
            getString(R.string.version).plus(" ( " + BuildConfig.VERSION_NAME + " ) ")
        handleNotificationClick()
        setListeners()
        getLocationBasedOnCityFilter()
        vm.isvisble.value = true
        binding.viewallTxt.setOnClickListener {
            findNavController().navigate(R.id.nav_categories)
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
                                    iCityId = titleData.mstCode
                                    homeViewModel.isloading.set(true)
                                    locationList =
                                        homeViewModel.getLocationWIthFromCities(iCityId)
                                    locationId = "0"
                                    if (locationList.isNotEmpty())
                                        binding.locationTxt.text = locationList[0].title
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
        binding.locationTxt.setOnClickListener {
            homeViewModel.isloading.set(true)
            homeViewModel.getMstData(iCityId, categoryid)
        }
        binding.searchedit.setOnClickListener {
            homeViewModel.isloading.set(true)
            homeViewModel.getShowRoomSearch(showroomid, locationId, categoryid)
        }
        binding.clearImg.setOnClickListener {
            binding.searchedit.text = ""
            showroomid = "0"
            binding.clearImg.visibility = View.GONE
            dashboardOffersList()
        }
        binding.goldratesLyout.goldcLyout.setOnClickListener {
            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                openURL((AppPreference.read(Constants.GOLDTRENDREPORT, "") ?: "").toUri())
            } else {
                findNavController().navigate(R.id.nav_sign_in, getLoginBundleData())
            }
        }
        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
            homeViewModel.username.set(AppPreference.read(Constants.NAME, ""))
        } else {
            homeViewModel.username.set(getString(R.string.signin))
        }
        binding.locationTxt.text = "All"

    }


    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(100)
            activity?.setToolbarVisibility(View.GONE)
        }
        homeViewModel.getOfferCategories("0")
    }

    private fun setObserver() {
        if (!AppPreference.read(Constants.PROFILEPIC, "").isNullOrEmpty()) {
            homeViewModel.profilepic.set(AppPreference.read(Constants.PROFILEPIC, ""))
        }
        homeViewModel.categoriesResponse.observe(viewLifecycleOwner) { response ->
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
                                it.Mst_Code
                            )
                        )
                    }
                    if (!AppPreference.read(Constants.ISLOGGEDIN, false)) {
                        loadServices()
                    }
                    homeViewModel.getOfferSubcategoryChips(categoryid)
                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
                    homeViewModel.isloading.set(true)
                    homeViewModel.getOfferSubcategoryChips(categoryid)
                }

                else -> {}
            }
        }

        homeViewModel.subcategoryResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    offertypeList.clear()
                    offertypeList.add(FilterData("All", "0", true))
                    response.data?.Data?.forEach {
                        offertypeList.add(FilterData(it.Mst_Desc, it.Mst_Code))
                    }
                    loadFilters()
                    if (skipCriteriasearch) {
                        homeViewModel.getSearchCriteria(
                            AppPreference.read(Constants.USERUID, "") ?: "0"
                        )
                    } else {
                        skipCriteriasearch = true
                        homeViewModel.getDashboardData(
                            showroomid,
                            locationId,
                            categoryid, subCateogory, iCityId,
                            AppPreference.read(Constants.USERUID, "0") ?: "0",
                            "0"
                        )
                    }
                }

                is NetworkResult.Error -> {
                }

                else -> {}
            }
        }

        homeViewModel.searchcriteria.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        homeViewModel.isloading.set(false)
                        categoryid = response?.data?.Data?.firstOrNull()?.Service_UID ?: categoryid
//                         response?.data?.Data?.firstOrNull()?.Service_Desc ?: ""

                        locationId = response?.data?.Data?.firstOrNull()?.Location_UID ?: locationId
//                        showroomid = response?.data?.Data?.firstOrNull()?.Showroom_UID ?: showroomid
                        iCityId = response?.data?.Data?.firstOrNull()?.City_UID ?: iCityId
                        if (iCityId == "0") {
                            binding.cityTxt.text = "All"
                        } else {
                            binding.cityTxt.text = homeViewModel.getName("City", iCityId)
                        }
                        if (locationId == "0") {
                            binding.locationTxt.text = "All"
                        } else {
                            binding.locationTxt.text = homeViewModel.getName("Location", locationId)
                        }

                        categoryid = response?.data?.Data?.firstOrNull()?.Service_UID ?: categoryid
                        subCateogory =
                            response?.data?.Data?.firstOrNull()?.Offer_Type ?: subCateogory

                        loadServices()
                        homeViewModel.getGoldRatesData()
                        if (arguments?.getString("ISFROM").equals("CATEGORY")) {
                            homeViewModel.getDashboardData(
                                showroomid,
                                locationId,
                                arguments?.getString("CategoryID") ?: categoryid,
                                subCateogory,
                                iCityId,
                                AppPreference.read(Constants.USERUID, "0") ?: "0",
                                "0"
                            )
                        } else {
                            homeViewModel.getDashboardData(
                                showroomid,
                                locationId,
                                categoryid, subCateogory, iCityId,
                                AppPreference.read(Constants.USERUID, "0") ?: "0",
                                "0"
                            )
                        }
                    }
                }

                is NetworkResult.Error -> {
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
//                        dashboardOffersList()
                    }
                }

                is NetworkResult.Error -> {
//                    dashboardOffersList()
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

        homeViewModel.searchShowRoomResults.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    showroomList.clear()
                    homeViewModel.isloading.set(false)
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
                    updateShowRoomSearch()
                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
                    if (!binding.offertypeChips.isEmpty()) {
                        val firstChip = binding.offertypeChips.getChildAt(0) as? Chip
                        firstChip?.isChecked = true
                    }
                    dashboardOffersList()
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
        if (AppPreference.read(Constants.MASTERDATA, "").isNullOrEmpty()) {
            val intent = Intent(requireActivity(), OnBoardingActivity::class.java)
            intent.putExtra(
                Constants.MOBILENO,
                AppPreference.read(Constants.MOBILENO, "") ?: ""
            )
            AppPreference.write(Constants.ISLOGGEDIN, false)
            AppPreference.clearAll()
            AppPreference.write(Constants.SKIPSIGNIN, true)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        } else {
            val sType = object : TypeToken<List<CommonDataResponse>>() {}.type
            val otherList = Gson().fromJson<List<CommonDataResponse>>(
                AppPreference.read(Constants.MASTERDATA, ""),
                sType
            )
            cityList.clear()
            otherServicesList.clear()
            otherList.forEach {
                if (it.MstType == "Web_Link_Offers")
                    otherServicesList.add(
                        CommonDataResponse(
                            MstCode = it.MstCode,
                            MstDesc = it.MstDesc,
                            Image_path = it.Image_path, URL = it.URL
                        )
                    )
                if (it.MstType == "City") {
                    cityList.add(SpinnerRowModel(it.MstDesc, mstCode = it.MstCode))
                }
            }
            loadViewPager(otherServicesList)
        }

    }

    private fun updateShowRoomSearch() {
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
                                setChipDefaultSelected()
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

    fun setChipDefaultSelected() {
        if (!binding.offertypeChips.isEmpty()) {
            val firstChip = binding.offertypeChips.getChildAt(0) as? Chip
            firstChip?.isChecked = true
        }
        homeViewModel.getGoldRatesData()
        if (!otherServicesList.isNullOrEmpty()) {
            binding.otherserviceLyout.visibility = View.VISIBLE
        } else {
            binding.otherserviceLyout.visibility = View.GONE
        }
        dashboardOffersList()
    }

    fun dashboardOffersList() {
        homeViewModel.nodata.set(false)
        homeViewModel.isloading.set(true)
        homeViewModel.getDashboardData(
            showroomid,
            locationId,
            categoryid, subCateogory, iCityId,
            AppPreference.read(Constants.USERUID, "0") ?: "0",
            "0"
        )
    }

    private fun setRecyclervewData() {
        adapter =
            binding.rvOfferslist.setUpPagingMultiViewRecyclerAdapter { item: WidgetViewModel, binder: ViewDataBinding, position: Int ->
                binder.setVariable(BR.item, item)
                var datavalues = item as DashboardData

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
                                findNavController().navigate(R.id.nav_sign_in, getLoginBundleData())
                            }
                        }

                        R.id.title_txt, R.id.discountInfo, R.id.storeName, R.id.store_img -> {
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
                                findNavController().navigate(R.id.nav_sign_in, getLoginBundleData())
                            }
                        }

                        R.id.call_img -> {
                            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                                getUserIntrestOnclick("Call", datavalues)
                                activity?.openDialPad(datavalues.contact)
                            } else {
                                findNavController().navigate(R.id.nav_sign_in, getLoginBundleData())
                            }
                        }

                        R.id.directions_img, R.id.location -> {
                            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                                getUserIntrestOnclick("Direction", datavalues)
                                activity?.navigateToGoogleMap(datavalues.GoogleLocation)
                            } else {
                                findNavController().navigate(R.id.nav_sign_in, getLoginBundleData())
                            }
                        }

                        R.id.website_img -> {
                            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                                getUserIntrestOnclick("Website", datavalues)
                                if (!TextUtils.isEmpty(datavalues?.Website_link)) {
                                    openURL(Uri.parse(datavalues?.Website_link ?: ""))
                                } else {
                                    showToast("vendor don't have website")
                                }
                            } else {
                                findNavController().navigate(R.id.nav_sign_in, getLoginBundleData())
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
                                findNavController().navigate(R.id.nav_sign_in, getLoginBundleData())
                            }
                        }

                        R.id.video_img -> {
                            if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                                if (!item.Video_Link.isNullOrEmpty()) {
                                    getUserIntrestOnclick("Video", datavalues)
                                    activity?.openYoutube(datavalues.Video_Link)
                                } else {
                                    showToast("vendor don't have video")
                                }
                            } else {
                                findNavController().navigate(R.id.nav_sign_in, getLoginBundleData())
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
            var selectedchip = "0"
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
            subCateogory = selectedchip
            dashboardOffersList()
        }
        binding.offertypeChips.removeAllViews()
        var i = 0
        offertypeList.forEach {
            binding.offertypeChips.addView(
                createChip(
                    it.filtercategory_desc ?: "",
                    it.filtercode?.toInt() ?: 1
                )
            )
            binding.executePendingBindings()
            i++
        }
        if (!binding.offertypeChips.isEmpty()) {
            val firstChip = binding.offertypeChips.getChildAt(0) as? Chip
            firstChip?.isChecked = true
        }
    }

    fun loadServices() {
        categoryList.firstOrNull()?.isselected = true
        if (categoryList.isNotEmpty()) {
            binding.categoriesTxt.visibility = View.VISIBLE
            binding.viewallTxt.visibility = View.VISIBLE
        }

        binding.rvCategories.setUpMultiViewRecyclerAdapter(
            categoryList
        ) { item: CategoriesData, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            if (arguments?.getString(Constants.ISFROM) == "CATEGORY") {
                setCategorySeleted(position, item)
            } else {
                setCategorySeleted(position, item)
            }
            binder.setVariable(BR.onItemClick, View.OnClickListener {
                when (it.id) {
                    R.id.category_item -> {
                        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                            categoryList[previouscat].isselected = false
                            previouscat = position
                            categoryList[previouscat].isselected = true
                            homeViewModel.isloading.set(true)
                            categoryid = item.category_id ?: categoryid
                            arguments?.putString("ISFROM", "")
                            skipCriteriasearch = false
                            homeViewModel.getOfferSubcategoryChips(item.category_id ?: categoryid)
                        } else {

                            findNavController().navigate(R.id.nav_sign_in, getLoginBundleData())
                        }

                        binding.rvCategories.notifyDataChange()
                    }
                }
                binder.executePendingBindings()
            })
        }
    }

    private fun setCategorySeleted(position: Int, item: CategoriesData) {
//        binding.rvCategories.scrollToPosition(position)
        categoryList[position].isselected =
            item.category_id == (arguments?.getString("CategoryID") ?: categoryid)
        if (item.isselected) {
            previouscat = position
        }
    }

    private fun loadViewPager(otherservices: ArrayList<CommonDataResponse>) {
        val imageList = ArrayList<SlideModel>()
        otherservices.forEach {
            imageList.add(SlideModel(it.Image_path, it.MstDesc, ScaleTypes.FIT))
        }
        if (!imageList.isNullOrEmpty()) {
            binding.slider.setImageList(imageList)
        }
        binding.slider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                navigatepagerClick(otherservices[position].URL)
            }

            override fun doubleClick(position: Int) {

            }
        })

    }

    private fun navigateOfferDeatils(datavalues: DashboardData?, isfrom: String = "") {
        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
            val bundle = Bundle()
            if (isfrom != "Notifications") {
                bundle.putString("OfferID", datavalues?.id)
                bundle.putString(
                    "Imagepath",
                    datavalues?.ImagesList?.firstOrNull()?.imagepath
                )
            } else {
                bundle.putString(
                    "OfferID", AppPreference.read(
                        Constants.Offer_id, ""
                    )
                )
            }
            findNavController().navigate(R.id.nav_offer_details, bundle)
        } else {
            findNavController().navigate(R.id.nav_sign_in, getLoginBundleData())
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

                if ((AppPreference?.read(Constants.Screen_Code, "") ?: "") == "1") {
                    navigateOfferDeatils(null, "Notifications")
                    AppPreference.write(Constants.Screen_Code, "")
                    AppPreference.write(Constants.Offer_id, "")
                }
                if ((AppPreference?.read(Constants.Screen_Code, "") ?: "") == "2") {
                    findNavController().navigate(R.id.nav_rewardshistory)
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


    private fun setListeners() {
        binding.setVariable(BR.onItemClick, View.OnClickListener {
            when (it.id) {
                R.id.imageView14 -> {
                    if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                        findNavController().navigate(R.id.nav_customerProfileFragment)
                    } else {
                        findNavController().navigate(R.id.nav_sign_in, getLoginBundleData())
                    }
                }

                R.id.notofication_img -> {
                    if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                        findNavController().navigate(R.id.nav_notifications)
                    } else {
                        var bundle = Bundle()
                        bundle.putBoolean("isFrom", true)
                        findNavController().navigate(R.id.nav_sign_in, getLoginBundleData())
                    }
                }

                R.id.favourite_img -> {
                    if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                        findNavController().navigate(R.id.nav_wishlist)
                    } else {

                        var bundle = Bundle()
                        bundle.putBoolean("isFrom", true)
                        findNavController().navigate(R.id.nav_sign_in, getLoginBundleData())
                    }
                }

                R.id.add_post -> {
                    if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                        findNavController().navigate(R.id.nav_addpost)
                    } else {
                        var bundle = Bundle()
                        bundle.putBoolean("isFrom", true)
                        findNavController().navigate(R.id.nav_sign_in, getLoginBundleData())
                    }
                }
            }

        })
    }

    private fun getLoginBundleData(): Bundle {
        var bundle = Bundle()
        bundle.putBoolean("isFrom", true)
//        bundle.putString("Locationid", locationId)
//        bundle.putString("lShowroomId", showroomid)
//        bundle.putString("lServiceId", service)
//        bundle.putString("iCategoryId", categoryid)
//        bundle.putString("iCityId", iCityId)
        return bundle
    }

    fun getUserIntrestOnclick(flag: String, datavalues: DashboardData) {
        val posuserintrest = PostUserIntrest(
            datavalues.showroomid,
            datavalues.locationid,
            datavalues.id,
            flag,
            AppPreference.read(Constants.USERUID, "") ?: ""
        )
        homeViewModel.getUserInterest(posuserintrest)
    }

    fun navigatepagerClick(weburl: String) {
        val bundle = Bundle()
        bundle.putString(Constants.ISFROM, Constants.Web_Link_Offers)
        bundle.putString(
            Constants.WEB_URL,
            weburl
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

    private fun getLocationsData() {
        if (locationList.isEmpty()) {
            showToast("No data")
        } else {
            locationList.add(SpinnerRowModel("All", mstCode = "0"))
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

    private fun getLocationBasedOnCityFilter() {
        homeViewModel.masterdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    homeViewModel.isloading.set(false)
                    response.data?.let { resposnes ->
                        locationList.clear()
                        response?.data?.data?.forEach {
                            locationList.add(
                                SpinnerRowModel(
                                    it.Location_Name,
                                    false,
                                    false,
                                    mstCode = it.Location_UID
                                )
                            )
                        }
                        getLocationsData()
                    }
                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
                }

                else -> {}
            }
        }
    }
}

