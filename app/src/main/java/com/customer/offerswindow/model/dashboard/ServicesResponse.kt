package com.customer.offerswindow.model.dashboard


data class OfferTypeResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<Servicedata>?
)
data class OfferTypedata(
    val Mst_Code: String,
    val Mst_Type: String,
    val Mst_Desc: String,
    val Mst_Status: String,
)



data class ServicesResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<Servicedata>?
)
data class Servicedata(
    val Mst_Code: String,
    val Mst_Desc: String,
    val Image_path: String
)