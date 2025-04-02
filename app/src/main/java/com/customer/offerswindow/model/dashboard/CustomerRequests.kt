package com.customer.offerswindow.model.dashboard

import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.utils.getDateTime


data class ProfileUpdateRequest(
    var CustomerId: String = AppPreference.read(Constants.USERUID, "") ?: "0",
    var CustomerName: String = "",
    var LastName: String = "",
    var EmailID: String = "",
    var PhoneNo: String = "",
    var DoB: String = "",
    var Location_Desc: String = "",
    var LocationId: String = "",
    var CountryId: String = "",
    var PinCode: String = "",
    var Country: String = "",
    var Country_Desc: String = "",
    var Pin_No: String = "",
    var CustomerPhotoFilePath: String = "",
    @Transient
    var CustomerImageUrl: String = "",
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

