package com.customer.offerswindow.ui.wallet.wallettransfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentWalletTransferBinding
import com.customer.offerswindow.utils.setWhiteToolBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalletTransferFragment : Fragment() {

    private val viewModel: WalletTransferViewModel by viewModels()
    private var _binding: FragmentWalletTransferBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = WalletTransferFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWalletTransferBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        lifecycleScope.launch {
            delay(100)
            activity?.setWhiteToolBar("Wallet Balance Transfer", true)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.name.set(AppPreference.read(Constants.NAME,"")?:"")
        viewModel.email.set(AppPreference.read(Constants.EMAIL,"")?:"")
        viewModel.getWalletBalanceData(
            AppPreference.read(
                Constants.USERUID,
                "0"
            ) ?: "0"
        )
    }

}