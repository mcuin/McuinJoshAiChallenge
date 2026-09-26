package com.joshai.nasajoshaichallenge

import androidx.lifecycle.ViewModel
import com.joshai.nasajoshaichallenge.dataClasses.NASARoversListUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class NASARoversListViewModel @Inject constructor(private val repository: NASARoversListRepository) : ViewModel() {

    private val _roversUIState = MutableStateFlow(NASARoversListUIState())
    val roversUIState: StateFlow<NASARoversListUIState> = _roversUIState.asStateFlow()

    private val disposable: CompositeDisposable = CompositeDisposable()

    fun getRovers() {

        _roversUIState.update {
            it.copy(isLoading = true, errorMessage = null)
        }

        repository.fetchRovers()
            .subscribe({ rovers ->
                _roversUIState.update {
                    it.copy(isLoading = false, rovers = rovers)
                }
            }, { error ->
                _roversUIState.update {
                    it.copy(isLoading = false, errorMessage = error.message)
                }
            })

        disposable.add(disposable)
    }

    override fun onCleared() {
        disposable.clear()
    }
}