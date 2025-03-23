package com.customer.offerswindow.model.dashboard

import com.customer.offerswindow.utils.getDateTime


data class ProfileUpdateRequest(
    var CustomerId: String = "",
    var CustomerName: String = "",
    var LastName: String = "",
    var EmailID: String = "",
    var PhoneNo: String = "",
    var DoB: String = "",
    var LocationId: String = "",
    var CountryId: String = "",
    var PinCode: String = "",
    var CustomerPhotoFilePath: String = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTCB6HqWvcVC3HlKduT7nP44d_RYuJOIFTwcA4LQQo0zA8GRbW_N9wEwaF1kBiMPoKcnN4&usqp=CAU",
    @Transient
    var CustomerImageUrl: String = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTCB6HqWvcVC3HlKduT7nP44d_RYuJOIFTwcA4LQQo0zA8GRbW_N9wEwaF1kBiMPoKcnN4&usqp=CAU",
    var CreatedBy: String = "",
    var CreatedDateTime: String = getDateTime(),
    var UpdatedBy: String = "",
    var UpdatedDateTime: String = getDateTime()
)


data class ProfileUpdateResponse(
    val Status: Int?,
    val Message: String?,
    val Data: Data?
)

data class Data(
    val ID: Int?
)

