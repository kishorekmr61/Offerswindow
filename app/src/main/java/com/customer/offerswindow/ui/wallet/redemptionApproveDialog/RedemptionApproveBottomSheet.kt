package com.customer.offerswindow.ui.wallet.redemptionApproveDialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.databinding.RedemptionapproveLyoutBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.SpinnerRowModel
import com.customer.offerswindow.model.masters.CommonMasterResponse
import com.customer.offerswindow.model.wallet.RedemptionRequestBody
import com.customer.offerswindow.ui.myReferences.MyReferencesViewModel
import com.customer.offerswindow.utils.bottomsheet.BaseBottomSheetDialog
import com.customer.offerswindow.utils.bottomsheet.OnItemSelectedListner
import com.customer.offerswindow.utils.bottomsheet.SpinnerBottomSheet
import com.customer.offerswindow.utils.showFullToast
import com.customer.offerswindow.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RedemptionApproveBottomSheet : BaseBottomSheetDialog<RedemptionapproveLyoutBinding>() {

    private lateinit var inflate: RedemptionapproveLyoutBinding
    private lateinit var mBehavior: BottomSheetBehavior<*>
    private val viewModel: RedemptionApprovalViewModel by viewModels()
    private val referenceviewModel: MyReferencesViewModel by viewModels()

    lateinit var purposedata: SpinnerRowModel
    var masterdata = arrayListOf<SpinnerRowModel>()

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): RedemptionapproveLyoutBinding {
        inflate = RedemptionapproveLyoutBinding.inflate(inflater, container, false)
        return inflate
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, R.layout.paymentmodes_lyout, null)
        dialog.setContentView(view)
        mBehavior = BottomSheetBehavior.from(view.parent as View)
        return dialog
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObserver()
        referenceviewModel.getStatusMstData()
        binding.viewModel = viewModel
        viewModel.amount.set(arguments?.getString("REWARDBALANCE"))
        binding.submitBtn.setOnClickListener {
            if (isPaymentValid()) {
                viewModel.isloading.set(true)
                var redemptionRequestBody = RedemptionRequestBody()
                redemptionRequestBody.RewardPoints = binding.etAmount.text.toString()
                redemptionRequestBody.PurposeCode = purposedata.mstCode
                viewModel.postWalletApproval(redemptionRequestBody)
            }
        }
        binding.etPurpose.setOnClickListener {
            activity?.let { it1 ->
                val modalBottomSheet = SpinnerBottomSheet.newInstance(
                    Constants.PRODUCTS,
                    binding.etPurpose.text.toString(), masterdata, false, object :
                        OnItemSelectedListner {
                        override fun onItemSelectedListner(
                            titleData: SpinnerRowModel?,
                            datevalue: String
                        ) {
                            if (titleData != null) {
                                purposedata = titleData
                                binding.etPurpose.setText(titleData?.title)
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
        binding.closeImg.setOnClickListener {
            dismiss()
        }

    }


    private fun isPaymentValid(): Boolean {
        if (binding.etAmount.text.toString().isNullOrEmpty()) {
            showToast("Please enter amount")
            return false
        }
        if(!::purposedata.isInitialized){
            showToast("Please select purpose")
            return false
        }

        return true
    }

    private fun setObserver() {
        referenceviewModel.masterdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        viewModel.isloading.set(false)
                        if (resposnes?.Status == 200) {
                            updateMasterdata(resposnes)
                        } else {
                            com.customer.offerswindow.utils.showFullToast(
                                response.data?.Message ?: ""
                            )
                        }
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                    response.message?.let { com.customer.offerswindow.utils.showFullToast(response.message) }
                }

                is NetworkResult.Loading -> {

                }
            }
        }
        viewModel.postingResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data.let { resposnes ->
                        viewModel.isloading.set(false)
                        if (resposnes?.Status == 200) {
                            com.customer.offerswindow.utils.showFullToast(
                                response.data?.Message ?: getString(R.string.something_went_wrong)
                            )
                            dismiss()
                        } else {
                            com.customer.offerswindow.utils.showFullToast(
                                response.message ?: getString(R.string.something_went_wrong)
                            )
                        }
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                    response.message?.let { com.customer.offerswindow.utils.showFullToast(response.message) }
                }

                is NetworkResult.Loading -> {
//                    viewModel.isloading.set(true)
                }
            }
        }
    }





    private fun updateMasterdata(resposnes: CommonMasterResponse) {
        resposnes.data.forEach {
            if (it.MstType == "Redemption_Purpose" && it.MstStatus == "A") {
                masterdata.add(SpinnerRowModel(it.MstDesc, false, false, "", it.MstCode))
            }
        }
    }
}


