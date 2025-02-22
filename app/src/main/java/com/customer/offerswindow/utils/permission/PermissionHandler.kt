package com.customer.offerswindow.utils.permission

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

const val PERMISSION_REQUEST_CODE = 222

object PermissionHandler {

    fun requestPermission(
        activity: Activity,
        successAction: () -> Unit,
        requestPermissions: Array<String>
    ) {
        if (needPermission(activity, requestPermissions)) {
            getPermissions(activity, requestPermissions)
        } else {
            successAction.invoke()
        }
    }

    private fun needPermission(activity: Activity, requestPermissions: Array<String>): Boolean {
        for (permission in requestPermissions) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) == PackageManager.PERMISSION_DENIED
            ) {
                return true
            }

        }
        return false
    }


    private fun getPermissions(activity: Activity, requestedPermissions: Array<String>) {
        ActivityCompat.requestPermissions(activity, requestedPermissions, PERMISSION_REQUEST_CODE)
    }

    private fun getPermissions(fragment: Fragment, requestedPermissions: Array<String>) {
        fragment.requestPermissions(requestedPermissions, PERMISSION_REQUEST_CODE)
    }

    private fun shouldShowRationalAllPermissions(
        activity: Activity,
        requestedPermissions: Array<String>
    ): Boolean {
        for (permission in requestedPermissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true
            }
        }
        return false
    }

    fun permissionResult(
        activity: Activity,
        requestedPermissions: Array<String>,
        requestCode: Int,
        grantResults: IntArray,
        successAction: () -> Unit,
        unSuccessAction: () -> Unit,
        errorMessageAction: (() -> Unit)? = null
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.size == requestedPermissions.size) {
            var allGranted = true
            for (gr in grantResults) {
                allGranted = allGranted && gr == PackageManager.PERMISSION_GRANTED
            }
            when {
                allGranted -> successAction.invoke()
                shouldShowRationalAllPermissions(
                    activity,
                    requestedPermissions
                ) -> unSuccessAction.invoke()

                else -> errorMessageAction?.invoke()
            }
        }

    }
}

