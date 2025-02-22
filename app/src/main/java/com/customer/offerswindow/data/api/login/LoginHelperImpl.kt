package com.customer.offerswindow.data.api.login

import com.customer.offerswindow.data.api.login.ApiServices.MasterApiService
import javax.inject.Inject

class LoginHelperImpl @Inject constructor(private val masterApiService: MasterApiService) {
    suspend fun getUserToken(mobilenumber: String, password: String) =
        masterApiService.getToken(mobilenumber, password)
    suspend fun getHubMasterData(mastertype: String,parentid : String) = masterApiService.getHubMaster(mastertype,parentid)
    suspend fun getCommonMasterData(mastertype: String,parentid : String,Userid : String) = masterApiService.getCommonMaster(mastertype,parentid,Userid)

    suspend fun getCommonPlanMasterData(mastertype: String,parentid : String) = masterApiService.getCommonPlanMaster(mastertype,parentid)
    suspend fun getPlanBasedProductsData(mastertype: String,parentid : String) = masterApiService.getPlanBasedProductsData(mastertype,parentid)
    suspend fun getUserinfo(mobilenumber: String, password: String) =
        masterApiService.verifylogin(mobilenumber, password)

    suspend fun getFlavoursMasterData(iProductID: String,lUserID :String) = masterApiService.getFlavoursMaster(iProductID,lUserID)

    suspend fun getCommonLocationMasterData(mastertype: String) = masterApiService.getCommonLocationMaster(mastertype)



    suspend fun getForgetPassword(mobilenumber: String) =
        masterApiService.forgotPassword(mobilenumber)
//    suspend fun postResetPassword(postResetPassword: PostResetPassword) =
//        masterApiService.resetPassword(postResetPassword)

    suspend fun deleteUserAccount(lUserID :String) = masterApiService.deleteUserAccount(lUserID)
}