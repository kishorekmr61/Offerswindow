package com.customer.offerswindow.model

data class PostNewEnquiry(
    var CustomerName: String = "",
    var MobileNo: String = "",
    var EmailID: String = "",
    var ReferenceMobileNo: String,
    var RelationCode: Int,
    var Age: Int,
    var AvailableTime: String,
    var Location: String,
    var PurposeDetails: ArrayList<Purposedetails>,
    var CreatedBy: String,
    var CreatedDateTime: String,
    var UpdatedBy: String,
    var UpdatedDateTime: String,
)

class Purposedetails(
    var PurposeCode: Int = 0
)
