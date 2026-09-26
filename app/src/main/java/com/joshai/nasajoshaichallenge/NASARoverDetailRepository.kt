package com.joshai.nasajoshaichallenge

import com.joshai.nasajoshaichallenge.dataClasses.PhotoData
import com.joshai.nasajoshaichallenge.dataClasses.PhotoItem
import com.joshai.nasajoshaichallenge.networking.APIService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.rx3.await
import javax.inject.Inject

class NASARoverDetailRepository @Inject constructor(private val apiService: APIService) {

    fun fetchRoverDetails(roverId: String) = flow {
        val roverData = apiService.getRover(roverId).await()
        emit(roverData)
    }

    fun fetchRoverPhotos(roverId: String, selectedDated: String): Flow<List<PhotoItem>> = flow {
        val pagesRequest = apiService.getPhotosDate(roverId, 1, 100, selectedDated, selectedDated)
        val totalPages = pagesRequest.pagination.totalPages
        val photoData = pagesRequest.data.toMutableList()
        for (page in 2..totalPages) {
            val photosResponse = apiService.getPhotosDate(roverId, page, 100, selectedDated, selectedDated)
            photoData.addAll(photosResponse.data)
        }
        emit(photoData.toList())
    }
}