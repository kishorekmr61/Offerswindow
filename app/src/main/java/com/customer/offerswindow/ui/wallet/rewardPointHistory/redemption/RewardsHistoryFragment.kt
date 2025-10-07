package com.customer.offerswindow.ui.wallet.rewardPointHistory.redemption

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import com.customer.offerswindow.databinding.FragmentRewardsHistoryBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.wallet.RewardHistory
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.ui.wallet.rewardPointHistory.RewardPointHistoryViewModel
import com.customer.offerswindow.utils.setUpMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setWhiteToolBar
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RewardsHistoryFragment : Fragment() {


    private val viewModel: RewardPointHistoryViewModel by viewModels()
    private val vm: DashBoardViewModel by activityViewModels()
    private var _binding: FragmentRewardsHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRewardsHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        lifecycleScope.launch {
            delay(100)
            activity?.setWhiteToolBar("Rewards Points History", true)
        }
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
        viewModel.isloading.set(true)
        viewModel.getRewardsHistoryData(AppPreference.read(Constants.USERUID, "0") ?: "0", 0)
    }


    private fun bindRewardsHistory(rewardHistory: ArrayList<RewardHistory>) {
        if (rewardHistory.isEmpty()) {
            viewModel.nodata.set(true)
            binding.nodataavaliable.textDesc.setText("No  Transactions")
        } else {
            viewModel.nodata.set(false)
        }
        binding.rvRewardpoints.setUpMultiViewRecyclerAdapter(
            rewardHistory
        ) { item: RewardHistory, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            binder.executePendingBindings()
        }
    }


    private fun setObserver() {
        viewModel.rewardsHistorydata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            viewModel.isloading.set(false)
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

    }

}