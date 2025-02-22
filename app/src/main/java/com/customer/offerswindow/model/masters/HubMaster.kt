package com.customer.offerswindow.model.masters

import com.google.gson.annotations.SerializedName

data class HubMaster(
    @SerializedName("Hub_Code") var hubcode: String,
    @SerializedName("Hub_Name") var hubname: String,
    @SerializedName("Hub_Status") var hubstatu: String,
    @SerializedName("Created_Date") var createddate: String,
    @SerializedName("Created_By") var createdby: String
)


