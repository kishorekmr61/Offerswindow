package com.customer.offerswindow.ui.wallet.successdialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.customer.offerswindow.R
import com.customer.offerswindow.databinding.SuccessDailogFragmentBinding
import com.customer.offerswindow.model.leads.PostFollowUpResponse
import com.customer.offerswindow.ui.wallet.successdialog.SuccessDailogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuccessDailogFragment : DialogFragment() {

    private val viewModel: SuccessDailogViewModel by viewModels()
    var _binding: SuccessDailogFragmentBinding? = null
    val binding get() = _binding!!


    companion object {

        lateinit var dataobJect: PostFollowUpResponse
        lateinit var totalvpcount: String
        fun newInstance(data: PostFollowUpResponse, mtotalvpcount: String): SuccessDailogFragment {
            dataobJect = data
            totalvpcount = mtotalvpcount
            return SuccessDailogFragment()
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SuccessDailogFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeImg.setOnClickListener {
            dialog?.dismiss()
        }
        binding.paymentdescTxt.text = dataobJect.Message
        binding.totalamountTxt.text = getString(R.string.total_amount_n_rs).plus(totalvpcount)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}