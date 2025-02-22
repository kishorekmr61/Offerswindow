package com.customer.offerswindow.di

import com.customer.offerswindow.BuildConfig
import com.customer.offerswindow.data.api.login.ApiServices.*
import com.customer.offerswindow.helper.AuthInterceptor
import com.customer.offerswindow.helper.AuthAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val CONNECT_TIMEOUT = 20L
    private const val READ_TIMEOUT = 60L
    private const val WRITE_TIMEOUT = 120L

    @Singleton
    @Provides
    fun provideHttpClient(authInterceptor: AuthInterceptor,
                          authAuthenticator: AuthAuthenticator
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(authAuthenticator)
            .build()

    }



    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }


    @Singleton
    @Provides
    fun provideDashBoarddata(retrofit: Retrofit): DashboardApiService =
        retrofit.create(DashboardApiService::class.java)


    @Singleton
    @Provides
    fun provideMasterdata(retrofit: Retrofit): MasterApiService =
        retrofit.create(MasterApiService::class.java)

    @Singleton
    @Provides
    fun provideCustomerApiServicedata(retrofit: Retrofit): CustomerApiService =
        retrofit.create(CustomerApiService::class.java)


//    @Singleton
//    @Provides
//    fun provideWalletApiServicedata(retrofit: Retrofit): WalletApiService =
//        retrofit.create(WalletApiService::class.java)
//
//    @Singleton
//    @Provides
//    fun provideStocksApiServicedata(retrofit: Retrofit): StocksApiService =
//        retrofit.create(StocksApiService::class.java)
//
//    @Singleton
//    @Provides
//    fun provideChallengesApiServicedata(retrofit: Retrofit): ChallengesApiService =
//        retrofit.create(ChallengesApiService::class.java)
//
//    @Singleton
//    @Provides
//    fun provideMembershipApiServicedata(retrofit: Retrofit): MemberShipApiService =
//        retrofit.create(MemberShipApiService::class.java)
//
//    @Singleton
//    @Provides
//    fun provideClosureGroupApiServicedata(retrofit: Retrofit): ClosureGroupsApiService =
//        retrofit.create(ClosureGroupsApiService::class.java)
//
//    @Singleton
//    @Provides
//    fun provideScannerApiServicedata(retrofit: Retrofit): ScannerApiService =
//        retrofit.create(ScannerApiService::class.java)

}