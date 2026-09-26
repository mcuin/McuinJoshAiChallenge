package com.joshai.nasajoshaichallenge

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.joshai.nasajoshaichallenge.dataClasses.FullRoverData
import com.joshai.nasajoshaichallenge.dataClasses.NASARoversDetailsUIState
import com.joshai.nasajoshaichallenge.dataClasses.PhotoAttributes
import com.joshai.nasajoshaichallenge.dataClasses.PhotoLinks
import com.joshai.nasajoshaichallenge.dataClasses.Rover
import com.joshai.nasajoshaichallenge.dataClasses.RoverDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class NASARoverDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val nasaRoverDetailRepository: NASARoverDetailRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<RoverDetailRoute>()
    private val addPhotos = MutableStateFlow(listOf<PhotoLinks>())

    private val _uiState = nasaRoverDetailRepository.fetchRoverDetails(route.roverId)
        .combine(
            nasaRoverDetailRepository.fetchRoverPhotos(route.roverId)
        ) { roverData, roverPhotos ->

            NASARoversDetailsUIState(
                roverDetails = Rover(roverData.data.attributes, roverData.data.relationships),
                roverPhotos = roverPhotos.data.mapNotNull { it.attributes.images }
            )
        }.catch {
            emit(NASARoversDetailsUIState(errorMessage = it.message))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NASARoversDetailsUIState()
        )
    val uiState = _uiState

    fun getMorePhotos() {
        nasaRoverDetailRepository.fetchRoverPhotos(route.roverId).mapNotNull { photoData ->
            val currentPhotos = _uiState.value.roverPhotos
            val allPhotos = currentPhotos + photoData.data.mapNotNull { it.attributes.images }
            _uiState.value.roverPhotos = allPhotos
        }
    }
}