package com.customer.offerswindow.model

import android.os.Parcelable
import com.customer.offerswindow.model.Error
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorData(
    val errorList: List<Error>?,
    val httpStatus: String
): Parcelable