package com.joshai.nasajoshaichallenge

import com.joshai.nasajoshaichallenge.dataclasses.RoverCard
import com.joshai.nasajoshaichallenge.dataclasses.RoverIds
import com.joshai.nasajoshaichallenge.networking.APIService
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.observable.ObservableFromIterable
import io.reactivex.rxjava3.schedulers.Schedulers
import jakarta.inject.Inject

class NASARoversListRepository @Inject constructor(private val apiService: APIService) {

    private fun fetchRoverIds(): Single<RoverIds> {
        return apiService.getRoverIds()
    }

    fun fetchRovers(): Single<List<RoverCard>> {
        return fetchRoverIds()
            .flatMapObservable { roverIds ->
                ObservableFromIterable.fromIterable(roverIds.data)
            }.flatMapSingle { roverId ->
                Single.zip(
                    apiService.getRover(roverId.id)
                        .subscribeOn(Schedulers.io()),
                    apiService.getPhotos(roverId.id, 1)
                        .subscribeOn(Schedulers.io())
                ) { roverData, photoData ->
                    RoverCard(
                        roverData.data.attributes,
                        roverData.data.relationships,
                        photoData.data[0].attributes.images
                    )
                }
            }
            .toList()
            .subscribeOn(Schedulers.io())
    }
}