package com.customer.offerswindow.ui.webpages

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentWebBinding
import com.customer.offerswindow.model.masters.CommonDataResponse
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.utils.handleHardWareBackClick
import com.customer.offerswindow.utils.setWhiteToolBar


class WebFragment : Fragment() {
    private val cmsWebViewModel: WebViewModel by activityViewModels()
    private val vm: DashBoardViewModel by activityViewModels()
    private lateinit var binding: FragmentWebBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentWebBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = cmsWebViewModel
        if (arguments?.getString(Constants.ISFROM) == Constants.Web_Link_Offers) {
            cmsWebViewModel.url.set(arguments?.getString(Constants.WEB_URL) ?: "www.google.com")
            init()
        } else {
            setUpObserver()
            cmsWebViewModel.url.set(AppPreference.read(Constants.ABOUTUS, ""))
            init()
//            cmsWebViewModel.setLoadingState(true)
//            cmsWebViewModel.getMstData()
        }
        handleHardWareBackClick {
            backClick()
        }
        return binding.root
    }

    private fun setUpObserver() {
        cmsWebViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun backClick() {
        findNavController().popBackStack()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setWhiteToolBar("About Us")
//        vm.btabselectedpostion.value = 4
    }


    fun init() {

        /*requireActivity().findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            findNavController().popBackStack()
        }*/

        binding.webView.settings.javaScriptEnabled = true

        binding.webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                cmsWebViewModel.setLoadingState(true)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(cmsWebViewModel.url.get() ?: "")
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                cmsWebViewModel.setLoadingState(false)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                cmsWebViewModel.setLoadingState(false)
            }

        }
        binding.webView.loadUrl(cmsWebViewModel.url.get() ?: "")

    }

    fun loadWebView(commonDataResponse: CommonDataResponse) {
//        cmsWebViewModel.url.set(commonDataResponse.MstDesc)
        init()
    }

}

private class SSLTolerentWebViewClient : WebViewClient() {
    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        handler.proceed() // Ignore SSL certificate errors
    }
}