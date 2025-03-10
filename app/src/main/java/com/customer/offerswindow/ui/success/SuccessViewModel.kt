package com.customer.offerswindow.ui.success

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.customer.offerswindow.repositry.Repository
import com.customer.offerswindow.utils.helper.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SuccessViewModel @Inject constructor(
    var app: Application,
) : ViewModel() {
    var isloading = ObservableField(false)
    var message = ObservableField<String>()
}