package com.joshai.nasajoshaichallenge.networking

import com.joshai.nasajoshaichallenge.dataClasses.PhotoData
import com.joshai.nasajoshaichallenge.dataClasses.RoverData
import com.joshai.nasajoshaichallenge.dataClasses.RoverIds
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {

    @GET("api/v2/rovers")
    fun getRoverIds(): Single<RoverIds>

    @GET("api/v2/rovers/{id}")
    fun getRover(@Path("id") id: String): Single<RoverData>

    @GET("api/v2/photos")
    fun getPhoto(@Query("rovers") rovers: String, @Query("perPage") perPage: Int) : Single<PhotoData>

    @GET("api/v2/photos")
    suspend fun getPhotosDate(@Query("rovers") rovers: String, @Query("page") page: Int, @Query("per_page") perPage: Int, @Query("date_min") minDate: String, @Query("date_max") maxDate: String) : PhotoData
}