package com.customer.offerswindow.ui.detailview

import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.BR
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentDetailViewBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.customersdata.PostOfferBooking
import com.customer.offerswindow.model.customersdata.PostUserIntrest
import com.customer.offerswindow.model.customersdata.PostWishlist
import com.customer.offerswindow.model.offerdetails.OfferDeatils
import com.customer.offerswindow.model.offerdetails.OfferImages
import com.customer.offerswindow.model.offerdetails.Termsandconditions
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.ui.home.HomeViewModel
import com.customer.offerswindow.ui.wishlist.WishListViewModel
import com.customer.offerswindow.utils.navigateToGoogleMap
import com.customer.offerswindow.utils.openDialPad
import com.customer.offerswindow.utils.openURL
import com.customer.offerswindow.utils.openWhatsAppConversation
import com.customer.offerswindow.utils.openYoutube
import com.customer.offerswindow.utils.setUpMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setUpViewPagerAdapter
import com.customer.offerswindow.utils.setWhiteToolBar
import com.customer.offerswindow.utils.shareImageFromUrl
import com.customer.offerswindow.utils.showLongToast
import com.customer.offerswindow.utils.showToast
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailViewFragment : Fragment() {

    private var _binding: FragmentDetailViewBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val wishListViewModel: WishListViewModel by viewModels()
    private val vm: DashBoardViewModel by activityViewModels()
    var termslist = ArrayList<Termsandconditions>()
    var dashboaroffersList = arrayListOf<OfferDeatils>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailViewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        activity?.setWhiteToolBar("Offer detail", true)
        vm.isvisble.value = false
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.nav_home)
                }
            }
        )
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        setListeners()
        viewModel.isloading.set(true)
        arguments?.getString("OfferID")?.let { viewModel.getDetailData(it) }
    }

    private fun setRecyclervewData(dashboaroffersList: ArrayList<OfferDeatils>) {
        binding.rvMoreoffers.setUpMultiViewRecyclerAdapter(
            dashboaroffersList
        ) { item: OfferDeatils, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
//            var viewpager = binder.root.findViewById<ViewPager2>(R.id.viewPager)
//            var tabview = binder.root.findViewById<TabLayout>(R.id.tab_layout)
//            viewpager.setUpViewPagerAdapter(
//                getOtherImageList(item?.ImagesList) ?: arrayListOf()
//            ) { imageitem: OfferImages, binder: ViewDataBinding, position: Int ->
//                binder.setVariable(BR.item, imageitem)
//                binder.setVariable(BR.onItemClick, View.OnClickListener {
//                    when (it.id) {
//                        R.id.img -> {
//                            viewModel.isloading.set(true)
//                            viewModel.getDetailData(item.id)
//                        }
//                    }
//                })
//            }
//            TabLayoutMediator(tabview, viewpager) { tab, position ->
//            }.attach()
            binder.setVariable(BR.onItemClick, View.OnClickListener {
                when (it.id) {
                    R.id.favourite, R.id.favourite_img -> {
                        if (item.isfavourite) {
                            item.isfavourite = false
                        } else {
                            item.isfavourite = true
                        }
                        val postdata = PostWishlist(
                            item.id,
                            AppPreference.read(Constants.USERUID, "") ?: ""
                        )
                        homeViewModel.isloading.set(true)
                        homeViewModel.postWishListItem(postdata)

                    }

                    R.id.title_txt,R.id.store_img,R.id.storeName,R.id.discountInfo -> {
                        viewModel.getDetailData(item.id)
                    }


                    R.id.share_img -> {
                        getUserIntrestOnclick("Share", item)
                        activity?.shareImageFromUrl(
                            requireActivity(),
                            item.id,
                            item.ImagesList?.firstOrNull()?.imagepath ?: ""
                        )
                    }

                    R.id.call_img -> {
                        getUserIntrestOnclick("Call", item)
                        activity?.openDialPad(item.contact)
                    }

                    R.id.directions_img, R.id.location-> {
                        getUserIntrestOnclick("Direction", item)
                        activity?.navigateToGoogleMap(item.GoogleLocation)
                    }

                    R.id.website_img -> {
                        getUserIntrestOnclick("Website", item)
                        if (!item?.Website_link.isNullOrEmpty()) {
                            openURL((item.Website_link ?: "").toUri())
                        } else {
                            showToast("vendor don't have website")
                        }
                    }

                    R.id.whatsapp_img -> {
                        getUserIntrestOnclick("Whatsapp", item)
                        activity?.openWhatsAppConversation(
                            item.contact,
                            getString(R.string.whatsappmsg)
                        )

                    }

                    R.id.video_img -> {
                        if (!item.Video_Link.isNullOrEmpty()) {
                            getUserIntrestOnclick("Video", item)
                            activity?.openYoutube(item.Video_Link)
                        } else {
                            showToast("vendor don't have video")
                        }

                    }
                }
                binder.executePendingBindings()
            })
        }
        binding.rvTanc.setUpMultiViewRecyclerAdapter(
            termslist
        ) { item: Termsandconditions, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            val texviedesc = binder.root.findViewById<TextView>(R.id.desc_txt)
            Linkify.addLinks(texviedesc, Linkify.ALL);
            binder.setVariable(BR.onItemClick, View.OnClickListener {
                binder.executePendingBindings()
            })
        }
    }

    private fun setObserver() {
        viewModel.deatiledresponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    termslist.clear()
                    response.data?.let { resposnes ->
                        if (response.data?.Data != null) {
                            viewModel.isloading.set(false)
                            viewModel.OfferDeatils.set(response.data?.Data)
                            dashboaroffersList.addAll(
                                response.data?.Data?.Other_Offer_Details ?: arrayListOf()
                            )
                            if (!resposnes.Data.ImagesList.isNullOrEmpty()) {
                                binding.tandcTxt.visibility = View.VISIBLE
                                viewModel.imagepath.set(
                                    resposnes.Data.ImagesList?.firstOrNull()?.imagepath ?: ""
                                )
                            }
                            if (!response.data?.Data?.Terms_Conditions.isNullOrEmpty()) {
                                termslist.addAll(
                                    response.data?.Data?.Terms_Conditions ?: arrayListOf()
                                )
                            }
                            updateImages(response.data?.Data)
                            if (dashboaroffersList.isNullOrEmpty()) {
                                binding.moreoffersTxt.visibility = View.GONE
                                binding.rvMoreoffers.visibility = View.GONE
                            } else {
                                binding.moreoffersTxt.visibility = View.VISIBLE
                                binding.rvMoreoffers.visibility = View.VISIBLE
                                setRecyclervewData(dashboaroffersList)
                            }
                        } else {
                            showLongToast("No data ")
                            findNavController().popBackStack()
                        }
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                }

                else -> {}
            }
        }
        viewModel.offerPostingResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    viewModel.isloading.set(false)
                    response.data?.let { resposnes ->
                        showLongToast(resposnes.Message)
                        var bundle = Bundle()
                        bundle.putString("ISFROM", "OFFERBOOKING")
                        findNavController().navigate(R.id.nav_success, bundle)
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                }

                else -> {}
            }
        }
        wishListViewModel.removewishlistResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        viewModel.isloading.set(false)
                        showLongToast(response.message ?: "")
                        viewModel.isloading.set(true)
                        arguments?.getString("OfferID")?.let { viewModel.getDetailData(it) }
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                    showLongToast(response.message ?: "")
                    viewModel.isloading.set(true)
                    arguments?.getString("OfferID")?.let { viewModel.getDetailData(it) }
                }

                else -> {}
            }
        }
        homeViewModel.postwishlistdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    viewModel.isloading.set(false)
                    showLongToast(response.message ?: "")
                    viewModel.isloading.set(true)
                    arguments?.getString("OfferID")?.let { viewModel.getDetailData(it) }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                    showLongToast(response.message ?: "")
                }

                else -> {}
            }
        }
    }

    private fun updateImages(data: OfferDeatils?) {
        binding.viewPager.setUpViewPagerAdapter(
            getOtherImageList(data?.ImagesList) ?: arrayListOf()
        ) { item: OfferImages, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
        }.attach()
    }

    private fun setListeners() {
        binding.setVariable(BR.onItemClick, View.OnClickListener {
            var dataobj = viewModel.OfferDeatils.get()
            when (it.id) {
                R.id.favourite -> {

                    if (dataobj?.getWishlistData() == true) {
                        viewModel.isloading.set(true)
                        wishListViewModel.removeWishListitem(
                            dataobj.id,
                            AppPreference.read(Constants.USERUID, "") ?: ""
                        )
                    } else {
                        val postdata = PostWishlist(
                            dataobj?.id ?: "",
                            AppPreference.read(Constants.USERUID, "") ?: ""
                        )
                        viewModel.isloading.set(true)
                        homeViewModel.postWishListItem(postdata)
                    }
                }

                R.id.slotbooking_txt -> {
                    val bundle = Bundle()
                    bundle.putString(
                        "Location",
                        dataobj?.showroomname + " - " + dataobj?.locationname
                    )
                    bundle.putString("OfferID", dataobj?.id)
                    bundle.putString("SERVICEID", dataobj?.serviceid)
                    bundle.putString("LOCATIONID", dataobj?.locationid)
                    bundle.putString("SHOWROOMID", dataobj?.showroomid)
                    bundle.putString("ISFROM", "SlotBooking")
                    bundle.putString("Imagepath", dataobj?.ImagesList?.firstOrNull()?.imagepath)
                    findNavController().navigate(R.id.nav_slots, bundle)
                }

                R.id.bookoffer_txt -> {
                    viewModel.isloading.set(true)
                    val postofferbookings = PostOfferBooking(
                        dataobj?.showroomid ?: "",
                        dataobj?.locationid ?: "",
                        dataobj?.serviceid ?: "",
                        dataobj?.id ?: "", dataobj?.id ?: ""
                    )
                    viewModel.postOfferBooking(postofferbookings)
                }

                R.id.share_lyout, R.id.share_img -> {
                    getUserIntrestOnclick("Share", dataobj)
                    activity?.shareImageFromUrl(
                        requireActivity(),
                        dataobj?.id ?: "",
                        dataobj?.ImagesList?.firstOrNull()?.imagepath ?: ""
                    )

                }

                R.id.call_lyout, R.id.call_img -> {
                    getUserIntrestOnclick("Call", dataobj)
                    activity?.openDialPad(dataobj?.contact ?: "")
                }

                R.id.direction_lyout ,R.id.location_txt -> {
                    getUserIntrestOnclick("Direction", dataobj)
                    activity?.navigateToGoogleMap(dataobj?.GoogleLocation ?: "")
                }

                R.id.website_lyout, R.id.website_img -> {
                    getUserIntrestOnclick("Website", dataobj)
                    openURL(Uri.parse(dataobj?.Website_link ?: ""))
                }

                R.id.whatsapp_lyout -> {
                    getUserIntrestOnclick("Whatsapp", dataobj)
                    activity?.openWhatsAppConversation(
                        dataobj?.contact ?: "",
                        getString(R.string.whatsappmsg)
                    )
                }

                R.id.video_lyout, R.id.video_img -> {
                    if (!dataobj?.Video_Link.isNullOrEmpty()) {
                        getUserIntrestOnclick("Video", dataobj)
                        activity?.openYoutube(dataobj?.Video_Link ?: "")
                    } else {
                        showToast("vendor don't have video")
                    }
                }
            }

        })
    }

    //    private fun getImageList(imagesList: ArrayList<Images>?): ArrayList<Images>? {
//        if (imagesList.isNullOrEmpty()) {
//            imagesList?.add(Images("0", ""))
//        }
//        return imagesList
//    }
    private fun getOtherImageList(imagesList: ArrayList<OfferImages>?): ArrayList<OfferImages>? {
        if (imagesList.isNullOrEmpty()) {
            imagesList?.add(OfferImages("0", ""))
        }
        return imagesList
    }

    fun getUserIntrestOnclick(flag: String, datavalues: OfferDeatils?) {
        var posuserintrest = PostUserIntrest(
            datavalues?.showroomid ?: "",
            datavalues?.locationid ?: "",
            datavalues?.id ?: "",
            flag,
            AppPreference.read(Constants.USERUID, "") ?: ""
        )
        homeViewModel.getUserInterest(posuserintrest)
    }
}