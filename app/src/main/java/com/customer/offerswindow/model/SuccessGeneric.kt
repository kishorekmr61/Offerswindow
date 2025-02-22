package com.customer.offerswindow.model

import com.google.gson.annotations.SerializedName

data class SuccessGeneric(

    @SerializedName("results") var results: Results?,
    @SerializedName("errorData")var errorData: ErrorData? = null
)

data class Results(
    @SerializedName("status") var status: String? = "Something wrong"
)

data class CustomizeResponse(
    var errorData: ErrorData? = null,
    val results: String? = ""
)


data class setFavData(

    @SerializedName("crmId") var crmId: String? = null,
    @SerializedName("favouritePlaceList") var favouritePlaceList: ArrayList<FavouritePlaceList>

)


data class FavouritePlaceList(

    @SerializedName("placeId") var placeId: String = "",
    @SerializedName("placeName") var placeName: String? = null,
    @SerializedName("placeAddress1") var placeAddress1: String? = null,
    @SerializedName("placeAddress2") var placeAddress2: String? = null,
    @SerializedName("placeCity") var placeCity: String? = null,
    @SerializedName("placeState") var placeState: String? = null,
    @SerializedName("placeCountry") var placeCountry: String? = null,
    @SerializedName("placePinCode") var placePinCode: String? = null,
    @SerializedName("placeLatitude") var placeLatitude: Double = 0.0,
    @SerializedName("placeLongitude") var placeLongitude: Double = 0.0

)
