package com.customer.offerswindow.model

data class StockPurchsasePostingResponse(
    var Status: Int,
    val Message: String,
    var Data: postData
)

data class postData(
    var ID: Long,

)

