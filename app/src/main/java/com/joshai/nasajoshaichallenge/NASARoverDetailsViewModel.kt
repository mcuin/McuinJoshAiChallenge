package com.joshai.nasajoshaichallenge

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.joshai.nasajoshaichallenge.dataClasses.NASARoversDetailsUIState
import com.joshai.nasajoshaichallenge.dataClasses.Rover
import com.joshai.nasajoshaichallenge.dataClasses.RoverData
import com.joshai.nasajoshaichallenge.dataClasses.RoverDetailRoute
import com.joshai.nasajoshaichallenge.utils.DateFormatters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import javax.inject.Inject

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
                return@flatMapConcat flowOf(NASARoversDetailsUIState(roverErrorMessage = R.string.rover_not_found))
            }

            val minEpoch = LocalDate.parse(roverData.data.attributes.landingDate, DateFormatters.epochFormatter)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
            val maxEpoch = LocalDate.parse(roverData.data.attributes.maxDate, DateFormatters.epochFormatter)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()

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
                        val date = selectedDate.let { DateFormatters.apiFormatter.parse(it) }
                        when {

                            roverPhotos.isEmpty() -> {
                                return@map NASARoversDetailsUIState(photoErrorMessage = R.string.no_photos_found)
                            }

                            else -> {

                                val launchDate = DateFormatters.apiFormatter.parse(roverData.data.attributes.launchDate)
                                val landingDate = DateFormatters.apiFormatter.parse(roverData.data.attributes.landingDate)

                                NASARoversDetailsUIState(
                                    roverDetails = Rover(
                                        roverData.data.attributes.copy(
                                            landingDate = DateFormatters.selectedFormatter.format(landingDate ?: roverData.data.attributes.landingDate),
                                            launchDate = DateFormatters.selectedFormatter.format(launchDate ?: roverData.data.attributes.launchDate)),
                                        roverData.data.relationships
                                    ),
                                    roverPhotos = roverPhotos.mapNotNull { photoItem ->
                                        photoItem.attributes.images
                                    },
                                    startDate = date.let { DateFormatters.selectedFormatter.format(it ?: selectedDate) },
                                    selectedDateMillis = date?.time,
                                    minEpoch = minEpoch,
                                    maxEpoch = maxEpoch,
                                )
                            }
                        }
                    }.catch {
                        emit(NASARoversDetailsUIState(roverErrorMessage = R.string.rover_not_found))
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NASARoversDetailsUIState(isLoading = true)
        )
    val uiState = _uiState

    fun updateDateGetPhotos(date: Long) {
        val dateString = DateFormatters.apiFormatter.format(Date(date))
        selectedDate.update {
            dateString
        }
    }
}