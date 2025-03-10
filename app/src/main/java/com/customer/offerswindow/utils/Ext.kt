package com.customer.offerswindow.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.customer.offerswindow.R
import com.customer.offerswindow.application.OfferWindowApplication.Companion.context
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.model.ErrorData
import com.customer.offerswindow.utils.bottomsheet.OnItemSelectedListner
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


fun View.VISIBLE() {
    this.visibility = View.VISIBLE
}

fun View.INVISIBLE() {
    this.visibility = View.INVISIBLE
}

fun View.GONE() {
    this.visibility = View.GONE
}

fun TextView.setViewTextColor(colorCode: Int) {
    this.setTextColor(
        ContextCompat.getColor(
            this.context, colorCode
        )
    )
}

fun Activity.hideOnBoardingToolbar() {
    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    toolbar?.navigationIcon = null
    findViewById<View>(R.id.rvAppBar)?.visibility = View.GONE
}


fun Activity.setWhiteToolBar(
    title: String,
    isBackDisplay: Boolean = true,
    showAppBarDivider: Boolean = true,
    showtoolbar: Boolean = false
) {
    try {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        showToolbar()
        if (isBackDisplay) {
            toolbar?.navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_back)
            toolbar?.navigationIcon?.setTint(getColor(R.color.white))
        } else {
            toolbar.navigationIcon = null
        }
        findViewById<AppCompatTextView>(R.id.tvToolBarTitle)?.text = title
//    if (!TextUtils.isEmpty(desc)) {
////        findViewById<AppCompatTextView>(R.id.tvToolBarDesc)?.text = desc
//        findViewById<AppCompatTextView>(R.id.tvToolBarDesc)?.visibility = View.VISIBLE
//    } else {
        findViewById<AppCompatTextView>(R.id.tvToolBarDesc)?.visibility = View.GONE
//    }

        findViewById<AppCompatTextView>(R.id.tvToolBarTitle)?.setTextColor(
            resources.getColor(
                R.color.white, null
            )
        )
        findViewById<Toolbar>(R.id.toolbar)?.setBackgroundColor(
            ContextCompat.getColor(
                this, R.color.primary
            )
        )
        showStatusBar()
        window.statusBarColor = getColor(R.color.white)
        val params =
            findViewById<RelativeLayout>(R.id.rvAppBar).layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        findViewById<AppBarLayout>(R.id.appbar).setExpanded(true, true)


        /*VISIBLE divider in Other screen*/
        findViewById<View>(R.id.appBarDivider)?.visibility =
            if (showAppBarDivider) View.VISIBLE else View.GONE

        findViewById<View>(R.id.rvAppBar)?.visibility = View.VISIBLE
    } catch (e: Exception) {
        e.printStackTrace()
    }

}


fun Activity.setStatusBar(color: Int, lightTheme: Boolean, colorInString: String = "") {
    if (TextUtils.isEmpty(colorInString)) window.statusBarColor = getColor(color)
    else window.statusBarColor = Color.parseColor(colorInString)

    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = lightTheme
}


fun Activity.hideToolbar() {
    findViewById<AppBarLayout>(R.id.appbar).visibility = View.GONE
}

fun Activity.showToolbar() {

    try {
        findViewById<AppBarLayout>(R.id.appbar).visibility = View.VISIBLE
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Activity.setToolbarVisibility(visibility: Int = View.VISIBLE) {
    try {
        findViewById<RelativeLayout>(R.id.rvAppBar).visibility = visibility
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun View.hideKeyboard() {
    try {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    } catch (e: Exception) {
    }
}

public fun Fragment.showToast(message: String) {
    Toast.makeText(this.requireActivity(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showUnderDevelopment() {
    Toast.makeText(this.requireActivity(), "Under Development", Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(message: String) {
    Toast.makeText(this.requireActivity(), message, Toast.LENGTH_LONG).show()
}


fun ErrorData.getError(getErrorStatus: Boolean = false): String {
    return if (getErrorStatus) {
        var errorStatus = ""
        this.errorList?.firstOrNull()?.errorCode?.let {
            errorStatus = it
        }
        errorStatus
    } else {
        var errorMessage = "Unable to process your request."
        this.errorList?.firstOrNull()?.errorMsg?.let {
            errorMessage = it
        }
        errorMessage
    }
}

fun Activity.viewNotTouchable() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
}

fun Activity.viewTouchable() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}


fun RecyclerView.notifyDataChange() {
    this.post {
        this.adapter?.notifyDataSetChanged()
    }
}

private fun appInstalledOrNot(url: String): Boolean {
    val packageManager: PackageManager = context.packageManager
    val app_installed: Boolean = try {
        packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
    return app_installed
}

fun EditText.makeClearableEditText(
    onIsNotEmpty: ((String) -> Unit)?, onClear: (() -> Unit)?, clearDrawable: Drawable
) {
    val updateRightDrawable = {
        this.setCompoundDrawables(
            compoundDrawables[COMPOUND_DRAWABLE_LEFT_INDEX],
            null,
            if (text.isNotEmpty()) clearDrawable else null,
            null
        )
    }
    updateRightDrawable()

    this.doAfterTextChanged {
        if (it?.isNotEmpty() == true) {
            onIsNotEmpty?.invoke(it.toString())
        } else {
            onClear?.invoke()
        }
        updateRightDrawable()
    }
    this.onRightDrawableClicked {
        this.text.clear()
        this.setCompoundDrawables(compoundDrawables[COMPOUND_DRAWABLE_LEFT_INDEX], null, null, null)
        onClear?.invoke()
        this.requestFocus()
    }
}

private const val COMPOUND_DRAWABLE_RIGHT_INDEX = 2
private const val COMPOUND_DRAWABLE_LEFT_INDEX = 0


@SuppressLint("ClickableViewAccessibility")
fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
    setOnTouchListener { v, event ->
        var hasConsumed = false
        if (v is EditText) {
            if (event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
        }
        hasConsumed
    }
}

fun Activity.showCustomSnackBar(
    message: String, drawable: Int = R.drawable.success
): Snackbar {
    val snackbar = Snackbar.make(
        this.findViewById(android.R.id.content), "", Snackbar.LENGTH_SHORT
    )
    val custom_view: View = layoutInflater.inflate(R.layout.custom_toast, null)

    val snackBarView = snackbar.view as Snackbar.SnackbarLayout
    snackBarView.setPadding(0, 0, 0, 0)

    (custom_view.findViewById<View>(R.id.toast_msg) as TextView).text = message
    var imageview = (custom_view.findViewById<View>(R.id.img_toast) as ImageView)
    if (drawable == null || drawable == R.drawable.success) {
        imageview.setImageResource(R.drawable.success)
    } else {
        imageview.setImageResource(drawable)
    }
    snackBarView.addView(custom_view, 0)
    snackbar.show()
    return snackbar
}

fun String.getError(getErrorStatus: Boolean = false): String {
    return try {
        val jsonObject = JSONObject(this).get("errorData").toString()
        val errorData = Gson().fromJson(jsonObject, ErrorData::class.java)
        errorData.getError(getErrorStatus)
    } catch (e: Exception) {
        "Something went wrong"
    }
}


fun Activity.hideStatusBar() {
    window?.let { WindowCompat.setDecorFitsSystemWindows(it, true) }
    window?.let {
        WindowInsetsControllerCompat(window!!, it.decorView).apply {
            hide(WindowInsetsCompat.Type.statusBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

fun Activity.showStatusBar() {
    window?.let { WindowCompat.setDecorFitsSystemWindows(it, true) }
    window?.let {
        WindowInsetsControllerCompat(window!!, it.decorView).apply {
            show(WindowInsetsCompat.Type.statusBars())
        }
    }
}

fun Activity.convertDate(
    date: String, inputformat: String, outformat: String
): String {
    if (!date.isNullOrEmpty()) {
        val simpleDateFormat = SimpleDateFormat(inputformat, Locale.getDefault())
        val formatdate = SimpleDateFormat(
            outformat, Locale.getDefault()
        ).format(simpleDateFormat.parse(date))
        return formatdate.toString()
    } else return ""
}


fun Fragment.handleHardWareBackClick(onClick: () -> Unit) {
    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onClick.invoke()
        }
    }
    requireActivity().onBackPressedDispatcher.addCallback(
        this, onBackPressedCallback
    )
}

internal val ViewPager?.isEmpty: Boolean get() = this?.adapter?.count == 0
internal val ViewPager2?.isEmpty: Boolean get() = this?.adapter?.itemCount == 0


fun String.getFileNameFromURL(): String? {
    if (this == null) {
        return ""
    }
    try {
        val resource = URL(this)
        val host: String = resource.getHost()
        if (host.length > 0 && this.endsWith(host)) {
            // handle ...example.com
            return ""
        }
    } catch (e: MalformedURLException) {
        return ""
    }
    val startIndex = this.lastIndexOf('/') + 1
    val length = this.length

    // find end index for ?
    var lastQMPos = this.lastIndexOf('?')
    if (lastQMPos == -1) {
        lastQMPos = length
    }

    // find end index for #
    var lastHashPos = this.lastIndexOf('#')
    if (lastHashPos == -1) {
        lastHashPos = length
    }

    // calculate the end index
    val endIndex = Math.min(lastQMPos, lastHashPos)
    return this.substring(startIndex, endIndex)
}


fun Boolean.toInt() = if (this) 1 else 0


fun getDateTime(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return currentDateTime.format(formatter)
}

fun showCalenderDialog(
    context: Context, listner: OnItemSelectedListner, outPutFormat: String, maxdate: Long
) {
    var c = Calendar.getInstance()
    var mYear = c.get(Calendar.YEAR)
    var mMonth = c.get(Calendar.MONTH) + 1
    var mDay = c.get(Calendar.DAY_OF_MONTH)
    val datePickerDialog = DatePickerDialog(
        context, { view, year, monthOfYear, dayOfMonth ->
            run {
                var smonth = monthOfYear + 1
                var monthc: String = if ((smonth + 1).toString().length == 1) {
                    "0$smonth"
                } else {
                    smonth.toString()
                }
                var date = "${dayOfMonth}/$monthc/${year}"
                listner.onItemSelectedListner(
                    null, convertDate(
                        date, Constants.DDMMYYYY, outPutFormat
                    )
                )
            }
        }, mYear, mMonth, mDay
    )
    if (maxdate != 0L) {
        datePickerDialog.datePicker.maxDate = maxdate
    }
    datePickerDialog.show()

}


fun Fragment.showCommonCustomIOSDialog(
    context: Context,
    title: String,
    description: String,
    positiveText: String,
    positiveCallback: () -> Unit,
    negativeText: String,
    negativeCallback: () -> Unit,
    visible: Boolean = true
) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.setContentView(R.layout.dialog_ios_style)

    val tvTitle = dialog.findViewById(R.id.tvTitle) as AppCompatTextView
    val tvDescription = dialog.findViewById(R.id.tvDescription) as AppCompatTextView
    val tvPositive = dialog.findViewById(R.id.tvPositive) as AppCompatTextView
    val tvNegative = dialog.findViewById(R.id.tvNegative) as AppCompatTextView

    tvTitle.text = title
    tvDescription.text = description
    tvPositive.text = positiveText
    tvPositive.setTextColor(context.getColor(R.color.primary))
    tvNegative.text = negativeText
    tvNegative.setTextColor(Color.BLUE)
    if (!visible) tvNegative.visibility = View.GONE

    tvPositive.setOnClickListener {
        positiveCallback.invoke()
        dialog.dismiss()
    }
    tvNegative.setOnClickListener {
        negativeCallback.invoke()
        dialog.dismiss()
    }
    dialog.show()
}


fun Fragment.ShowFullToast(message: String) {
    if (!TextUtils.isEmpty(message)) {
        val snackbar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content), "", Snackbar.LENGTH_INDEFINITE
        )
        val customSnackView: View = layoutInflater.inflate(R.layout.custom_toast, null)
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(0, 0, 0, 0)
        val toast_msg = customSnackView.findViewById<AppCompatTextView>(R.id.toast_msg)
        toast_msg.text = message
        val ok_txt = customSnackView.findViewById<AppCompatTextView>(R.id.ok_txt)
        ok_txt.setOnClickListener {
            snackbar.dismiss()
        }
        snackbarLayout.addView(customSnackView, 0)
        snackbar.show()
    }
}

fun BottomSheetDialogFragment.showFullToast(message: String) {
    if (!TextUtils.isEmpty(message)) {
        var snackbar = dialog?.window?.decorView?.let {
            Snackbar.make(
                it, "", Snackbar.LENGTH_INDEFINITE
            )
        }

        val customSnackView: View = layoutInflater.inflate(R.layout.custom_toast, null)
        snackbar?.view?.setBackgroundColor(Color.TRANSPARENT)
        val snackbarLayout = snackbar?.view as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(0, 0, 0, 0)
        val toast_msg = customSnackView.findViewById<AppCompatTextView>(R.id.toast_msg)
        toast_msg.text = message
        val ok_txt = customSnackView.findViewById<AppCompatTextView>(R.id.ok_txt)
        ok_txt.setOnClickListener {
            snackbar.dismiss()
        }
        snackbarLayout.addView(customSnackView, 0)
        snackbar.show()

    }
}

fun DialogFragment.showFullToast(message: String) {
    if (!TextUtils.isEmpty(message)) {
        var snackbar = dialog?.window?.decorView?.let {
            Snackbar.make(
                it, "", Snackbar.LENGTH_INDEFINITE
            )
        }

        val customSnackView: View = layoutInflater.inflate(R.layout.custom_toast, null)
        snackbar?.view?.setBackgroundColor(Color.TRANSPARENT)
        val snackbarLayout = snackbar?.view as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(0, 0, 0, 0)
        val toast_msg = customSnackView.findViewById<AppCompatTextView>(R.id.toast_msg)
        toast_msg.text = message
        val ok_txt = customSnackView.findViewById<AppCompatTextView>(R.id.ok_txt)
        ok_txt.setOnClickListener {
            snackbar.dismiss()
        }
        snackbarLayout.addView(customSnackView, 0)
        snackbar.show()

    }
}


fun Fragment.openURL(uri: Uri?) {
    try {
        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(true)
        val customTabsIntent = builder.build()
        val browserIntent =
            Intent().setAction(Intent.ACTION_VIEW).addCategory(Intent.CATEGORY_BROWSABLE)
                .setType("text/plain").setData(Uri.fromParts("https://", "", null))
        var possibleBrowsers: List<ResolveInfo>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            possibleBrowsers = requireActivity().packageManager.queryIntentActivities(
                browserIntent, PackageManager.MATCH_DEFAULT_ONLY
            )
            if (possibleBrowsers.isEmpty()) {
                possibleBrowsers = requireActivity().packageManager.queryIntentActivities(
                    browserIntent, PackageManager.MATCH_ALL
                )
            }
        } else {
            possibleBrowsers = requireActivity().packageManager.queryIntentActivities(
                browserIntent, PackageManager.MATCH_DEFAULT_ONLY
            )
        }
        if (possibleBrowsers.size > 0) {
            customTabsIntent.intent.setPackage(possibleBrowsers[0].activityInfo.packageName)
            customTabsIntent.launchUrl(requireActivity(), uri!!)
        } else {
            val browserIntent2 = Intent(Intent.ACTION_VIEW, uri)
            requireActivity().startActivity(browserIntent2)
        }
    } catch (ex: Exception) {
        showToast("Please check meeting Link ,looks invited link is not valid.")
    }


}

fun Double.roundTo(n: Int): Double {
    return "%.${n}f".format(this).toDouble()
}
