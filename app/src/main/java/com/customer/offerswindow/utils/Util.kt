package com.customer.offerswindow.utils

import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Patterns
import androidx.core.graphics.ColorUtils
import com.google.gson.Gson
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.model.CustomerData
import java.text.SimpleDateFormat
import java.util.*

object Util {
    fun isDark(color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) < 0.5
    }

    fun isDark(hexColor: String): Boolean {
        val colorInt = Color.parseColor(hexColor)
        return ColorUtils.calculateLuminance(colorInt) < 0.5
    }

}


fun getCustomerData(arguments: Bundle?): CustomerData {
    if (arguments != null) {
        arguments?.let {
            return Gson().fromJson(it.getString(Constants.Customertype), CustomerData::class.java)
        }
    }
    return CustomerData()
}


fun convertDate(date: String, inputformat: String, outformat: String): String {
    if (!TextUtils.isEmpty(date)) {
        val simpleDateFormat = SimpleDateFormat(inputformat, Locale.getDefault())
        val formatdate = try {
            simpleDateFormat.parse(date)?.let {
                SimpleDateFormat(
                    outformat,
                    Locale.getDefault()
                ).format(it)
            }
        } catch (e: Exception) {
            ""
        }
        return formatdate.toString()
    }
    return ""
}


fun getFilePathFromUri(contentResolver: ContentResolver, uri: Uri): String? {
    var filePath: String? = null
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            filePath = it.getString(columnIndex)
        }
    }
    cursor?.close()
    return filePath
}

fun isEmailValid(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

