package com.customer.offerswindow.ui.onboarding.welcome

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.customer.offerswindow.utils.helper.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private var networkHelper: NetworkHelper
) : ViewModel() {
    var isSuccess = ObservableField(false)
    val currentPin = ObservableField("none")
    val customerName = ObservableField("Welcome")
    val welcomeimage = ObservableField("")
    var isProgressVisible = ObservableField(false)


}