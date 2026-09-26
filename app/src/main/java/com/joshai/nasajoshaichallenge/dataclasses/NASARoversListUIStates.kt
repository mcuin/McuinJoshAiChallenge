package com.joshai.nasajoshaichallenge.dataclasses

data class NASARoversListUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val rovers: List<RoverCard> = emptyList()
)