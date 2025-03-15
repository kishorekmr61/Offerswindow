package com.customer.offerswindow.model.masters

data class ShowRoomsResponse(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<ShowRoomData> ? = arrayListOf()
)

data class ShowRoomData(
    val Showroom_UID: String,
    val Showroom_Category: String,
    val Showroom_Name: String,
)
