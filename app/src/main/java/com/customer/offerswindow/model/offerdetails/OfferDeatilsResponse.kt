package com.customer.offerswindow.model.offerdetails

import com.customer.offerswindow.R
import com.customer.offerswindow.model.dashboard.DashboardData
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
    @Expose @SerializedName("Google_Location") val loction: String,
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
    val Terms_Conditions: ArrayList<Termsandconditions>,
    val Other_Offer_Details: ArrayList<DashboardData>,
)

data class Termsandconditions(
    @Expose @SerializedName("Rec_ID") val id: String,
    @Expose @SerializedName("Sort_Order") val sortorder: String,
    @Expose @SerializedName("Description") val Description: String,
): WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.row_tandc
    }
}
