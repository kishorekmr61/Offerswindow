package com.customer.offerswindow.model.dashboard

import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DashBoardDataResponse(

    @Expose @SerializedName("Status") var Status: Int,
    @Expose @SerializedName("Message") val Message: String,
    @Expose @SerializedName("Data") var dashboardData: ArrayList<DashboardData>?
)

data class DashboardData(
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
    @Expose @SerializedName("Display_Date") val displaydate: String,
    @Expose @SerializedName("Effective_Date") val effectivedate: String,
    @Expose @SerializedName("End_Date") val endate: String,
    @Expose @SerializedName("Offer_Status") val offerstatus: String,
    @Expose @SerializedName("Website_link") val Website_link: String,
    var isfavourite: Boolean = false,
    @Expose @SerializedName("Image_Details") var ImagesList: ArrayList<Images>? = arrayListOf(),
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.homelist_item
    }
}

data class Images(
    @Expose @SerializedName("Rec_ID") val imageid: String,
    @Expose @SerializedName("Image_Path") val imagepath: String,
): WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.homeroww_img
    }
}



