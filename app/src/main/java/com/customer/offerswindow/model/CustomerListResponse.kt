package com.customer.offerswindow.model

data class CustomerListResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<CustomerData>?
)

data class CustomerData(
    var Cust_Code: String = "",
    var Cust_Category: String = "",
    var Cust_Name: String? = "",
    var Sur_Name: String? = "",
    var Mobile_No: String? = "",
    var Email_ID: String = "",
    var User_Reference_ID: String = "",
    var User_UID: String = "",
    var Coach_User_UID: String = "",
    var Coach_Name: String = "",
    var Coach_Mobile_No: String = "",
    var Coach_Email_ID: String = "",
    var DOB: String? = "",
    var Age: String? = "",
    var Gender: String = "",
    var Marital_Status: String? = "",
    var Marriage_Anniversary: String? = "",
    var Fitness_Goal: String? = "",
    var Height_CM: String? = "",
    var Initial_Weight: String? = "",
    var Location: String = "",
    var Last_Plan_Start_Date: String = "",
    var Last_Plan_Expiry_Date: String = "",
    var Last_Activated_Plan_Name: String = "",
    var Last_Pan_Completed_Visits: String = "",
    var Last_Paln_Pending_Visits: String = "",
    var Cust_Image_Path: String = "",
    var Rewards_Balance: String = "",
    var Rewards_Value: String = "",
    var Wallet_Balance: String = "",
    var PDD_Status: String = "",
    var Online_Opted: String = "",
    var Sale_Started: String = "",
    var Cust_Status: String = "",
    var BMI: Double? = 0.0,
    var Last_Body_Weight: String = "N/A",
    var Login_Status: String = "",
    var Reference_Given_By: String = "",

    )
