package com.customer.offerswindow.model.dashboard

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DashBoardDataResponse(

    @Expose
    @SerializedName("Status")
    var Status: Int,
    @Expose
    @SerializedName("Message")
    val Message: String,
    @Expose
    @SerializedName("Data")
    var Data: Data
)

data class Data(
    @Expose
    @SerializedName("Table")
    var Table: List<Table>,
    @Expose
    @SerializedName("Table1")
    var Table1: List<Table1>
)

data class Table1(
    @Expose
    @SerializedName("Transaction_Type")
    val Transaction_Type: String,
    @Expose
    @SerializedName("Lead_Status_Desc")
    val Lead_Status_Desc: String,
    @Expose
    @SerializedName("Records_Count")
    var Records_Count: Int
)

data class Table(
    @Expose
    @SerializedName("Transaction_Type")
    val Transaction_Type: String,
    @Expose
    @SerializedName("Cust_Status")
    val Cust_Status: String,
    @Expose
    @SerializedName("Records_Count")
    var Records_Count: Int
)
