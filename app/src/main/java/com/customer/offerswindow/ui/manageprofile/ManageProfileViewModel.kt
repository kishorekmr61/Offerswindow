package com.customer.offerswindow.ui.manageprofile

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.customer.offerswindow.utils.helper.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageProfileViewModel @Inject constructor(
    private var networkHelper: NetworkHelper,
) : ViewModel() {
    var isImgCaptured = ObservableField(false)

//    fun updateProfilePicture(imageBase64: String,isCVPProfilePresent : Boolean): MutableLiveData<Resource<UpdateDeleteProfileImgResponse>> {
//        val updateProfileResponse = MutableLiveData<Resource<UpdateDeleteProfileImgResponse>>()
//        val accessTokenActive = (System.currentTimeMillis() -
//                AppPreference.read(AppPreference.ACCCESS_TOKEN_VALID_FROM, 0L)) < Constants.ACCESS_TOKEN_VALIDITY
//        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
//            updateProfileResponse.postValue(Resource.error("Something went wrong", null))
//        }
//        viewModelScope.launch(exceptionHandler) {
//            if (networkHelper.isNetworkConnected()) {
//                if (accessTokenActive) {
//                    val authorization =
//                        "Bearer " + AppPreference.read(AppPreference.ACCCESS_TOKEN, "")
//                    val updateProfileImageRequest = UpdateProfileImageRequest(
//                        AppPreference.read(
//                            AppPreference.CUSTOMER_HASH,
//                            ""
//                        )!!, AppPreference.read(AppPreference.CRM_ID, "") ?: "", imageBase64
//                    )
//                    accountsRepository.updateProfileImage(
//                        authorization, updateProfileImageRequest,isCVPProfilePresent
//                    ).onStart {
//                        updateProfileResponse.postValue(Resource.loading(null))
//                    }.catch { e ->
//                        when (e) {
//                            is ClientRequestException -> {
//                                updateProfileResponse.postValue(
//                                    Resource.error(
//                                        e.response.getError(), null
//                                    )
//                                )
//                            }
//                            else -> {
//                                updateProfileResponse.postValue(
//                                    Resource.error(
//                                        "Something went wrong",
//                                        null
//                                    )
//                                )
//                            }
//                        }
//                    }.collect { result ->
//                        updateProfileResponse.postValue(Resource.success(result))
//                    }
//                } else {
//                    val refreshTokenReqBody =
//                        RefreshTokenReqBody(AppPreference.read(AppPreference.REFRESH_TOKEN, "")!!)
//                    userAccessRepository.refreshAccessToken(refreshTokenReqBody).onStart {
//                        updateProfileResponse.postValue(Resource.loading(null))
//                    }.catch { e ->
//                        when (e) {
//                            is ClientRequestException -> {
//                                updateProfileResponse.postValue(
//                                    Resource.error(
//                                        e.response.getError(), null
//                                    )
//                                )
//                            }
//                            else -> {
//                                updateProfileResponse.postValue(
//                                    Resource.error(
//                                        "Something went wrong",
//                                        null
//                                    )
//                                )
//                            }
//                        }
//
//                    }.collect { response ->
//                        response.results?.let {
//                            AppPreference.write(
//                                AppPreference.ACCCESS_TOKEN,
//                                response.results.accessToken
//                            )
//                            val currentTime = System.currentTimeMillis()
//                            AppPreference.write(AppPreference.ACCCESS_TOKEN_VALID_FROM, currentTime)
//
//                            val authorization = "Bearer " + response.results.accessToken
//                            val updateProfileImageRequest = UpdateProfileImageRequest(
//                                AppPreference.read(
//                                    AppPreference.CUSTOMER_HASH,
//                                    ""
//                                )!!, AppPreference.read(AppPreference.CRM_ID, "") ?: "", imageBase64
//                            )
//                            accountsRepository.updateProfileImage(
//                                authorization, updateProfileImageRequest,isCVPProfilePresent
//                            ).onStart {
//                            }.catch { e ->
//                                when (e) {
//                                    is ClientRequestException -> {
//                                        updateProfileResponse.postValue(
//                                            Resource.error(
//                                                e.response.getError(), null
//                                            )
//                                        )
//                                    }
//                                    else -> {
//                                        updateProfileResponse.postValue(
//                                            Resource.error(
//                                                "Something went wrong",
//                                                null
//                                            )
//                                        )
//                                    }
//                                }
//
//                            }.collect { response ->
//                                updateProfileResponse.postValue(Resource.success(response))
//                            }
//                        } ?: kotlin.run {
//                            updateProfileResponse.postValue(
//                                Resource.error(
//                                    "Something went wrong",
//                                    null
//                                )
//                            )
//                        }
//                    }
//                }
//            } else {
//                updateProfileResponse.postValue(Resource.error("No internet connection", null))
//            }
//        }
//        return updateProfileResponse
//    }
//
//    fun deleteProfilePicture(isCVPProfilePresent : Boolean): MutableLiveData<Resource<UpdateDeleteProfileImgResponse>> {
//        val deleteProfileResponse = MutableLiveData<Resource<UpdateDeleteProfileImgResponse>>()
//        val accessTokenActive = (System.currentTimeMillis() -
//                AppPreference.read(AppPreference.ACCCESS_TOKEN_VALID_FROM, 0L)) < Constants.ACCESS_TOKEN_VALIDITY
//        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
//            deleteProfileResponse.postValue(Resource.error("Something went wrong", null))
//        }
//        viewModelScope.launch(exceptionHandler) {
//            if (networkHelper.isNetworkConnected()) {
//                if (accessTokenActive) {
//                    val authorization =
//                        "Bearer " + AppPreference.read(AppPreference.ACCCESS_TOKEN, "")
//                    val deleteProfileImgRequest = DeleteProfileImgRequest(
//                        AppPreference.read(
//                            AppPreference.CUSTOMER_HASH,
//                            ""
//                        )!!,
//                        AppPreference.read(AppPreference.CRM_ID, "") ?: "",
//                        AppPreference.read(AppPreference.PROFILE_IMAGE_URL, "") ?: ""
//                    )
//                    accountsRepository.deleteProfileImage(
//                        authorization, deleteProfileImgRequest,isCVPProfilePresent
//                    ).onStart {
//                        deleteProfileResponse.postValue(Resource.loading(null))
//                    }.catch { e ->
//                        when (e) {
//                            is ClientRequestException -> {
//                                deleteProfileResponse.postValue(
//                                    Resource.error(
//                                        e.response.getError(), null
//                                    )
//                                )
//                            }
//                            else -> {
//                                deleteProfileResponse.postValue(
//                                    Resource.error(
//                                        "Something went wrong",
//                                        null
//                                    )
//                                )
//                            }
//                        }
//                    }.collect { result ->
//                        deleteProfileResponse.postValue(Resource.success(result))
//                    }
//                } else {
//                    val refreshTokenReqBody =
//                        RefreshTokenReqBody(AppPreference.read(AppPreference.REFRESH_TOKEN, "")!!)
//                    userAccessRepository.refreshAccessToken(refreshTokenReqBody).onStart {
//                        deleteProfileResponse.postValue(Resource.loading(null))
//                    }.catch { e ->
//                        when (e) {
//                            is ClientRequestException -> {
//                                deleteProfileResponse.postValue(
//                                    Resource.error(
//                                        e.response.getError(), null
//                                    )
//                                )
//                            }
//                            else -> {
//                                deleteProfileResponse.postValue(
//                                    Resource.error(
//                                        "Something went wrong",
//                                        null
//                                    )
//                                )
//                            }
//                        }
//
//                    }.collect { response ->
//                        response.results?.let {
//                            AppPreference.write(
//                                AppPreference.ACCCESS_TOKEN,
//                                response.results.accessToken
//                            )
//                            val currentTime = System.currentTimeMillis()
//                            AppPreference.write(AppPreference.ACCCESS_TOKEN_VALID_FROM, currentTime)
//
//                            val authorization = "Bearer " + response.results.accessToken
//                            val deleteProfileImgRequest = DeleteProfileImgRequest(
//                                AppPreference.read(
//                                    AppPreference.CUSTOMER_HASH,
//                                    ""
//                                )!!,
//                                AppPreference.read(AppPreference.CRM_ID, "") ?: "",
//                                AppPreference.read(AppPreference.PROFILE_IMAGE_URL, "") ?: ""
//                            )
//                            accountsRepository.deleteProfileImage(
//                                authorization, deleteProfileImgRequest,isCVPProfilePresent
//                            ).onStart {
//                            }.catch { e ->
//                                when (e) {
//                                    is ClientRequestException -> {
//                                        deleteProfileResponse.postValue(
//                                            Resource.error(
//                                                e.response.getError(), null
//                                            )
//                                        )
//                                    }
//                                    else -> {
//                                        deleteProfileResponse.postValue(
//                                            Resource.error(
//                                                "Something went wrong",
//                                                null
//                                            )
//                                        )
//                                    }
//                                }
//
//                            }.collect { response ->
//                                deleteProfileResponse.postValue(Resource.success(response))
//                            }
//                        } ?: kotlin.run {
//                            deleteProfileResponse.postValue(
//                                Resource.error(
//                                    "Something went wrong",
//                                    null
//                                )
//                            )
//                        }
//                    }
//                }
//            } else {
//                deleteProfileResponse.postValue(Resource.error("No internet connection", null))
//            }
//        }
//        return deleteProfileResponse
//    }
//    fun checkCVPProfileIsAvailable(results: CustomerData?) : Boolean{
//        return !TextUtils.isEmpty(results?.brandData?.cvpProfile?.cvpProfileId ?: "")
//    }
}