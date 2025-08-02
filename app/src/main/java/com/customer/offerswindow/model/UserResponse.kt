package com.customer.offerswindow.model

data class UserResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<Data>
)



data class OTPResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<Data>
)

data class Data(
    var Cust_UID: String,
    var Mobile_No: String,
    var Cust_Name: String,
    var Cust_Last_Name: String,
    var Email_ID: String,
    var Location_Code: String,
    var Location_Desc: String,
    var Sub_Location_Code: String,
    var Sub_Location_Desc: String,
    var Cust_Image_URL: String,
    var DOB: String,
    var Country: String,
    var Country_Desc: String,
    var Pin_Code: String,
    var Pin_No: String
)
