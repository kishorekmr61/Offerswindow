package com.customer.offerswindow.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.customer.offerswindow.BR
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentNotificationsBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.notification.NotificationsData
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.utils.setUpMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setWhiteToolBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationsViewModel by viewModels()
    private val dashBoardViewModel: DashBoardViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        dashBoardViewModel.isvisble.value = false
        activity?.setWhiteToolBar("Notifications", true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        viewModel.isloading.set(true)
        viewModel.getUserInfo(AppPreference.read(Constants.USERUID, "") ?: "0")
    }

    private fun setObserver() {
        viewModel.notificationinfo.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        viewModel.isloading.set(false)
                        setrecylerData(resposnes.data)
                    }
                    viewModel.nodata.set(response.data?.data?.isEmpty())
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                }

                else -> {}
            }
        }
    }

    private fun setrecylerData(data: ArrayList<NotificationsData>) {
        binding.rvNotifications.setUpMultiViewRecyclerAdapter(
            data
        ) { item: NotificationsData, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            binder.setVariable(BR.onItemClick, View.OnClickListener {

                binder.executePendingBindings()
            })
        }
    }
}