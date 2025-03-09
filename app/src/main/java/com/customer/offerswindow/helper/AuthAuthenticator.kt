package com.customer.offerswindow.helper

import com.customer.offerswindow.BuildConfig
import com.customer.offerswindow.data.api.login.apiServices.MasterApiService
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.model.TokenResponse
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class AuthAuthenticator @Inject constructor() : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val token = runBlocking {
            AppPreference.read(Constants.TOKEN, "")
        }

        return runBlocking {
            val newToken = getNewToken(token)

            if (newToken?.isSuccessful == true || newToken?.body() != null) {
                AppPreference.write(Constants.TOKEN, newToken.body()?.access_token ?: "")
            }

            newToken?.body()?.let {
                AppPreference.write(Constants.TOKEN, it.access_token ?: "")
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${it.access_token}")
                    .build()
            }
        }
    }

    private suspend fun getNewToken(refreshToken: String?): retrofit2.Response<TokenResponse>? {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val service = retrofit.create(MasterApiService::class.java)

        return AppPreference.read(Constants.LOGINUSERNAME, "")
            ?.let {
                AppPreference.read(Constants.LOGINPASSWORD, "")
                    ?.let { it1 -> service.getToken("8374810383", "Welcome") }
            }
    }
}