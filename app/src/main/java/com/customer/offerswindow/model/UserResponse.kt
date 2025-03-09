package com.customer.offerswindow.model

data class UserResponse(
    var Status: Int,
    val Message: String,
    var Data: String
)

data class OTPResponse(
    var Status: Int,
    val Message: String
)

data class Data(
    var User_UID: String,
    var Enquiry_ID: String,
    var User_ID: String,
    var Cust_Name: String,
    var Email_ID: String,
    var Cust_Image_Path: String,
    var User_Group_ID: String,
    var Plan_Code: String,
    var Plan_Name: String,
    var User_Status: String,
    var Cust_Version: String="",

    )
