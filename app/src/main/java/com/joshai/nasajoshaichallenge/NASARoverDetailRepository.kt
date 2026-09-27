package com.joshai.nasajoshaichallenge

import com.joshai.nasajoshaichallenge.dataClasses.PhotoItem
import com.joshai.nasajoshaichallenge.networking.APIService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

        if (totalPages > 1) {
            val morePhotos = coroutineScope {
                (2..totalPages).map { page ->
                    async {
                        apiService.getPhotosDate(roverId, page, 100, selectedDated, selectedDated)
                    }
                }.awaitAll().flatMap { it.data }
            }
            photoData.addAll(morePhotos)
        }

        emit(photoData.toList())
    }
}