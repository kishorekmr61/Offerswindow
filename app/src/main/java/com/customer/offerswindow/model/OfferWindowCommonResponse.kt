package com.customer.offerswindow.model

data class OfferWindowCommonResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<postData>
)

data class postData(
    var ID: Long,

    )



