package com.customer.offerswindow.model.masters

import androidx.databinding.BaseObservable
import com.customer.offerswindow.R
import com.customer.offerswindow.utils.resource.WidgetViewModel
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
    @SerializedName("Product_Code")
    var ProductCode: Int =0,
    @SerializedName("Created_By")
    var CreatedBy: String = "",
    @SerializedName("Created_Date")
    var CreatedDate: String = "",
): WidgetViewModel, BaseObservable() {
    override fun layoutId(): Int {
        return R.layout.row_servicestandc
    }
}
