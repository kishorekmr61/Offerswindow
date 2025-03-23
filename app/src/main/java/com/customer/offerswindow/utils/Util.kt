package com.customer.offerswindow.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Patterns
import androidx.core.graphics.ColorUtils
import com.customer.offerswindow.R
import com.google.android.gms.common.util.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale


object Util {
    fun isDark(color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) < 0.5
    }

    fun isDark(hexColor: String): Boolean {
        val colorInt = Color.parseColor(hexColor)
        return ColorUtils.calculateLuminance(colorInt) < 0.5
    }

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

fun getFilePathFromURI(context: Context?, contentUri: Uri?): String? {
    //copy file and send new file path
    val fileName = getFileName(contentUri)
    if (!TextUtils.isEmpty(fileName)) {
        val tempImagesDir = context?.getString(R.string.temp_images_dir)?.let {
            File(
                context.filesDir, //this function gets the external cache dir
                it
            )
        }
        val tempImage = File(
            tempImagesDir, //prefix the new abstract path with the temporary images dir path
            context?.getString(R.string.temp_image)
        )
        val copyFile = tempImage
        copy(context!!, contentUri, tempImage)
        return copyFile.absolutePath
    }
    return null
}

fun getFileName(uri: Uri?): String? {
    if (uri == null) return null
    var fileName: String? = null
    val path = uri.path
    val cut = path!!.lastIndexOf('/')
    if (cut != -1) {
        fileName = path.substring(cut + 1)
    }
    return fileName
}

fun copy(context: Context, srcUri: Uri?, dstFile: File?) {
    try {
        val inputStream: InputStream = srcUri?.let { context.contentResolver.openInputStream(it) }
            ?: return
        val outputStream: OutputStream = FileOutputStream(dstFile)
        IOUtils.copyStream(inputStream, outputStream)
        inputStream.close()
        outputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

