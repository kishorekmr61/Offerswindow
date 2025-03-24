package com.customer.offerswindow.ui.mybookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.BR
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentMyBookingsBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.dashboard.BookingData
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.utils.setUpMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setWhiteToolBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyBookingsFragment : Fragment() {

    private var _binding: FragmentMyBookingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyBookingsViewModel by viewModels()
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
            binder.setVariable(BR.onItemClick, View.OnClickListener {
                when (it.id) {
                    R.id.bookings_lyout -> {
                        viewModel.isloading.set(true)
                        navigateOfferDeatils(item)
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
                bundle.putString("OfferID", datavalues?.Service_UID)
            }

            findNavController().navigate(R.id.nav_offer_details, bundle)
        } else {

            findNavController().navigate(R.id.nav_sign_in)
        }
    }
}