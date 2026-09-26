package com.joshai.nasajoshaichallenge

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.joshai.nasajoshaichallenge.dataClasses.NASARoversDetailsUIState
import com.joshai.nasajoshaichallenge.dataClasses.Rover
import com.joshai.nasajoshaichallenge.dataClasses.RoverData
import com.joshai.nasajoshaichallenge.dataClasses.RoverDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class NASARoverDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val nasaRoverDetailRepository: NASARoverDetailRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<RoverDetailRoute>()
    private val selectedDate = MutableStateFlow<String?>(null)

    private val roverDetailsFlow = nasaRoverDetailRepository.fetchRoverDetails(route.roverId)
        .catch { emit(RoverData(null)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _uiState = roverDetailsFlow.filterNotNull()
        .flatMapConcat { roverData ->
            if (roverData.data == null) {
                return@flatMapConcat flowOf(NASARoversDetailsUIState(roverErrorMessage = "Rover not found"))
            }

            if (selectedDate.value == null) {
                selectedDate.update {
                    roverData.data.attributes.maxDate
                }
            }

            selectedDate.filterNotNull().flatMapConcat { selectedDate ->
                nasaRoverDetailRepository.fetchRoverPhotos(
                    route.roverId,
                    selectedDate
                )
                    .catch { emit(listOf()) }
                    .map { roverPhotos ->

                        when {

                            roverPhotos.isEmpty() -> {
                                return@map NASARoversDetailsUIState(photoErrorMessage = "No photos found")
                            }

                            else -> {
                                NASARoversDetailsUIState(
                                    roverDetails = Rover(
                                        roverData.data.attributes,
                                        roverData.data.relationships
                                    ),
                                    roverPhotos = roverPhotos.mapNotNull { photoItem ->
                                        photoItem.attributes.images
                                    }
                                )
                            }
                        }
                    }.catch {
                        emit(NASARoversDetailsUIState(roverErrorMessage = it.message))
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NASARoversDetailsUIState(isLoading = true)
        )
    val uiState = _uiState

    fun updateDateGetPhotos(date: String) {
        if (date != selectedDate.value) {
            selectedDate.update {
                date
            }
        }
    }
}