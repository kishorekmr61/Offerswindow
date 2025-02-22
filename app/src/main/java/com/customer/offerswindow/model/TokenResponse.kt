package com.customer.offerswindow.model

data class TokenResponse(
    var access_token: String,
    var token_type: String,
    var expires_in: String,
    val error:String?="",
    val error_description:String?="",
)
