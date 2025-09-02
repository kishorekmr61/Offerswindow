package com.customer.offerswindow.model

data class OfferWindowCommonResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<postData>
)

data class postData(
    var ID: Long,

    )


data class PostOfferWindowCommonResponse(
    var Status: Int,
    val Message: String,
    var Data: Commondata
)

data class Commondata(
    var ID: Long,

    )



