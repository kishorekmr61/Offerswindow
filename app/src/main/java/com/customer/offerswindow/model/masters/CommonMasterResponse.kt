package com.customer.offerswindow.model.masters

import com.customer.offerswindow.data.constant.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CommonMasterResponse(
    @Expose
    @SerializedName("Status")
    var Status: Int,
    @Expose
    @SerializedName("Message")
    val Message: String,
    @SerializedName("Data")
    var data: ArrayList<CommonDataResponse>

)

data class CommonDataResponse(
    @SerializedName("Mst_Code")
    var MstCode: String = "",
    @SerializedName("Mst_Type")
    var MstType: String = "",
    @SerializedName("Mst_Desc")
    var MstDesc: String = "",
    @SerializedName("Mst_Status")
    var MstStatus: String = "",
    @SerializedName("Image_path")
    var Image_path: String = "",
    @SerializedName("Rec_ID")
    var Rec_ID: String = "",
    @SerializedName("Price_Date")
    var Price_Date: String = "",
    @SerializedName("Gold_24c")
    var Gold_24c: String = "",
    @SerializedName("Gold_22c")
    var Gold_22c: String = "",
    @SerializedName("Gold_18c")
    var Gold_18c: String = "",
    @SerializedName("Silver")
    var Silver: String = "",
    @SerializedName("Diamonds")
    var Diamonds: String = "",
    @SerializedName("Created_By")
    var CreatedBy: String = "",
    @SerializedName("Created_Date")
    var CreatedDate: String = "",
) {

    fun convertDate(): String {
        return com.customer.offerswindow.utils.convertDate(
            Price_Date,
            Constants.YYYYMMDDTHH,
            "dd-MMM-yyyy"
        )
    }
}