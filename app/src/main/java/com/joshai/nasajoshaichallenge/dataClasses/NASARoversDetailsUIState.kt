package com.joshai.nasajoshaichallenge.dataClasses

data class NASARoversDetailsUIState (
    val isLoading: Boolean = false,
    val roverErrorMessage: Int? = null,
    val roverDetails: Rover? = null,
    val roverPhotos: List<PhotoLinks> = emptyList(),
    val photoErrorMessage: Int? = null,
    val startDate: String = "",
    val selectedDateMillis: Long? = null,
    val minEpoch: Long = 0L,
    val maxEpoch: Long = 0L,
)