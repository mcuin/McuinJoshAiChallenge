package com.joshai.nasajoshaichallenge

import com.joshai.nasajoshaichallenge.dataClasses.*
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class NASARoversListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository: NASARoversListRepository = mock()
    private lateinit var viewModel: NASARoversListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        viewModel = NASARoversListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        RxJavaPlugins.reset()
    }

    @Test
    fun `getRovers updates uiState with rovers list and formats dates on success`() {
        // Given
        val fullRoverData = FullRoverData(
            attributes = Attributes(
                name = "Curiosity",
                landingDate = "2012-08-06",
                launchDate = "2011-11-26",
                status = "active",
                totalPhotos = 100,
                maxDate = "2023-10-20"
            ),
            relationships = Relationships(cameras = emptyList()),
            photoData = PhotoLinks(full = "https://example.com/photo.jpg")
        )

        whenever(repository.fetchRovers()).thenReturn(Single.just(listOf(fullRoverData)))

        // When
        viewModel.getRovers()

        // Then
        val uiState = viewModel.roversUIState.value
        assertFalse(uiState.isLoading)
        assertNull(uiState.errorMessage)
        assertEquals(1, uiState.rovers.size)
        
        val rover = uiState.rovers[0]
        assertEquals("Curiosity", rover.attributes.name)
        assertEquals("11/26/2011", rover.attributes.launchDate)
        assertEquals("08/06/2012", rover.attributes.landingDate)
    }

    @Test
    fun `getRovers updates uiState with error message on failure`() {
        // Given
        whenever(repository.fetchRovers()).thenReturn(Single.error(RuntimeException("Network error")))

        // When
        viewModel.getRovers()

        // Then
        val uiState = viewModel.roversUIState.value
        assertFalse(uiState.isLoading)
        assertEquals("Network error", uiState.errorMessage)
        assertTrue(uiState.rovers.isEmpty())
    }
}
