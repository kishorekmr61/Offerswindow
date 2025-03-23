package com.customer.offerswindow.ui.wallet.rewardPointHistory.redemption

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.databinding.FragmentRedemptionBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.SpinnerRowModel
import com.customer.offerswindow.model.wallet.RedemptionRequestBody
import com.customer.offerswindow.utils.bottomsheet.OnItemSelectedListner
import com.customer.offerswindow.utils.bottomsheet.SpinnerBottomSheet
import com.customer.offerswindow.utils.setWhiteToolBar
import com.customer.offerswindow.utils.showLongToast
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RedemptionFragment : Fragment() {


    private val viewModel: RedemptionViewModel by viewModels()
    private var _binding: FragmentRedemptionBinding? = null
    private val binding get() = _binding!!
    var transactiontype = arrayListOf<SpinnerRowModel>()

    companion object {
        fun newInstance() = RedemptionFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRedemptionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        lifecycleScope.launch {
            delay(100)
            activity?.setWhiteToolBar("Redeem Rewards", true)
        }
        setHasOptionsMenu(true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        viewModel.walletbalance.set(arguments?.getString("WALLETBALANCE"))
        transactiontype.clear()
        transactiontype.add(SpinnerRowModel("Amazon Voucher", false, false, mstCode = "0"))
        transactiontype.add(SpinnerRowModel("Transfer to account", false, false, mstCode = "0"))
        binding.redeemBtn.setOnClickListener {
            if (isValid()) {
                viewModel.isloading.set(true)
                val redemptionRequestBody = RedemptionRequestBody(
                    RewardPoints = binding.etNoofpoints.text.toString(),
                    TransactionType = binding.etTransactiontype.text.toString(),
                    RedemptionValue = binding.etValueofpoints.text.toString(),
                    AccountNo = binding.etWalletnumber.text.toString()
                )
                viewModel.postRedemption(redemptionRequestBody)
            }
        }

        binding.etTransactiontype.setOnClickListener {
            activity?.let { it1 ->
                val modalBottomSheet = SpinnerBottomSheet.newInstance(
                    Constants.STATUS,
                    binding.etTransactiontype.text.toString(), transactiontype, false, object :
                        OnItemSelectedListner {
                        override fun onItemSelectedListner(
                            titleData: SpinnerRowModel?,
                            datevalue: String
                        ) {
                            if (titleData != null) {
                                binding.etTransactiontype.setText(titleData.title)
                            }
                        }

                        override fun onItemmultipleSelectedListner(
                            titleData: ArrayList<SpinnerRowModel>?,
                            value: ArrayList<SpinnerRowModel>
                        ) {

                        }
                    })
                modalBottomSheet.show(it1.supportFragmentManager, SpinnerBottomSheet.TAG)
            }
        }
    }

    private fun isValid(): Boolean {
        if (binding.etNoofpoints.text.isNullOrEmpty()) {
            showToast("Enter No of Points")
            return false
        }
        if (binding.etValueofpoints.text.isNullOrEmpty()) {
            showToast("Enter No of Points")
            return false
        }
        if (binding.etTransactiontype.text.isNullOrEmpty()) {
            showToast("Select Transaction Type")
            return false
        }
        if (binding.etWalletnumber.text.isNullOrEmpty()) {
            showToast("Enter Account Number")
            return false
        }
        if (binding.etAddress.text.isNullOrEmpty()) {
            showToast("Enter Address")
            return false
        }
        return true
    }

    private fun setObserver() {
        viewModel.rewardsPostingResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    viewModel.isloading.set(false)
                    response.data?.let { resposnes ->
                        showLongToast(resposnes.Message)
                        findNavController().navigate(R.id.nav_home)
                    }
                }

                is NetworkResult.Error -> {
                    showLongToast(response.message ?: "")
                    viewModel.isloading.set(false)
                }

                else -> {}
            }
        }
    }
}