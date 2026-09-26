package com.joshai.nasajoshaichallenge.dataClasses

import androidx.compose.runtime.mutableStateListOf

data class NASARoversDetailsUIState (
    var isLoading: Boolean = false,
    var errorMessage: String? = null,
    var roverDetails: Rover? = null,
    var roverPhotos: List<PhotoLinks> = emptyList(),
)