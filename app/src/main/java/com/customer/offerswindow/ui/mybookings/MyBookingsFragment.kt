package com.customer.offerswindow.ui.mybookings

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.customer.offerswindow.BR
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentMyBookingsBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.customersdata.PostUserIntrest
import com.customer.offerswindow.model.dashboard.BookingData
import com.customer.offerswindow.model.dashboard.Images
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.ui.home.HomeViewModel
import com.customer.offerswindow.utils.getImageList
import com.customer.offerswindow.utils.navigateToGoogleMap
import com.customer.offerswindow.utils.openDialPad
import com.customer.offerswindow.utils.openNativeSharingDialog
import com.customer.offerswindow.utils.openURL
import com.customer.offerswindow.utils.openWhatsAppConversation
import com.customer.offerswindow.utils.setUpMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setUpViewPagerAdapter
import com.customer.offerswindow.utils.setWhiteToolBar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyBookingsFragment : Fragment() {

    private var _binding: FragmentMyBookingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyBookingsViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val vm: DashBoardViewModel by activityViewModels()
    var bookingData = ArrayList<BookingData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBookingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        vm.isvisble.value = false

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        vm.hidetoolbar.value = false
        viewModel.isloading.set(true)
        if (arguments?.getString("ISFROM", "") == "OFFERBOOKING") {
            activity?.setWhiteToolBar("My Offer Bookings", true)
            viewModel.getOffersData(AppPreference.read(Constants.USERUID, "") ?: "")
        } else {
            activity?.setWhiteToolBar("My Slot Bookings", true)
            viewModel.getBookingsData(AppPreference.read(Constants.USERUID, "") ?: "")
        }

    }

    private fun setObserver() {
        viewModel.bookingsList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    viewModel.isloading.set(false)
                    response.data?.let { resposnes ->
                        bookingData.clear()
                        bookingData.addAll(resposnes.Data ?: arrayListOf())
                        setupRecyclerview(bookingData)
                        viewModel.nodata.set(bookingData.isEmpty())
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                }

                else -> {}
            }
        }
    }

    private fun setupRecyclerview(mbookinglist: ArrayList<BookingData>) {
        binding.rvMybookings.setUpMultiViewRecyclerAdapter(
            mbookinglist
        ) { item: BookingData, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            var viewpager = binder.root.findViewById<ViewPager2>(R.id.viewPager)
            var tabview = binder.root.findViewById<TabLayout>(R.id.tab_layout)
            viewpager.setUpViewPagerAdapter(
                getImageList(item.Offer_Image_Details) ?: arrayListOf()
            ) { imageitem: Images, binder: ViewDataBinding, position: Int ->
                binder.setVariable(BR.item, imageitem)
                binder.setVariable(BR.onItemClick, View.OnClickListener {
                    when (it.id) {
                        R.id.img -> {
                            navigateOfferDeatils(item)
                        }
                    }
                })
            }
            TabLayoutMediator(tabview, viewpager) { tab, position ->
            }.attach()
            binder.setVariable(BR.onItemClick, View.OnClickListener {
                when (it.id) {
                    R.id.title_txt -> {
                        navigateOfferDeatils(item)
                    }

                    R.id.share_img -> {
                        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                            getUserIntrestOnclick(item, "Share")
                            activity?.openNativeSharingDialog(item.Website_link)
                        } else {
                            findNavController().navigate(R.id.nav_sign_in)
                        }
                    }

                    R.id.call_img -> {
                        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                            getUserIntrestOnclick(item, "Call")
                            activity?.openDialPad(item.Contact_No_1)
                        } else {
                            findNavController().navigate(R.id.nav_sign_in)
                        }
                    }

                    R.id.directions_img -> {
                        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                            getUserIntrestOnclick(item, "Direction")
                            activity?.navigateToGoogleMap(item.Google_Location)
                        } else {
                            findNavController().navigate(R.id.nav_sign_in)
                        }
                    }

                    R.id.website_img -> {
                        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                            getUserIntrestOnclick(item, "Website")
                            openURL(Uri.parse(item?.Website_link ?: ""))
                        } else {
                            findNavController().navigate(R.id.nav_sign_in)
                        }
                    }

                    R.id.whatsapp_img -> {
                        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                            getUserIntrestOnclick(item, "Whatsapp")
                            activity?.openWhatsAppConversation(item.Contact_No_1, "")
                        } else {
                            findNavController().navigate(R.id.nav_sign_in)
                        }
                    }
                }
                binder.executePendingBindings()
            })
        }
    }


    private fun navigateOfferDeatils(datavalues: BookingData?) {
        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
            var bundle = Bundle()
            if (arguments?.getString("ISFROM", "") == "OFFERBOOKING") {
                bundle.putString("OfferID", datavalues?.Offer_UID)
            } else {
                bundle.putString("OfferID", datavalues?.Offer_UID)
            }
            findNavController().navigate(R.id.nav_offer_details, bundle)
        } else {

            findNavController().navigate(R.id.nav_sign_in)
        }
    }

    fun getUserIntrestOnclick(item: BookingData, flag: String) {
        var posuserintrest = PostUserIntrest(
            item.Showroom_UID,
            item.Location_UID,
            item.Offer_UID,
            flag,
            AppPreference.read(Constants.USERUID, "") ?: ""
        )
        homeViewModel.getUserInterest(posuserintrest)
    }
}