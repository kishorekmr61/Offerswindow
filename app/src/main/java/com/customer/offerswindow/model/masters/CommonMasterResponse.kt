package com.customer.offerswindow.model.masters

import com.customer.offerswindow.utils.getDateTime
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
    var Image_path: String ="",
    @SerializedName("Created_By")
    var CreatedBy: String = "",
    @SerializedName("Created_Date")
    var CreatedDate: String = getDateTime(),
)

