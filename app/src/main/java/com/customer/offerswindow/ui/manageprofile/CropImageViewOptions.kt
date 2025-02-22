package com.customer.offerswindow.ui.manageprofile

import android.util.Pair
import com.theartofdev.edmodo.cropper.CropImageView
import com.theartofdev.edmodo.cropper.CropImageView.CropShape
import com.theartofdev.edmodo.cropper.CropImageView.Guidelines

internal class CropImageViewOptions {
    var scaleType = CropImageView.ScaleType.CENTER_INSIDE
    var cropShape = CropShape.RECTANGLE
    var guidelines = Guidelines.ON_TOUCH
    var aspectRatio = Pair(1, 1)
    var autoZoomEnabled = false
    var maxZoomLevel = 0
    var fixAspectRatio = false
    var multitouch = false
    var showCropOverlay = false
    var showProgressBar = false
    var flipHorizontally = false
    var flipVertically = false
}
