package com.customer.offerswindow.model.customersdata

data class PostSignUp(
    var phoneNo: String,
    var emailID: String,
    var customerName: String,
    var lastName: String,
    var otpValue: String,
    var PinNo: String,
)

data class UserSigUp(
    var FirstName: String="",
    var LastName: String="",
    var MobileNo: String="",
    var EmailId: String="",
)
