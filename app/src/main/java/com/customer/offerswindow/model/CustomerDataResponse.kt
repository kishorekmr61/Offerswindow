package com.customer.offerswindow.model

data class CustomerDataResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<CustomerData>?
)

data class CustomerData(
    var Cust_UID: String = "",
    var Mobile_No: String = "",
    var Cust_Name: String? = "",
    var Cust_Last_Name: String? = "",
    var Location_Code: String = "",
    var Location_Desc: String = "",
    var Email_ID: String = "",

    )
