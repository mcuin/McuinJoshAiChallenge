package com.joshai.nasajoshaichallenge.dataClasses

data class NASARoversListUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val rovers: List<FullRoverData> = emptyList()
)