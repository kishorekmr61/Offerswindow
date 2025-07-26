package com.customer.offerswindow.ui.wallet.rewardPointHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.BR
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.RewarPointHistoryFragmentBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.wallet.RewardHistory
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.utils.setUpMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setWhiteToolBar
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RewarPointHistoryFragment : Fragment() {

    private val viewModel: RewardPointHistoryViewModel by viewModels()
    private val vm: DashBoardViewModel by activityViewModels()
    private var _binding: RewarPointHistoryFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = RewarPointHistoryFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RewarPointHistoryFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        lifecycleScope.launch {
            delay(100)
            activity?.setWhiteToolBar("Rewards Points History", true)
        }
        setHasOptionsMenu(true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        vm.btabselectedpostion.value = 3
        viewModel.getRewardsHistoryData(AppPreference.read(Constants.USERUID, "0") ?: "0", 0)
        binding.redeemTxt.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("WALLETBALANCE", viewModel.walletbalance.get())
            findNavController().navigate(R.id.nav_redeemption, bundle)
        }
    }

    private fun setObserver() {
        viewModel.rewardsHistorydata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            viewModel.isloading.set(false)
                            viewModel.walletbalance.set(resposnes.Data.Table.firstOrNull()?.Rewards_Balance)
                            bindRewardsHistory(resposnes.Data.Table1)
                        } else {
                            showToast(response.data?.Message ?: "")
                        }
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                    response.message?.let { showToast(response.message) }
                }

                is NetworkResult.Loading -> {
                    viewModel.isloading.set(true)
                }
            }
        }

        viewModel.rewardBalance.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            viewModel.isloading.set(false)
                            viewModel.walletbalance.set(resposnes.rewardsHistory.firstOrNull()?.RewardsBalance)
                            viewModel.getRewardsHistoryData(
                                AppPreference.read(
                                    Constants.USERUID,
                                    "0"
                                ) ?: "0", 0
                            )
                        } else {
                            showToast(response.data?.Message ?: "")
                            viewModel.getRewardsHistoryData(
                                AppPreference.read(
                                    Constants.USERUID,
                                    "0"
                                ) ?: "0", 0
                            )
                        }
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                    response.message?.let { showToast(response.message) }
                }

                is NetworkResult.Loading -> {
                    viewModel.isloading.set(true)
                }
            }
        }
    }

    private fun bindRewardsHistory(rewardHistory: ArrayList<RewardHistory>) {
        binding.rvRewardpoints.setUpMultiViewRecyclerAdapter(
            rewardHistory
        ) { item: RewardHistory, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            binder.executePendingBindings()
        }
    }


}