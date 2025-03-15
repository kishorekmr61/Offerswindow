package com.customer.offerswindow.data.api.login

import com.customer.offerswindow.data.api.login.apiServices.MasterApiService
import com.customer.offerswindow.model.customersdata.PostSignUp
import com.customer.offerswindow.model.customersdata.PostWishlist
import javax.inject.Inject

class LoginHelperImpl @Inject constructor(private val masterApiService: MasterApiService) {
    suspend fun getUserToken(mobilenumber: String, password: String) =
        masterApiService.getToken(mobilenumber, password)

    suspend fun getCommonMasterData(mastertype: String, parentid: String) =
        masterApiService.getCommonMaster(mastertype, parentid)

    suspend fun getOtp(mobilenumber: String) =
        masterApiService.verifyPhone(mobilenumber)

    suspend fun validateOTP(mobilenumber: String, otp: String) =
        masterApiService.validateOTP(mobilenumber, otp)

    suspend fun postSignUp(postSignUp: PostSignUp) =
        masterApiService.signupUser(postSignUp)


    suspend fun getCommonLocationMasterData(mastertype: String) =
        masterApiService.getCommonLocationMaster(mastertype)


    suspend fun getForgetPassword(mobilenumber: String) =
        masterApiService.forgotPassword(mobilenumber)

    suspend fun deleteUserAccount(lUserID: String) = masterApiService.deleteUserAccount(lUserID)


    suspend fun getNotifications(lCustomerID: String) = masterApiService.getNotifications(lCustomerID)

    suspend fun postWishList(postWishlist: PostWishlist) = masterApiService.postWishList(postWishlist)
}