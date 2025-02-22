package com.customer.offerswindow.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Error(
    val errorCode: String,
    val errorMsg: String?
): Parcelable