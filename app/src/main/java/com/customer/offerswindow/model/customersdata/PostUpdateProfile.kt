package com.customer.offerswindow.model.customersdata

data class PostUpdateProfile(
    var customerId: String,
    var phoneNo: String,
    var customerName: String,
    var lastName: String,
    var emailID: String,
    var createdBy: String,
    var createdDateTime: String,
    var updatedBy: String,
    var updatedDateTime: String,
)
