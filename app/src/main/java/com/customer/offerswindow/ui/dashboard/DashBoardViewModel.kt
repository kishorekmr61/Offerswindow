package com.customer.offerswindow.ui.dashboard

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.customer.offerswindow.repositry.DashBoardRepositry
import com.customer.offerswindow.utils.helper.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val repository: DashBoardRepositry,
    private var networkHelper: NetworkHelper,
    var app: Application,
) : ViewModel() {

    var hidetoolbar= MutableLiveData<Boolean>()
    var profilepic = ObservableField<String>()
    var username = ObservableField<String>()
    var isvisble = MutableLiveData<Boolean>()

}