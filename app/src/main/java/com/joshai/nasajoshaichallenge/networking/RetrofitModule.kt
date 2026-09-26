package com.joshai.nasajoshaichallenge.networking

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private val jsonConverter = Json { ignoreUnknownKeys = true }
    private val contentType = "application/json".toMediaType()
    val headerInterceptor = Interceptor { chain ->

        val request = chain.request()
        val headers = request.newBuilder()
            .header("X-Api-Key", "mv_live_1a35f31e060515e78326559132ecc06deeb9b5e5")
            .build()

        chain.proceed(headers)
    }

    @Provides
    fun provideBaseUrl(): String = "https://api.marsvista.dev/"

    @Provides
    @Singleton
    fun provideRetrofit(BASE_URL: String): Retrofit = Retrofit.Builder()
        .addConverterFactory(jsonConverter.asConverterFactory(contentType))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .baseUrl(BASE_URL)
        .client(okhttp3.OkHttpClient.Builder().addInterceptor(headerInterceptor).build())
        .build()

    @Provides
    @Singleton
    fun providesAPIService(retrofit: Retrofit): APIService =
        retrofit.create(APIService::class.java)
}