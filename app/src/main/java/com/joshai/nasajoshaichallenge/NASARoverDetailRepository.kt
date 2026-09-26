package com.joshai.nasajoshaichallenge

import com.joshai.nasajoshaichallenge.dataClasses.Rover
import com.joshai.nasajoshaichallenge.dataClasses.RoverData
import com.joshai.nasajoshaichallenge.networking.APIService
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.rx3.await
import javax.inject.Inject

class NASARoverDetailRepository @Inject constructor(private val apiService: APIService) {

    fun fetchRoverDetails(roverId: String) = flow {
        val roverData = apiService.getRover(roverId).await()
        emit(roverData)
    }

    fun fetchRoverPhotos(roverId: String) = flow {
        val roverPhotos = apiService.getPhotos(roverId, 20).await()
        emit(roverPhotos)
    }
}