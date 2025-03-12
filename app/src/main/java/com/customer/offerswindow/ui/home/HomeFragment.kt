package com.customer.offerswindow.ui.home

import android.annotation.SuppressLint
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
import androidx.viewpager2.widget.ViewPager2
import com.customer.offerswindow.BR
import com.customer.offerswindow.BuildConfig
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentHomeCustomerBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.dashboard.CategoriesData
import com.customer.offerswindow.model.dashboard.DashboardData
import com.customer.offerswindow.model.dashboard.FilterData
import com.customer.offerswindow.model.dashboard.Images
import com.customer.offerswindow.model.masters.CommonDataResponse
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.utils.PermissionsUtil
import com.customer.offerswindow.utils.notifyDataChange
import com.customer.offerswindow.utils.setToolbarVisibility
import com.customer.offerswindow.utils.setUpMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setUpViewPagerAdapter
import com.customer.offerswindow.utils.showToast
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
    var locationList = arrayListOf<CommonDataResponse>()
    var offertypeList = arrayListOf<FilterData>()
    var dashboaroffersList = arrayListOf<DashboardData>()


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
        vm.hidetoolbar.value = false
        homeViewModel.isloading.set(true)
        homeViewModel.getMstData()
        homeViewModel.profilepic.set("https://img.freepik.com/premium-vector/avatar-profile-icon-flat-style-female-user-profile-vector-illustration-isolated-background-women-profile-sign-business-concept_157943-38866.jpg?semt=ais_hybrid")
        binding.versionTextview.text =
            getString(R.string.version).plus(" ( " + BuildConfig.VERSION_NAME + " ) ")
        handleNotificationClick()
        setListeners()
        homeViewModel.getToken()
        setListeners()
        binding.goldratesLyout.updatelblTxt.setOnClickListener {
            homeViewModel.isloading.set(true)
            homeViewModel.getGoldRatesData()
        }
        binding.viewallTxt.setOnClickListener {
            findNavController().navigate(R.id.nav_categories)
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
                        AppPreference.read(Constants.MOBILENO, "")
                            ?.let { homeViewModel.getUserInfo(/*it*/"9533586878") }
                        homeViewModel.getDashboardData("0", "0", "0")
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
                        AppPreference.write(
                            Constants.NAME,
                            resposnes?.Data?.firstOrNull()?.Cust_Name ?: ""
                        )
                        AppPreference.write(
                            Constants.USERUID,
                            resposnes?.Data?.firstOrNull()?.Cust_UID ?: ""
                        )
                        AppPreference.write(
                            Constants.MOBILENO,
                            resposnes?.Data?.firstOrNull()?.Mobile_No ?: ""
                        )
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
                    response.data?.let { resposnes ->
                        categoryList.add(
                            CategoriesData(
                                "https://cdn.pixabay.com/photo/2021/10/11/23/49/app-6702045_1280.png",
                                "All",
                                "0",
                                true
                            )
                        )
                        offertypeList.add(FilterData("All", true))
                        response?.data?.data?.forEach {
                            if (it.MstType == "Service") {
                                categoryList.add(
                                    CategoriesData(
                                        it.Image_path,
                                        it.MstDesc,
                                        it.MstCode
                                    )
                                )
                            }
                            if (it.MstType == "Location") {
                                locationList.add(it)
                            }
                            if (it.MstType == "Offer_Type") {
                                offertypeList.add(FilterData(it.MstDesc))
                            }
                        }
                    }
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
            when (response) {
                is NetworkResult.Success -> {
                    dashboaroffersList.clear()
                    dashboaroffersList.addAll(response.data?.dashboardData ?: arrayListOf())
                    setRecyclervewData(dashboaroffersList)
                }

                is NetworkResult.Error -> {
                    homeViewModel.isloading.set(false)
                }

                else -> {}
            }
        }
    }

    private fun setRecyclervewData(dashboaroffersList: ArrayList<DashboardData>) {
        binding.rvOfferslist.setUpMultiViewRecyclerAdapter(
            dashboaroffersList
        ) { item: DashboardData, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            binder.root.findViewById<ViewPager2>(R.id.viewPager).setUpViewPagerAdapter(
                item.ImagesList ?: arrayListOf()
            ) { item: Images, binder: ViewDataBinding, position: Int ->
                binder.setVariable(BR.item, item)
                binder.setVariable(BR.onItemClick, View.OnClickListener {

                })
            }
            binder.setVariable(BR.onItemClick, View.OnClickListener {
                when (it.id) {
                    R.id.favourite -> {
                        item.isfavourite = false
                        binding.rvOfferslist.notifyDataChange()
                    }

                    R.id.title_txt -> {
                        var bundle = Bundle()
                        bundle.putString("OfferID", item.id)
                        bundle.putString("Imagepath", item.ImagesList?.firstOrNull()?.imagepath)
                        findNavController().navigate(R.id.nav_offer_details, bundle)
                    }
                }
                binder.executePendingBindings()
            })
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
                        binding.rvFilter.notifyDataChange()
                    }
                }
                binder.executePendingBindings()
            })
        }
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
                    findNavController().navigate(R.id.nav_customerProfileFragment)
                }

                R.id.notofication_img -> {
                    findNavController().navigate(R.id.nav_notifications)
                }

                R.id.favourite_img -> {
                    findNavController().navigate(R.id.nav_wishlist)
                }
            }

        })
    }

}