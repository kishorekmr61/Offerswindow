package com.customer.offerswindow.utils.bottomsheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.databinding.ViewDataBinding
import com.customer.offerswindow.BR
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.databinding.SpinnerresultsLyoutBinding
import com.customer.offerswindow.model.SpinnerRowModel
import com.customer.offerswindow.utils.notifyDataChange
import com.customer.offerswindow.utils.setUpMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.viewTouchable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SpinnerBottomSheet : BaseBottomSheetDialog<SpinnerresultsLyoutBinding>() {

    private lateinit var inflate: SpinnerresultsLyoutBinding
    private lateinit var mBehavior: BottomSheetBehavior<*>
    var selectedPos = -1
    private var date = ""
    private lateinit var mlocation: Location
    private var mbackuplist: ArrayList<SpinnerRowModel> = arrayListOf()

    companion object {
        const val TAG = "SpinnerBottomSheet"
        private var inItemSelectedListner: OnItemSelectedListner? = null
        var selectedvalue: String = ""
        var spinnerdataFlag = ""
        var outPutFormat = ""
        var mheaderlbl = ""
        var showcalender = false
        var searchspinnervalues: ArrayList<SpinnerRowModel> = arrayListOf()
        var colorList: ArrayList<Pair<String, String>> = arrayListOf()
        var mBundle = Bundle()

        fun newInstance(
            flag: String,
            mselectedvalue: String,
            spinerlistdata: ArrayList<SpinnerRowModel>,
            iscalendar: Boolean = false,
            listner: OnItemSelectedListner,
            bundle: Bundle = Bundle(),
            outPutFormat: String = "", headerlbl: String = ""
        ): SpinnerBottomSheet {
            spinnerdataFlag = flag
            selectedvalue = mselectedvalue
            searchspinnervalues = spinerlistdata
            inItemSelectedListner = listner
            showcalender = iscalendar
            mheaderlbl = headerlbl
            mBundle = bundle
            Companion.outPutFormat = outPutFormat
            return SpinnerBottomSheet()
        }

        fun newInstance(
            flag: String,
            mselectedvalue: String,
            spinerlistdata: ArrayList<SpinnerRowModel>,
            iscalendar: Boolean = false,
            headerlbl: String = "",
            colorData: ArrayList<Pair<String, String>>,
            listner: OnItemSelectedListner,
        ): SpinnerBottomSheet {
            spinnerdataFlag = flag
            selectedvalue = mselectedvalue
            searchspinnervalues = spinerlistdata
            inItemSelectedListner = listner
            showcalender = iscalendar
            mheaderlbl = headerlbl
            colorList = colorData
            return SpinnerBottomSheet()
        }

    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): SpinnerresultsLyoutBinding {
        inflate = SpinnerresultsLyoutBinding.inflate(inflater, container, false)
        return inflate
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, R.layout.spinnerresults_lyout, null)
        dialog.setContentView(view)
        mBehavior = BottomSheetBehavior.from(view.parent as View)
        return dialog
    }


    override fun onStart() {
        super.onStart()
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.header.text = mheaderlbl
        binding.recyclerView.visibility = View.VISIBLE
        binding.calenderlyout.calendarView.visibility = View.GONE
        getSpinnerItems(spinnerdataFlag, selectedvalue, searchspinnervalues)

        setClicklistener()
    }

    fun setClicklistener() {
//        binding.calenderlyout.btnconfirm.setOnClickListener {
//            if (inItemSelectedListner != null) {
//                inItemSelectedListner?.onItemSelectedListner(
//                    null,
//                    if (outPutFormat.isEmpty()) date else convertDate(
//                        date,
//                        Constants.DDMMYYYY,
//                        outPutFormat
//                    )
//                )
//                dismiss()
//            }
//        }
        binding.doneBtn.setOnClickListener {
            var arrayList = ArrayList<SpinnerRowModel>()
            searchspinnervalues.forEach {
                if (it.ischecked) {
                    arrayList?.add(
                        SpinnerRowModel(
                            it.title,
                            ischecked = true,
                            showColoursView = false,
                            tintColorCode = "",
                            mstCode = it.mstCode
                        )
                    )
                }
            }
            if (binding.locateEdt.text.toString().isNullOrEmpty()) {
                inItemSelectedListner?.onItemSelectedListner(
                    null, selectedvalue
                )
            } else if (Constants.LOCATION == spinnerdataFlag) {
                var location = ""
                location = binding.locateEdt.text.toString()
                inItemSelectedListner?.onItemSelectedListner(
                    null, location
                )
            }
            inItemSelectedListner?.onItemmultipleSelectedListner(
                null, arrayList
            )


            dismiss()
        }

        binding.locateEdt.doOnTextChanged { text, start, before, count ->
            run {
                filterList(binding.locateEdt.text.toString().trim())
                if (binding.locateEdt.text?.isEmpty() == true) {
                    setupRecyclerView(mbackuplist, selectedvalue)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        activity?.viewTouchable()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setupRecyclerView(spinnervalues: ArrayList<SpinnerRowModel>, value: String) {
        if (Constants.TASK == spinnerdataFlag) {
            spinnervalues.sortBy {
                it.tintColorCode?.toInt()
            }
        } else {
            spinnervalues.sortBy {
                it.title
            }
        }
        binding.recyclerView.setUpMultiViewRecyclerAdapter(spinnervalues) { item: SpinnerRowModel, binder: ViewDataBinding, pos: Int ->
            binder.setVariable(BR.spinneritem, item)
            if (!value.isNullOrEmpty()) {
                if (item.title == value) {
                    item.ischecked = true
                }
            }
            binder.setVariable(BR.onItemClick, View.OnClickListener {
                when (item) {
                    is SpinnerRowModel -> {
                        when (it.id) {
                            R.id.tvresult_lbl -> {
                                if (Constants.PURPOSE == spinnerdataFlag) {
                                    item.ischecked = !item.ischecked
                                    binding.recyclerView.notifyDataChange()
                                } else {
                                    selectedPos = pos
                                    inItemSelectedListner?.onItemSelectedListner(
                                        item,
                                        spinnerdataFlag
                                    )
                                    binding.recyclerView.adapter?.notifyDataSetChanged()
                                    dismiss()
                                }
                            }
                        }
                    }
                }
            })
            binder.executePendingBindings()
        }
    }

    fun getSpinnerItems(flag: Any, value: String, mspinnervalues: ArrayList<SpinnerRowModel>) {
        var spinnervalues: ArrayList<SpinnerRowModel> = arrayListOf()
        when (flag) {
            Constants.STATUS -> {
                spinnervalues.clear()
                binding.locateEdt.visibility = View.GONE
                binding.doneBtn.visibility = View.GONE
                spinnervalues.addAll(updateList(mspinnervalues, value))
                updateRecyclerview(spinnervalues, value)
            }

            Constants.LOCATION -> {
                spinnervalues.clear()
                binding.locateEdt.visibility = View.VISIBLE
                spinnervalues.addAll(updateList(mspinnervalues, value))
                updateRecyclerview(spinnervalues, value)
            }


            Constants.FILTER -> {
                spinnervalues.clear()
                binding.locateEdt.visibility = View.VISIBLE
                binding.doneBtn.visibility = View.GONE
                val hashSet = HashSet<SpinnerRowModel>()
                hashSet.addAll(mspinnervalues)
                mspinnervalues.clear()
                mspinnervalues.addAll(hashSet)
                spinnervalues.addAll(updateList(mspinnervalues, value))
                updateRecyclerview(spinnervalues, value)
            }
        }
    }


    private fun updateRecyclerview(spinnervalues: ArrayList<SpinnerRowModel>, value: String) {
        if (spinnervalues.isEmpty()) {
            setupRecyclerView(arrayListOf(), "")
            binding.noreults.clnoresults.visibility = View.VISIBLE
        } else {
            setupRecyclerView(spinnervalues, value)
            binding.noreults.clnoresults.visibility = View.GONE
        }

    }

    private fun updateList(
        mspinnervalues: ArrayList<SpinnerRowModel>,
        value: String
    ): ArrayList<SpinnerRowModel> {
        for (item in mspinnervalues) {
            item.ischecked = value == item.title
        }
        return mspinnervalues
    }


    private fun filterList(it1: String) {
        var filterlist =
            searchspinnervalues.filter {
                it.title.trim().lowercase().contains(it1.trim().lowercase())
            }
        if (filterlist.isEmpty()) {
            setupRecyclerView(arrayListOf(), "")
            binding.noreults.clnoresults.visibility = View.VISIBLE
        } else {
            setupRecyclerView(filterlist as ArrayList<SpinnerRowModel>, selectedvalue)
            binding.noreults.clnoresults.visibility = View.GONE
        }
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}


interface OnItemSelectedListner {
    fun onItemSelectedListner(titleData: SpinnerRowModel?, value: String)
    fun onItemmultipleSelectedListner(
        titleData: ArrayList<SpinnerRowModel>?,
        value: ArrayList<SpinnerRowModel>
    )
}


