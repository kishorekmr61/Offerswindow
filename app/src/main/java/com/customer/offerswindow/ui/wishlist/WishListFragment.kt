package com.customer.offerswindow.ui.wishlist

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
import com.customer.offerswindow.databinding.FragmentWishListBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.customersdata.PostUserIntrest
import com.customer.offerswindow.model.dashboard.SelectedOffers
import com.customer.offerswindow.model.dashboard.WishListData
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.ui.home.HomeViewModel
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
class WishListFragment : Fragment() {

    private var _binding: FragmentWishListBinding? = null
    private val binding get() = _binding!!
    private val vm: DashBoardViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    var wishlistData = ArrayList<WishListData>()
    private val viewModel: WishListViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        activity?.setWhiteToolBar("WishList", true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        vm.hidetoolbar.value = true
        viewModel.isloading.set(true)
        viewModel.getWishListData(AppPreference.read(Constants.USERUID, "") ?: "", "0")
    }

    private fun setObserver() {
        viewModel.wishlistResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        viewModel.isloading.set(false)
                        wishlistData.addAll(resposnes.Data ?: arrayListOf())
                        viewModel.nodata.set(wishlistData.isEmpty())
                        setRecyclervewData()
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                }

                else -> {}
            }
        }

    }

    private fun setRecyclervewData() {
        binding.rvListdata.setUpMultiViewRecyclerAdapter(
            wishlistData
        ) { witem: WishListData, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, witem)
            var viewpager = binder.root.findViewById<ViewPager2>(R.id.viewPager)
            var tabview = binder.root.findViewById<TabLayout>(R.id.tab_layout)
            viewpager.setUpViewPagerAdapter(
                getImageList(witem.Wishlist.firstOrNull()?.Selected_Offers) ?: arrayListOf()
            ) { item: SelectedOffers, binder: ViewDataBinding, position: Int ->
                binder.setVariable(BR.item, item)
                binder.setVariable(BR.onItemClick, View.OnClickListener {
                    when (it.id) {
                        R.id.title_txt -> {
                            var bundle = Bundle()
                            bundle.putString("OfferID", witem.Offer_ID)
                            findNavController().navigate(R.id.nav_offer_details, bundle)
                        }
                    }
                })
            }
            TabLayoutMediator(tabview, viewpager) { tab, position ->
            }.attach()
            binder.setVariable(BR.onItemClick, View.OnClickListener {
                when (it.id) {
                    R.id.title_txt -> {
                        var bundle = Bundle()
                        bundle.putString("OfferID", witem.Offer_ID)
                        findNavController().navigate(R.id.nav_offer_details, bundle)
                    }

                    R.id.share_img -> {
                        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                            getUserIntrestOnclick(witem, "Share")
                            activity?.openNativeSharingDialog(
                                witem.Wishlist.firstOrNull()?.Website_Url ?: ""
                            )
                        } else {
                            findNavController().navigate(R.id.nav_sign_in)
                        }
                    }

                    R.id.call_img -> {
                        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                            getUserIntrestOnclick(witem, "Call")
                            activity?.openDialPad(witem.Wishlist.firstOrNull()?.Contact_No_1 ?: "")
                        } else {
                            findNavController().navigate(R.id.nav_sign_in)
                        }
                    }

                    R.id.directions_img -> {
                        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                            getUserIntrestOnclick(witem, "Direction")
                            activity?.navigateToGoogleMap(
                                witem.Wishlist.firstOrNull()?.Google_Location ?: ""
                            )
                        } else {
                            findNavController().navigate(R.id.nav_sign_in)
                        }
                    }

                    R.id.website_img -> {
                        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                            getUserIntrestOnclick(witem, "Website")
                            openURL(Uri.parse(witem.Wishlist.firstOrNull()?.Website_Url ?: ""))
                        } else {
                            findNavController().navigate(R.id.nav_sign_in)
                        }
                    }

                    R.id.whatsapp_img -> {
                        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                            getUserIntrestOnclick(witem, "Whatsapp")
                            activity?.openWhatsAppConversation(
                                witem.Wishlist.firstOrNull()?.Contact_No_1 ?: "", ""
                            )
                        } else {
                            findNavController().navigate(R.id.nav_sign_in)
                        }
                    }
                }
                binder.executePendingBindings()
            })
        }
    }

    private fun getImageList(imagesList: ArrayList<SelectedOffers>?): ArrayList<SelectedOffers>? {
        if (imagesList.isNullOrEmpty()) {
            imagesList?.add(SelectedOffers("0", "", ""))
        }
        return imagesList
    }

    fun getUserIntrestOnclick(witem: WishListData, flag: String) {
        var posuserintrest = PostUserIntrest(
            witem.Showroom_UID,
            witem.Branch_UID,
            witem.Offer_ID,
            flag,
            AppPreference.read(Constants.USERUID, "") ?: ""
        )
        homeViewModel.getUserInterest(posuserintrest)
    }

}