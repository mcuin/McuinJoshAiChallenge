package com.joshai.nasajoshaichallenge

import androidx.lifecycle.ViewModel
import com.joshai.nasajoshaichallenge.dataClasses.NASARoversListUIState
import com.joshai.nasajoshaichallenge.utils.DateFormatters
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NASARoversListViewModel @Inject constructor(private val repository: NASARoversListRepository) : ViewModel() {

    private val _roversUIState = MutableStateFlow(NASARoversListUIState())
    val roversUIState: StateFlow<NASARoversListUIState> = _roversUIState.asStateFlow()

    private val disposable: CompositeDisposable = CompositeDisposable()

    fun getRovers() {

        _roversUIState.update {
            it.copy(isLoading = true, errorMessage = null)
        }

        disposable.add(repository.fetchRovers()
            .map { rovers ->
                rovers.map { roverData ->
                    val attributes = roverData.attributes

                    val formattedLaunchDate = attributes.launchDate.let { apiString ->
                        DateFormatters.apiFormatter.parse(apiString)?.let { apiDate ->
                            DateFormatters.selectedFormatter.format(apiDate)
                        }
                    }

                    val formattedLandingDate = attributes.landingDate.let { apiString ->
                        DateFormatters.apiFormatter.parse(apiString)?.let { apiDate ->
                            DateFormatters.selectedFormatter.format(apiDate)
                        }
                    }

                    roverData.copy(
                        attributes = attributes.copy(
                            launchDate = formattedLaunchDate ?: attributes.launchDate,
                            landingDate = formattedLandingDate ?: attributes.landingDate
                        )
                    )
                }
            }.subscribe({ rovers ->
                _roversUIState.update {
                    it.copy(isLoading = false, rovers = rovers)
                }
            }, { error ->
                _roversUIState.update {
                    it.copy(isLoading = false, errorMessage = error.message)
                }
            })
        )
    }

    override fun onCleared() {
        disposable.dispose()
    }
}