package com.customer.offerswindow.ui.wallet.redemption

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.customer.offerswindow.BR
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.RedemptionApprovalFragmentBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.wallet.RedemptionApproval
import com.customer.offerswindow.utils.setUpMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setWhiteToolBar
import com.customer.offerswindow.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RedemptionApprovalFragment : Fragment() {

    companion object {
        fun newInstance() = RedemptionApprovalFragment()
    }

    private val viewModel: RedemptionApprovalViewModel by viewModels()
    private var _binding: RedemptionApprovalFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RedemptionApprovalFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        activity?.setWhiteToolBar("Redemption Approval", true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        viewModel.isloading.set(true)
        viewModel.getRedemptionApprovalData(AppPreference.read(Constants.USERUID, "0") ?: "0", 0)

    }

    private fun setObserver() {
        viewModel.redemptionApprovalResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            viewModel.isloading.set(false)
                            bindCustomeFeedBackData(resposnes.resdemptionapproval)
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

    private fun bindCustomeFeedBackData(redemptionApproval: ArrayList<RedemptionApproval>) {
        binding.rvRedemptionapproval.setUpMultiViewRecyclerAdapter(
            redemptionApproval
        ) { item: RedemptionApproval, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            binder.executePendingBindings()
        }

    }

}