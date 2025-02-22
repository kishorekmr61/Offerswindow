package com.customer.offerswindow.model.customersdata

import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.utils.getDateTime

data class CustomerCheckUpRequest(
    var CustomerUID: String = "",
    var Weight: String = "",
    var BodyFat: String = "",
    var VisceralFat: String = "",
    var MetabolicAge: String = "",
    var BMI: String = "",
    var MuscleMass: String = "",
    var CreatedBy: String = "",
    var CreatedDateTime: String = "",
    var UpdatedBy: String = "",
    var UpdatedDateTime: String = ""
)

data class ProfileUpdateRequest(
    var CustomerUID: String = "",
    var CustomerName: String? = "",
    var CustomerCategory: String? = "",
    var SurName: String? = "",
    var Mobile_No: String? = "",
    var DoB: String = "",
    var EmailID: String = "",
    var MarriageAnniversaryDate: String? = "",
    var FitnessGoal: String? = "",
    var CustomerHeight: String? = "",
    var CustomerWeight: String? = "",
    var CustomerPhotoFilePath: String = "",

    @Transient
    var CustomerLocation: String = "",
    @Transient
    var CustomerImageUrl: String = "",
    @Transient
    var Coachname: String = "",
    @Transient
    var CoachMobileno: String = "",
    var Gender: String = "",
    var MaritalStatus: String = "",
    var CoachUserID: String = "",
    var CreatedBy: String = AppPreference.read(Constants.USERUID, "") ?: "",
    var CreatedDateTime: String = getDateTime(),
    var UpdatedBy: String = AppPreference.read(Constants.USERUID, "") ?: "",
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


data class FamilyMemberPostRequest(
    var RecordID: String = "",
    var CustomerUID: String? = "",
    var RelationshipType: String? = "",
    var Name: String? = "",
    var Age: String? = "",
    var Remarks: String? = "",
    var Diabetic: String? = "N",
    var Obesity: String? = "N",
    var Thyroid: String? = "N",
    var BP: String? = "N",
    var Others: String? = "N",
    var CreatedBy: String? = "",
    var CreatedDateTime: String? = "",
    var UpdatedBy: String? = "",
    var UpdatedDateTime: String? = ""
)