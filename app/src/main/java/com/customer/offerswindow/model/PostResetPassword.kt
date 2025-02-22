package com.customer.offerswindow.model

data class PostResetPassword(
    var CustomerUID : String="",
    var OldPassword:String="",
    var NewPassword : String="",
    var CreatedBy : String="",
    var CreatedDateTime : String="",
    var UpdatedBy : String="",
    var UpdatedDateTime : String=""
)

data class PostPhoneNumber(
    var MobileNo : String="",
)