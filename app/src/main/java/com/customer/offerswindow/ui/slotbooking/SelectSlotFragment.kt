package com.customer.offerswindow.ui.slotbooking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.customer.offerswindow.BR
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentSelectSlotBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.customersdata.PostSlotBooking
import com.customer.offerswindow.model.dashboard.SlotsData
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.utils.CalendarAdapter
import com.customer.offerswindow.utils.HorizontalItemDecoration
import com.customer.offerswindow.utils.setWhiteToolBar
import com.customer.offerswindow.utils.showLongToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class SelectSlotFragment : Fragment() {

    private var _binding: FragmentSelectSlotBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SelectSlotViewModel by viewModels()
    private val vm: DashBoardViewModel by activityViewModels()

    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val dates = ArrayList<Date>()
    private lateinit var adapter: CalendarAdapter
    private val calendarList2 = ArrayList<CalendarDateModel>()
    var slotList = ArrayList<SlotsData>()
    var pcurrentDate = ""
    var servicwe = ""
    var location = ""
    var showroom = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectSlotBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.location.set(arguments?.getString("Location"))
        activity?.setWhiteToolBar("Slot Bookings", true)
        setObserver()
        vm.hidetoolbar.value = false
        viewModel.isloading.set(true)
        servicwe = arguments?.getString("SERVICEID") ?: ""
        location = arguments?.getString("LOCATIONID") ?: ""
        showroom = arguments?.getString("SHOWROOMID") ?: ""
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        pcurrentDate = sdf.format(Date())
        viewModel.getSlotsData(servicwe, location, showroom, pcurrentDate)
        binding.contactnameTxt.text = AppPreference.read(Constants.NAME, "")
        binding.contactcallTxt.text = AppPreference.read(Constants.MOBILENO, "")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        setListeners()
        setUpClickListener()
        setUpCalendar()
    }

    private fun setUpClickListener() {
        binding.ivCalendarNext.setOnClickListener {
            cal.add(Calendar.MONTH, 1)
            setUpCalendar()
        }
        binding.ivCalendarPrevious.setOnClickListener {
            cal.add(Calendar.MONTH, -1)
            if (cal == currentDate)
                setUpCalendar()
            else
                setUpCalendar()
        }
    }

    private fun setUpAdapter() {
        var int = 10
        val spacingInPixels = resources.getDimensionPixelSize(int)
        binding.rvCalender.addItemDecoration(HorizontalItemDecoration(spacingInPixels))
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvCalender)
        adapter = CalendarAdapter { calendarDateModel: CalendarDateModel, position: Int ->
            calendarList2.forEachIndexed { index, calendarModel ->
                calendarModel.isSelected = index == position
            }
            adapter.setData(calendarList2)
        }
        binding.rvCalender.adapter = adapter
    }

    private fun setUpCalendar() {
        val calendarList = ArrayList<CalendarDateModel>()
        binding.tvDateMonth.text = sdf.format(cal.time)
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        while (dates.size < maxDaysInMonth) {
            dates.add(monthCalendar.time)
            calendarList.add(CalendarDateModel(monthCalendar.time))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendarList2.clear()
        calendarList2.addAll(calendarList)
        adapter.setData(calendarList)
    }

    private fun setObserver() {
        viewModel.slotsDataResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        slotList.addAll(resposnes.Data ?: arrayListOf())
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                }

                else -> {}
            }
        }
        viewModel.slotPostingResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        showLongToast(resposnes.Message)
                        findNavController().navigate(R.id.nav_success)
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                }

                else -> {}
            }
        }
    }

    private fun setListeners() {
        binding.setVariable(BR.onItemClick, View.OnClickListener {
            when (it.id) {
                R.id.booknow_btn -> {
                    viewModel.isloading.set(true)
                    var postbooking = PostSlotBooking(pcurrentDate, showroom, "1", servicwe, "1")
                    viewModel.postSlotBooking(postbooking)
                }
            }

        })
    }
}