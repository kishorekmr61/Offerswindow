package com.customer.offerswindow.model.dashboard


data class SubCoachesResponse(
    val Status: Int?,
    val Message: String?,
    val Data: ArrayList<SubCoachesData?>?
)

data class SubCoachesData(
    val Cust_ID: Int?,
    val User_Reference_ID: Int?,
    val Cust_Category: String?,
    val Cust_Name: String?,
    val Sur_Name: String?,
    val DOB: String?,
    val Email_ID: String?,
    val Gender: String?,
    val Marital_Status: String?,
    val Marriage_Anniversary: String?,
    val Fitness_Goal: Double?,
    val Height_CM: Double?,
    val Initial_Weight: Double?,
    val Location: Any?,
    val Cust_Image_Path: String?,
    val Coach_User_UID: String?,
    val Service_Coach_UID: Any?,
    val Cust_Status: String?,
    val Created_Date: String?,
    val Created_By: String?,
    val Updated_Date: Any?,
    val Updated_By: Any?,
    val Cust_UID: String?
)