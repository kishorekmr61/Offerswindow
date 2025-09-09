package com.customer.offerswindow.model.offerdetails

import com.customer.offerswindow.model.dashboard.Servicedata

data class SearchCriteriaResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<SearchCriteria>?
)
data class SearchCriteria(
    var Rec_ID : String="",
    var Cust_UID : String="",
    var Showroom_UID : String="",
    var Location_UID : String="",
    var Service_UID : String="",
    var Offer_Type : String="",
    var City_UID : String="",
    var Showroom_Name : String="",
    var Location_Name : String="",
    var Service_Desc : String="",
    var Offer_Expiry_Date : String="",
)