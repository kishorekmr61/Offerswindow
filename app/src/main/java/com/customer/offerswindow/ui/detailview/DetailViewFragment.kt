package com.customer.offerswindow.ui.detailview

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
import com.customer.offerswindow.databinding.FragmentDetailViewBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.dashboard.DashboardData
import com.customer.offerswindow.model.dashboard.Images
import com.customer.offerswindow.model.offerdetails.Termsandconditions
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.utils.notifyDataChange
import com.customer.offerswindow.utils.setUpMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setUpViewPagerAdapter
import com.customer.offerswindow.utils.setWhiteToolBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailViewFragment : Fragment() {

    private var _binding: FragmentDetailViewBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewViewModel by viewModels()
    private val vm: DashBoardViewModel by activityViewModels()
    var termslist = ArrayList<Termsandconditions>()
    var dashboaroffersList = arrayListOf<DashboardData>()


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
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        setListeners()
        viewModel.isloading.set(true)
        arguments?.getString("OfferID")?.let { viewModel.getDetailData(it) }
    }

    private fun setRecyclervewData(dashboaroffersList: ArrayList<DashboardData>) {
        binding.rvMoreoffers.setUpMultiViewRecyclerAdapter(
            dashboaroffersList
        ) { item: DashboardData, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            if (item.ImagesList.isNullOrEmpty()){
                item.ImagesList =  arrayListOf()
            }
            binder.root.findViewById<ViewPager2>(R.id.viewPager).setUpViewPagerAdapter(
                getImageList(item.ImagesList) ?: arrayListOf()
            ) { item: Images, binder: ViewDataBinding, position: Int ->
                binder.setVariable(BR.item, item)
                binder.setVariable(BR.onItemClick, View.OnClickListener {

                })
            }
            binder.setVariable(BR.onItemClick, View.OnClickListener {
                when (it.id) {
                    R.id.favourite -> {
                        if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                            item.isfavourite = false
                        } else {
                            findNavController().navigate(R.id.nav_sign_in)
                        }
                    }

                    R.id.title_txt -> {
                        viewModel.getDetailData(item.id)
                    }
                }
                binder.executePendingBindings()
            })
        }
        binding.rvTanc.setUpMultiViewRecyclerAdapter(
            termslist
        ) { item: Termsandconditions, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
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
                        viewModel.isloading.set(false)
                        viewModel.OfferDeatils.set(response.data?.Data)
                        dashboaroffersList.addAll(
                            response.data?.Data?.Other_Offer_Details ?: arrayListOf()
                        )
                        if (!resposnes.Data.ImagesList.isNullOrEmpty()) {
                            binding.tandcTxt.visibility = View.VISIBLE
                            viewModel.imagepath.set(resposnes.Data.ImagesList?.firstOrNull()?.imagepath?:"")
                        }
                        if (!response.data?.Data?.Terms_Conditions.isNullOrEmpty()) {
                            termslist.addAll(response.data?.Data?.Terms_Conditions ?: arrayListOf())
                        }
                        setRecyclervewData(dashboaroffersList)
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                }

                else -> {}
            }
        }
    }

    private fun setListeners() {
        binding.setVariable(BR.onItemClick, View.OnClickListener {

            when (it.id) {
                R.id.slotbooking_txt, R.id.bookoffer_txt -> {
                    if (AppPreference.read(Constants.ISLOGGEDIN, false)) {
                        var dataobj = viewModel.OfferDeatils.get()
                        val bundle = Bundle()
                        bundle.putString(
                            "Location",
                            dataobj?.showroomname + " - " + dataobj?.locationname
                        )
                        bundle.putString("OfferID", dataobj?.id)
                        bundle.putString("SERVICEID", dataobj?.serviceid)
                        bundle.putString("LOCATIONID", dataobj?.locationid)
                        bundle.putString("SHOWROOMID", dataobj?.showroomid)
                        bundle.putString("Imagepath", dataobj?.ImagesList?.firstOrNull()?.imagepath)
                        findNavController().navigate(R.id.nav_slots, bundle)
                    } else {
                        var  bundle =Bundle()
                        bundle.putBoolean("isFrom",true)
                        findNavController().navigate(R.id.nav_sign_in,bundle)
                    }
                }
            }

        })
    }

    private fun getImageList(imagesList: ArrayList<Images>?): ArrayList<Images>? {
        if (imagesList.isNullOrEmpty()) {
            imagesList?.add(Images("0", ""))
        }
        return imagesList
    }
}