package com.customer.offerswindow.ui.onboarding.otpdialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.customer.offerswindow.R
import com.customer.offerswindow.databinding.OtpDialogFragmentBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.stock.StockPurchsasePostingResponse
import com.customer.offerswindow.model.stock.postData
import com.customer.offerswindow.ui.wallet.otpdialog.OtpDialogViewModel
import com.customer.offerswindow.utils.bottomsheet.BaseBottomSheetDialog
import com.customer.offerswindow.utils.showFullToast
import com.customer.offerswindow.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpDialogFragment : BaseBottomSheetDialog<OtpDialogFragmentBinding>()  {

    private val viewModel: OtpDialogViewModel by viewModels()
    var _binding: OtpDialogFragmentBinding? = null
    private lateinit var inflate: OtpDialogFragmentBinding
    private lateinit var mBehavior: BottomSheetBehavior<*>
    var datetime: String = ""

    companion object {
        var isfrom = ""
        private var otpStatusListener: OtpStatusListener? = null
        lateinit var mobileno: String
        fun newInstance(
            mmobileno: String,
            misfrom: String, listner: OtpStatusListener,
        ): OtpDialogFragment {
            isfrom = misfrom
            mobileno = mmobileno
            otpStatusListener = listner
            return OtpDialogFragment()
        }

    }



    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): OtpDialogFragmentBinding {
        inflate = OtpDialogFragmentBinding.inflate(inflater, container, false)
        return inflate
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, R.layout.otp_dialog_fragment, null)
        dialog.setContentView(view)
        mBehavior = BottomSheetBehavior.from(view.parent as View)
        return dialog
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        binding.resendTxt.setOnClickListener {
            viewModel.isloading.set(true)
            viewModel.postOtp(mobileno)
        }
        binding.closeImg.setOnClickListener {
            dismiss()
        }
        viewModel.isloading.set(true)
        binding.otpverificationTxt.text =
            getString(R.string.please_enter_the_4_digit_code_sent_to).plus("\n")
                .plus(mobileno)

        binding.verifyBtn.setOnClickListener {
            if (binding.pinView.text.toString().isNullOrEmpty()) {
                showToast("please enter received OTP")
            } else {
                viewModel.isloading.set(true)
                viewModel.postVerifyOtp(mobileno, binding.pinView.text.toString())
            }


        }
    }


    private fun setObserver() {
        viewModel.verifyResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    viewModel.isloading.set(false)
                    if (response.data?.Status == 200) {
                        val message =  response.data?.Message ?: response.message
                        ?: getString(R.string.something_went_wrong)
                        com.customer.offerswindow.utils.showFullToast(
                            response.data?.Message ?: response.message
                            ?: getString(R.string.something_went_wrong)
                        )
                        otpStatusListener?.OTPSuccess(
                            response.data ?: StockPurchsasePostingResponse(
                                200,
                                message,
                                Data = postData(0)
                            )
                        )
                        dismiss()
                    } else {
                        val message =  response.data?.Message ?: response.message
                        ?: getString(R.string.something_went_wrong)
                        com.customer.offerswindow.utils.showFullToast(
                            message
                        )
                        binding.pinView.setText( "")
                    }
                }

                is NetworkResult.Error -> {
//                    otpStatusListener?.OtpWalletFailure(response.data)
                    viewModel.isloading.set(false)
                    response.message?.let { com.customer.offerswindow.utils.showFullToast(response.message) }
                }

                is NetworkResult.Loading -> {
                    viewModel.isloading.set(true)
                }
            }
        }
        viewModel.otpResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    viewModel.isloading.set(false)
                    if (response.data?.Status == 200) {
//                        otpStatusListener?.OtpWalletFailure(response.data)
                        com.customer.offerswindow.utils.showFullToast(
                            response.data?.Message ?: response.message
                            ?: getString(R.string.something_went_wrong)
                        )
                    } else {
                        com.customer.offerswindow.utils.showFullToast(
                            response.data?.Message ?: response.message
                            ?: getString(R.string.something_went_wrong)
                        )
                    }
                }

                is NetworkResult.Error -> {
//                    otpStatusListener?.OtpWalletFailure(response.data)
                    viewModel.isloading.set(false)
                    response.message?.let { com.customer.offerswindow.utils.showFullToast(response.message) }
                }

                is NetworkResult.Loading -> {
                    viewModel.isloading.set(true)
                }
            }
        }
    }


    interface OtpStatusListener {
        fun OTPSuccess(stockPurchsasePostingResponse: StockPurchsasePostingResponse)
        fun OtpFailure(stockPurchsasePostingResponse: StockPurchsasePostingResponse)
    }


}