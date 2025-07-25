package com.customer.offerswindow.utils

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.webkit.MimeTypeMap
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.customer.offerswindow.R
import com.customer.offerswindow.application.OfferWindowApplication.Companion.context
import com.customer.offerswindow.data.constant.Constants
import com.google.android.material.textfield.TextInputEditText
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern


@BindingAdapter("imgUrl")
fun setImageUrl(img: AppCompatImageView, url: String?) {
    if (!TextUtils.isEmpty(url)) {
        img.load(url) {
            crossfade(false)
            placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
            transformations(RoundedCornersTransformation(25f))
        }
    } else {
        img.setImageResource(R.drawable.default_img)
    }
}

@BindingAdapter("imgNormalUrl")
fun setImageNormalUrl(img: AppCompatImageView, url: String?) {
    if (!TextUtils.isEmpty(url)) {
        img.load(url) {
            crossfade(false)
            placeholder(R.drawable.default_img).error(R.drawable.default_img)
            transformations(RoundedCornersTransformation(25f))
        }
    } else {
        img.setImageResource(R.drawable.default_img)
    }
}

@BindingAdapter("loadUrl")
fun loadUrl(img: AppCompatImageView, url: String?) {

    if (!TextUtils.isEmpty(url)) {
        val pattern = "(http(s?):/)(/[^/]+)+" + ".(?:jpg|jpeg|gif|png)"
        val m: Matcher = Pattern.compile(pattern).matcher(url)
        if (m.matches()) {
            img.load(url) {
                crossfade(false)
                placeholder(R.drawable.default_img)
            }
        }
    } else {
        img.setImageResource(R.drawable.default_img)
    }
}

@BindingAdapter("setImage")
fun setImageFromDrawable(img: AppCompatImageView, imageDrawable: Int) {
    img.setImageResource(imageDrawable)
}

@BindingAdapter("setImage")
fun setImageFromDrawable(img: CircleImageView, imageDrawable: Int) {
    img.setImageResource(imageDrawable)
}

@BindingAdapter("setImage")
fun setImageFromDrawable(img: AppCompatImageView, imageDrawable: Drawable) {
    img.setImageDrawable(imageDrawable)
}


@BindingAdapter("txtColor")
fun setTextColor(layout: AppCompatTextView, colorCode: String?) {
    if (!TextUtils.isEmpty(colorCode))
        layout.setTextColor(Color.parseColor(colorCode))
}

@BindingAdapter("txtColor")
fun setTextColor(layout: AppCompatTextView, colorCode: Int?) {
    if (!TextUtils.isEmpty(colorCode.toString()))
        colorCode?.let { layout.setTextColor(it) }
}

@BindingAdapter("txt")
fun setText(layout: AppCompatTextView, text: String?) {
    if (!TextUtils.isEmpty(text))
        text?.let { layout.text = it }
}


@BindingAdapter("setVisibility")
fun visibility(view: View, showView: Boolean) {
    if (showView) view.visibility = View.VISIBLE else view.visibility = View.GONE
}


fun convertToDate(datetime: String?): String? {
    if (!TextUtils.isEmpty(datetime)) {
        var datetime = datetime?.replace("'", "")
        val simpleDateFormat = SimpleDateFormat(Constants.YYYYMMDDTHH, Locale.getDefault())
        val formatdate = try {
            SimpleDateFormat(
                Constants.DDMMMYYYY,
                Locale.getDefault()
            ).format(simpleDateFormat.parse(datetime))
        } catch (e: Exception) {
            return datetime
        }
        return formatdate.toString()
    }
    return datetime
}

@BindingAdapter("setTextviewDate")
fun setTextviewDate(textview: AppCompatTextView, datetime: String?) {
    if (!datetime.isNullOrEmpty()) {
        textview.text = convertToDate(datetime)
    } else {
        textview.text = "N/A"
    }
}

@BindingAdapter("setTextviewChat")
fun setTextviewChat(textview: AppCompatTextView, datetime: String?) {
    textview.text = convertDate(datetime ?: "", Constants.YYYYMMDDTHH, Constants.YYYYMMMDDHHMM)
}

@BindingAdapter("setEdiTextDate")
fun setEdiTextDate(textview: TextInputEditText, datetime: String?) {
    textview.setText(convertToDate(datetime))
}

@BindingAdapter("setThumbnail")
fun setThumbnail(img: AppCompatImageView, videourl: String?) {
    val fileExt = MimeTypeMap.getFileExtensionFromUrl(videourl)
    if (!videourl.isNullOrEmpty() && videourl.contains("http") && fileExt != "m4a") {
        Glide.with(context)
            .asBitmap()
            .load(videourl).error(R.drawable.default_img)
            .into(img);
    }
}

@BindingAdapter("profileImgUrlRound", requireAll = false)
fun setProfileImageUrl(img: AppCompatImageView, url: String?) {
    val url1 =  url?.replace("http://", "https://")
    if (!url.isNullOrEmpty()) {
        Glide.with(context).load(url).error(R.drawable.ic_profile)
            .apply(RequestOptions.circleCropTransform()).into(img)

//        Glide.with(context).load(url1).placeholder(R.drawable.ic_profile).dontAnimate().into(img);
    } else {
        img.setImageResource(R.drawable.ic_profile)
    }
}

@BindingAdapter("image", "modifiedDate", requireAll = false)
fun setImageUrl(img: AppCompatImageView, url: String?, modifiedDate: String?) {
    var url =  url?.replace("http://", "https://");

    if (!url.isNullOrEmpty()) {
        Glide.with(context).load(url).placeholder(R.drawable.ic_profile).dontAnimate().into(img);

//        Glide.with(context).load(url).signature(ObjectKey(modifiedDate ?: "")).into(img)
    } else {
        img.setImageResource(R.drawable.default_img)
    }
}


