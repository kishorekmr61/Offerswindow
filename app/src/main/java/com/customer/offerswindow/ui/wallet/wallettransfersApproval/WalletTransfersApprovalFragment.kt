package com.customer.offerswindow.ui.wallet.wallettransfersApproval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.customer.offerswindow.BR
import com.customer.offerswindow.R
import com.customer.offerswindow.databinding.WalletTransfersApprovalFragmentBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.wallet.PostWalletTransfersApprovals
import com.customer.offerswindow.model.wallet.WalletPendingData
import com.customer.offerswindow.ui.home.HomeViewModel
import com.customer.offerswindow.utils.MultiViewPagingRecyclerAdapter
import com.customer.offerswindow.utils.MultiViewPagingRecyclerFooterAdapter
import com.customer.offerswindow.utils.ShowFullToast
import com.customer.offerswindow.utils.resource.WidgetViewModel
import com.customer.offerswindow.utils.setUpPagingMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setWhiteToolBar
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalletTransfersApprovalFragment : Fragment() {

    companion object {
        fun newInstance() = WalletTransfersApprovalFragment()
    }

    private val homeViewModel: HomeViewModel by viewModels()
    private val walletTransfersApprovalViewModel: WalletTransfersApprovalViewModel by viewModels()
    private var _binding: WalletTransfersApprovalFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var walletpendingrequestadapter: PagingDataAdapter<WidgetViewModel, MultiViewPagingRecyclerAdapter.ViewHolder<ViewDataBinding>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WalletTransfersApprovalFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = walletTransfersApprovalViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        lifecycleScope.launch {
            delay(100)
            activity?.setWhiteToolBar("Wallet Transfer Approval", true)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        walletTransfersApprovalViewModel.isloading.set(true)
        setObserver()
        bindEvents()
        walletTransfersApprovalViewModel.isloading.set(true)
        walletTransfersApprovalViewModel.getWalletPendingData()
    }

    private fun setObserver() {
        walletTransfersApprovalViewModel.walletpendingrequest.observe(viewLifecycleOwner) { response ->
            response.let { resposnes ->
                walletTransfersApprovalViewModel.isloading.set(false)
                lifecycleScope.launch {
                    try {
                        walletpendingrequestadapter.submitData(response as PagingData<WidgetViewModel>)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }

        walletTransfersApprovalViewModel.postingResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    walletTransfersApprovalViewModel.isloading.set(false)
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            ShowFullToast(response.data?.Message ?: "")
                            walletTransfersApprovalViewModel.isloading.set(true)
                            walletTransfersApprovalViewModel.getWalletPendingData()
                        } else {
                            showToast(response.data?.Message ?: "")
                        }
                    }
                }

                is NetworkResult.Error -> {
                    walletTransfersApprovalViewModel.isloading.set(false)
                    if (!response.message.isNullOrEmpty()) {
                        ShowFullToast(response.message)
                        requireActivity().supportFragmentManager.popBackStack()
                    } else {
                        ShowFullToast(response.data?.Message ?: "")
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }

                is NetworkResult.Loading -> {
//                    viewModel.isloading.set(true)
                }
            }
        }
    }

    private fun bindEvents() {
        walletpendingrequestadapter =
            binding.rvRewardpoints.setUpPagingMultiViewRecyclerAdapter { item: WidgetViewModel, binder: ViewDataBinding, position: Int ->
                binder.setVariable(BR.item, item)
                binder.setVariable(BR.onItemClick, View.OnClickListener {
                    when (it.id) {
                        R.id.approve_btn -> {
                            var postingdate = getWalletpostData(item)
                            postingdate.ApprovalStatus = "A"

                            walletTransfersApprovalViewModel.isloading.set(true)
                            walletTransfersApprovalViewModel.postWalletApprovalData(postingdate)
                        }

                        R.id.reject_btn -> {
                            var postingdate = getWalletpostData(item)
                            postingdate.ApprovalStatus = "R"
                            walletTransfersApprovalViewModel.isloading.set(true)
                            walletTransfersApprovalViewModel.postWalletApprovalData(postingdate)
                        }
                    }
                })
                binder.executePendingBindings()
            }

        walletpendingrequestadapter.addLoadStateListener {
            if (it.source.refresh is LoadState.NotLoading &&
                it.append.endOfPaginationReached &&
                walletpendingrequestadapter.itemCount < 1
            ) {
                walletTransfersApprovalViewModel.nodata.set(walletpendingrequestadapter.itemCount == 0)
            }
        }

        val concatAdapter = walletpendingrequestadapter.withLoadStateFooter(
            footer = MultiViewPagingRecyclerFooterAdapter()
        )
        binding.rvRewardpoints.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }
    }

    private fun getWalletpostData(item: WidgetViewModel): PostWalletTransfersApprovals {
        var data = item as WalletPendingData
        var postwalletTransferStatus = PostWalletTransfersApprovals()
        postwalletTransferStatus.LocalRemoteID = data.Local_Remote_ID
        postwalletTransferStatus.RequestedBy = data.Requested_By
        postwalletTransferStatus.CustomerUID = data.Cust_UID
        postwalletTransferStatus.RequestedDateTime = data.Requested_Date
        postwalletTransferStatus.TransactionType = data.Transaction_Type
        postwalletTransferStatus.Amount = data.Amount
        return postwalletTransferStatus
    }
}