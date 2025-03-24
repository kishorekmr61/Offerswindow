package com.customer.offerswindow.model

import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference

data class CustomerDataResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<CustomerData>?
)

data class CustomerData(
    var Cust_UID: String ? ="",
    var Mobile_No: String = "",
    var Cust_Name: String? = "",
    var Cust_Last_Name: String? = "",
    var Location_Code: String = "",
    var Country: String = "",
    var Country_Desc: String = "",
    var Location_Desc: String = "",
    var Email_ID: String = "",
    var Cust_Image_URL: String ? = "",
    var DOB: String = "",
    var Pin_No: String = "",

    )
