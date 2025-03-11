package com.customer.offerswindow.model.dashboard

import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WishListResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<WishListData>?
)

data class WishListData(
    val Showroom_UID: String,
    val Showroom_Name: String,
    val Branch_UID: String,
    val Branch_Address: String,
    val Category_UID: String,
    val Category_Name: String,
    val Offer_ID: String,
    val Wishlist: ArrayList<Wishlist>,
    var isfavourite: Boolean = true
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.wishlist_item
    }
}

data class Wishlist(
    val Offer_ID: String,
    val Offer_Details: String,
    val Branch_UID: String,
    val Branch_Address: String,
    val Category_UID: String,
    val Category_Name: String,
    val Effective_Date: String,
    val End_Date: String,
    val Website_Url: String,
    val EmailID: String,
    val Google_Location: String,
    val Contact_No_1: String,
    val Contact_No_2: String,
    val Selected_Offers: ArrayList<SelectedOffers>
)

data class SelectedOffers(
    val Offer_Image_IDs: String,
    val Offer_Detailss: String,
    @Expose @SerializedName("Image_url") val imagepath: String,
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.homeroww_img
    }
}
