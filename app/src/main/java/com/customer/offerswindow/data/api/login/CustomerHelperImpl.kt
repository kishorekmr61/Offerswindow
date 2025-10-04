package com.customer.offerswindow.data.api.login

import com.customer.offerswindow.data.api.login.apiServices.CustomerApiService
import com.customer.offerswindow.model.PostPhoneNumber
import com.customer.offerswindow.model.customersdata.PostSignUp
import javax.inject.Inject

class CustomerHelperImpl @Inject constructor(
    private val customerApiService: CustomerApiService,
) {
    suspend fun postSignUp(postSignUp: PostSignUp) =
        customerApiService.postSignUp(postSignUp)


    suspend fun postOTPData(postPhoneNumber: PostPhoneNumber) =
        customerApiService.postOTPData(postPhoneNumber)

}