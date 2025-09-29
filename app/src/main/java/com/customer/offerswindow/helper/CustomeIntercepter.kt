package com.customer.offerswindow.helper

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class CustomeIntercepter @Inject constructor(
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val response = chain.proceed(chain.request())
            return response
        } catch (ex: Exception) {
            Log.e("API_ERROR", "Network error: " + ex.message)
            throw ex
        }
    }

}

