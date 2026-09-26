package com.joshai.nasajoshaichallenge.networking

import com.joshai.nasajoshaichallenge.dataclasses.PhotoData
import com.joshai.nasajoshaichallenge.dataclasses.Rover
import com.joshai.nasajoshaichallenge.dataclasses.RoverData
import com.joshai.nasajoshaichallenge.dataclasses.RoverIds
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {

    @GET("api/v2/rovers")
    fun getRoverIds(): Single<RoverIds>

    @GET("api/v2/rovers/{id}")
    fun getRover(@Path("id") id: String): Single<RoverData>

    @GET("api/v2/photos")
    fun getPhotos(@Query("rovers") rovers: String, @Query("perPage") perPage: Int) : Single<PhotoData>
}