package com.customer.offerswindow.utils.bottomsheet

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.R
import com.customer.offerswindow.databinding.FragmentPinBottomSheetBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.wallet.RedemptionRequestBody
import com.customer.offerswindow.ui.wallet.rewardPointHistory.redemption.RedemptionViewModel
import com.customer.offerswindow.utils.showLongToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PinBottomSheetFragment : Fragment() {

    private val viewModel: RedemptionViewModel by viewModels()
    private var _binding: FragmentPinBottomSheetBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPinBottomSheetBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        binding.pinview.requestFocus()
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.pinview, InputMethodManager.SHOW_IMPLICIT)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        binding.pinview.setAnimationEnable(true);
        binding.pinview.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 4) {
                    viewModel.isloading.set(true)
                    val redemptionRequestBody = RedemptionRequestBody(
                        RewardPoints = arguments?.getString("RewardPoints", "0") ?: "0",
                        TransactionType = arguments?.getString("TransactionType", "") ?: "",
                        RedemptionValue = arguments?.getString("RedemptionValue", "") ?: "",
                        AccountNo = arguments?.getString("AccountNo", "") ?: ""
                    )
                    viewModel.postRedemption(redemptionRequestBody)
                }
            }
        })
//        binding.pinview.setOtpCompletionListener {
//
//        }
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
