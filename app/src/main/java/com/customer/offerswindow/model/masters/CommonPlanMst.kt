package com.customer.offerswindow.model.masters

import android.graphics.Color
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.utils.resource.WidgetViewModel

data class CommonPlanMst(
    var Status: Int,
    val Message: String,
    var Data: ArrayList<PlanDataResponse>
)

class PlanDataResponse(
    var Plan_Code: String = "",
    var Plan_Name: String = "",
    var Plan_Category: String = "",
    var No_Of_Visits: String = "",
    var Validity_Period: String = "",
    var Plan_Amount: String = "",
    var Hex_Colour_Code: String? = "000000",
    var Plan_Status: String = "",
    var Club_Visits: String = "",
    var Hub_Visits: String = "",
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.myplans_row
    }

    fun planImages(flag: String): Int {
        try {
            return Color.parseColor("#".plus(flag))
        } catch (e: Exception) {
             return Color.parseColor("#EFEFEF")
        }
    }

    fun planDividerImages(flag: String): Int {
        when (flag) {
            "NMS" -> {
                return R.drawable.ic_bronze_underline
            }
            "UMS" -> {
                return R.drawable.ic_bronze_underline
            }
            "Basic" -> {
                return R.drawable.ic_silver_underline
            }
            "Basic +" -> {
                return R.drawable.ic_bronze_underline
            }
            "Advanced" -> {
                return R.drawable.ic_bronze_underline
            }
            "Advanced +" -> {
                return R.drawable.gold_underline
            }
            "Ultimate" -> {
                return R.drawable.gold_underline
            }
            "Ultimate +" -> {
                return R.drawable.gold_underline
            }
            "UMS-15" -> {
                return R.drawable.ic_bronze_underline
            }
            "VMS-SILVER" -> {
                return R.drawable.ic_silver_underline
            }
            "VMS -GOLD " -> {
                return R.drawable.gold_underline
            }
        }
        return R.drawable.ic_silver_underline
    }

    fun plandurationtext(flag: String): Int {
        when (flag) {
            "NMS" -> {
                return Color.parseColor("#E8B63A")
            }
            "UMS" -> {
                return Color.parseColor("#E88D69")
            }
            "Basic" -> {
                return Color.parseColor("#C7D1E0")
            }
            "Basic +" -> {
                return Color.parseColor("#E88D69")
            }
            "Advanced" -> {
                return Color.parseColor("#E88D69")
            }
            "Advanced +" -> {
                return Color.parseColor("#E8B63A")
            }
            "Ultimate" -> {
                return Color.parseColor("#E8B63A")
            }
            "Ultimate +" -> {
                return Color.parseColor("#E8B63A")
            }
            "UMS-15" -> {
                return Color.parseColor("#E88D69")
            }
            "VMS-SILVER" -> {
                return Color.parseColor("#C7D1E0")
            }
            "VMS -GOLD " -> {
                return Color.parseColor("#E8B63A")
            }
        }
        return Color.parseColor("#C7D1E0")
    }

    fun validatePlan(): Boolean {
        if (Plan_Code == AppPreference.read(Constants.PLANCODE, "") ?: "") {
            return true
        }
        return false
    }
}
