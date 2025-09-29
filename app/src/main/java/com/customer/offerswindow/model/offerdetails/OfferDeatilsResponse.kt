package com.customer.offerswindow.model.offerdetails

import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.utils.convertDate
import com.customer.offerswindow.utils.resource.WidgetViewModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OfferDeatilsResponse(
    var Status: Int,
    val Message: String,
    var Data: OfferDeatils
)

data class OfferDeatils(
    @Expose @SerializedName("Rec_ID") val id: String,
    @Expose @SerializedName("Showroom_UID") val showroomid: String,
    @Expose @SerializedName("Showroom_Name") val showroomname: String,
    @Expose @SerializedName("Location_UID") val locationid: String,
    @Expose @SerializedName("Location_Desc") val locationname: String,
    @Expose @SerializedName("Google_Location") val GoogleLocation: String,
    @Expose @SerializedName("Contact_No_1") val contact: String,
    @Expose @SerializedName("Contact_No_2") val alaternativecontact: String,
    @Expose @SerializedName("Open_Time") val opentime: String,
    @Expose @SerializedName("End_Time") val endtime: String,
    @Expose @SerializedName("Service_UID") val serviceid: String,
    @Expose @SerializedName("Service_Desc") val servicedesc: String,
    @Expose @SerializedName("Offer_Type_Code") val offertypecode: String,
    @Expose @SerializedName("Offer_Details") val Offer_Details: String,
    @Expose @SerializedName("Display_Date") val displaydate: String,
    @Expose @SerializedName("Effective_Date") val effectivedate: String,
    @Expose @SerializedName("Slot_Applicable") val Slot_Applicable: String? = "",
    @Expose @SerializedName("Booking_Applicable") val Booking_Applicable: String? = "",
    @Expose @SerializedName("End_Date") val endate: String,
    @Expose @SerializedName("Offer_Status") val offerstatus: String,
    @Expose @SerializedName("Website_link") val Website_link: String,
    @Expose @SerializedName("Offer_Type_Desc") val Offer_Type_Desc: String,
    @Expose @SerializedName("Display_Image") var DisplayImage: String,
    var Wishlist_Status: String = "",
    @Expose @SerializedName("Image_Details") var ImagesList: ArrayList<OfferImages>? = arrayListOf(),
    var Video_Link: String = "",
    var isfavourite: Boolean = false,
    val Terms_Conditions: ArrayList<Termsandconditions>? = arrayListOf(),
    val Other_Offer_Details: ArrayList<OfferDeatils>? = arrayListOf(),
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.detailhomelist_item
    }

    fun convertEndDate(): String {
        return  convertDate(endate, Constants.YYYYMMDDTHH, "dd-MM-yyyy")
    }

    fun getStartDate(): String{
        return convertDate(
            effectivedate,
            Constants.YYYYMMDDTHH,
            "dd-MM-yyyy"
        )
    }

    fun getWishlistData(): Boolean {
        if (Wishlist_Status == "Yes") {
            isfavourite = true
            return isfavourite
        } else {
            return isfavourite
        }
    }
}

data class Termsandconditions(
    @Expose @SerializedName("Rec_ID") val id: String,
    @Expose @SerializedName("Sort_Order") val sortorder: String,
    @Expose @SerializedName("Description") val Description: String,
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.row_tandc
    }
}

data class OfferImages(
    @Expose @SerializedName("Rec_ID") val imageid: String,
    @Expose @SerializedName("Image_Path") val imagepath: String,
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.detailsroww_img
    }
}
