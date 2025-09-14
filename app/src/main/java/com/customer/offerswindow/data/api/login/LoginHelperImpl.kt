package com.customer.offerswindow.data.api.login

import com.customer.offerswindow.data.api.login.apiServices.MasterApiService
import com.customer.offerswindow.model.customersdata.PostSignUp
import com.customer.offerswindow.model.customersdata.PostUserIntrest
import com.customer.offerswindow.model.customersdata.PostWishlist
import com.customer.offerswindow.model.customersdata.PostuserSearch
import com.customer.offerswindow.model.customersdata.UserSigUp
import com.customer.offerswindow.model.dashboard.ProfileUpdateRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class LoginHelperImpl @Inject constructor(private val masterApiService: MasterApiService) {
    suspend fun getUserToken(mobilenumber: String, password: String) =
        masterApiService.getToken(mobilenumber, password)

    suspend fun getCommonMasterData(mastertype: String, parentid: String,lServiceId : String ="0") =
        masterApiService.getCommonMaster(mastertype, parentid,lServiceId)

    suspend fun getOtp(mobilenumber: String) =
        masterApiService.verifyPhone(mobilenumber)

    suspend fun postSignupOtp(usersignup: UserSigUp) =
        masterApiService.postSignupOTP(usersignup)

    suspend fun validateOTP(mobilenumber: String, otp: String,sPinNo : String) =
        masterApiService.validateOTP(mobilenumber, otp,sPinNo)

    suspend fun postSignUp(postSignUp: PostSignUp) =
        masterApiService.signupUser(postSignUp)


    suspend fun getCommonLocationMasterData(mastertype: String) =
        masterApiService.getCommonLocationMaster(mastertype)


    suspend fun getForgetPassword(mobilenumber: String) =
        masterApiService.forgotPassword(mobilenumber)

    suspend fun deleteUserAccount(lUserID: String) = masterApiService.deleteUserAccount(lUserID)


    suspend fun getNotifications(lCustomerID: String) = masterApiService.getNotifications(lCustomerID)

    suspend fun postWishList(postWishlist: PostWishlist) = masterApiService.postWishList(postWishlist)

    suspend fun postSearch(postuserSearch: PostuserSearch) = masterApiService.postSearh(postuserSearch)


    suspend fun submitProfileUpdateData(
        part: MultipartBody.Part?,
        formDataBody: RequestBody
    ) = masterApiService.submitProfileUpdateData(part, formDataBody)


    suspend fun submitProfileUpdateData(
       profileUpdateRequest: ProfileUpdateRequest
    ) = masterApiService.submitProfileUpdateData(profileUpdateRequest)

    suspend fun postUserIntrest(
       postUserIntrest: PostUserIntrest
    ) = masterApiService.submitUserIntrest(postUserIntrest)

    suspend fun getFilterData() =
        masterApiService.getFilterData()

    suspend fun getOfferChips( lServiceId: String) =
        masterApiService.getOfferChips( lServiceId)


    suspend fun GetOfferCategories( iOfferTypeId: String) =
        masterApiService.GetOfferCategories( iOfferTypeId)
}