package com.customer.offerswindow.helper

import android.util.Log
import com.customer.offerswindow.model.StockPurchsasePostingResponse
import com.customer.offerswindow.model.TokenResponse
 import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

abstract class BaseApiResponse {


    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiCall()
            Log.v("Request :", response.raw().request.url.toString())
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    Log.v("Response", response.body().toString())
                    return NetworkResult.Success(body)
                }
            }

                if (response.raw().request.url.toString().contains("/token")){
                    val mError =
                        Gson().fromJson(
                            response.errorBody()?.string(),
                            TokenResponse::class.java
                        )
                    val datamesage = mError.error_description
                    return error(datamesage ?:"Oops! Something went wrong.")
                }else {
                    when (response.code()) {
                    in (400..499) -> {
                        return try {
                            val mError =
                                Gson().fromJson(
                                    response.errorBody()?.string(),
                                    StockPurchsasePostingResponse::class.java
                                )
                            val datamesage = mError.Message
                            error(datamesage)
                        } catch (e: IOException) {
                            error("Oops! Something went wrong.")
                        }
                    }

                    in (500..600) -> {
                        return error("Uh-oh! Something's not quite right. We're sorting it out now! Thanks for your understanding!")
                    }
                }
            }
            return error("Oops! Something went wrong.")
        } catch (e: Exception) {
            return error("Oops! Something went wrong.")
        }
    }

    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error(errorMessage)

}