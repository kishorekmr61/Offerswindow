package com.customer.offerswindow.ui.wallet.walletbalance

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.customer.offerswindow.BR
import com.customer.offerswindow.R
import com.customer.offerswindow.databinding.WalletBalanceFragmentBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.SpinnerRowModel
import com.customer.offerswindow.model.wallet.Table1
import com.customer.offerswindow.utils.MultiViewPagingRecyclerAdapter
import com.customer.offerswindow.utils.MultiViewPagingRecyclerFooterAdapter
import com.customer.offerswindow.utils.ShowFullToast
import com.customer.offerswindow.utils.resource.WidgetViewModel
import com.customer.offerswindow.utils.setUpPagingMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setWhiteToolBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalletBalanceFragment : Fragment() {

    private val viewModel: WalletBalanceViewModel by viewModels()
    private var _binding: WalletBalanceFragmentBinding? = null
    private val binding get() = _binding!!
    var wallettransfers = ArrayList<Table1>()

    private lateinit var walletTransfersadapter: PagingDataAdapter<WidgetViewModel, MultiViewPagingRecyclerAdapter.ViewHolder<ViewDataBinding>>

    companion object {
        fun newInstance() = WalletBalanceFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WalletBalanceFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        activity?.setWhiteToolBar("Wallet Balance", true)
        setHasOptionsMenu(true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindHistoryData()
        setObserver()
        viewModel.isloading.set(true)
        viewModel.getWalletBalanceData()
    }

    private fun getfilterData(): ArrayList<SpinnerRowModel> {
        val categorylist = ArrayList<SpinnerRowModel>()
        if (walletTransfersadapter.itemCount != 0) {
            wallettransfers.addAll(walletTransfersadapter.snapshot().items as ArrayList<Table1>)
        }
        wallettransfers?.forEach {
            categorylist.add(SpinnerRowModel(it.Trans_Type ?: "", false, false, "", ""))
        }
        return categorylist
    }

    private fun setObserver() {
        viewModel.walletdata.observe(viewLifecycleOwner) { response ->
            response.let { resposnes ->
                viewModel.isloading.set(false)
                lifecycleScope.launch {
                    try {
                        walletTransfersadapter.submitData(response as PagingData<WidgetViewModel>)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }
        viewModel.walletbalancedata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            viewModel.isloading.set(false)
                            viewModel.walletbalance.set(
                                resposnes?.Data?.Table?.firstOrNull()?.Wallet_Balance ?: "0"
                            )
                        } else {
                            ShowFullToast(response.data?.Message ?: "")
                        }
                        viewModel.getWalletHistoryData()
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                    response.message?.let { ShowFullToast(response.message) }
                    viewModel.getWalletHistoryData()
                }

                is NetworkResult.Loading -> {
                    viewModel.isloading.set(true)
                }
            }
        }
    }

    private fun bindHistoryData() {
        walletTransfersadapter =
            binding.rvWalletTransfers.setUpPagingMultiViewRecyclerAdapter { item: WidgetViewModel, binder: ViewDataBinding, position: Int ->
                binder.setVariable(BR.item, item)
                binder.executePendingBindings()
            }
        walletTransfersadapter.addLoadStateListener {
            if (it.source.refresh is LoadState.NotLoading &&
                it.append.endOfPaginationReached &&
                walletTransfersadapter.itemCount < 1
            ) {
                viewModel.nodata.set(walletTransfersadapter.itemCount == 0)
            }
        }

        val concatAdapter = walletTransfersadapter.withLoadStateFooter(
            footer = MultiViewPagingRecyclerFooterAdapter()
        )
        binding.rvWalletTransfers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_balancehistory, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_balancetransfer -> {
                findNavController().navigate(R.id.nav_walletpendingrequestFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}