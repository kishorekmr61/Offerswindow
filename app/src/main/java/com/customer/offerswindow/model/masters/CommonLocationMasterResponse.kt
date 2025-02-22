package com.customer.offerswindow.model.masters

data class CommonLocationMasterResponse(


    var Status: Int,
    val Message: String,
    var Data: ArrayList<LocationsData>

)

data class LocationsData(
    var Location_Desc: String = "",
)
