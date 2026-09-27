package com.joshai.nasajoshaichallenge

import com.joshai.nasajoshaichallenge.dataClasses.*
import com.joshai.nasajoshaichallenge.networking.APIService
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class NASARoversListRepositoryTest {

    private val apiService: APIService = mock()
    private lateinit var repository: NASARoversListRepository

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        repository = NASARoversListRepository(apiService)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
    }

    @Test
    fun `fetchRovers returns list of full rover data successfully`() {
        // Given
        val roverIds = RoverIds(data = listOf(RoverId(id = "curiosity")))
        val roverData = RoverData(
            data = Rover(
                attributes = Attributes(
                    name = "Curiosity",
                    landingDate = "2012-08-06",
                    launchDate = "2011-11-26",
                    status = "active",
                    totalPhotos = 100,
                    maxDate = "2023-10-20"
                ),
                relationships = Relationships(cameras = emptyList())
            )
        )
        val photoData = PhotoData(
            data = listOf(
                PhotoItem(
                    attributes = PhotoAttributes(
                        images = PhotoLinks(full = "https://example.com/photo.jpg")
                    )
                )
            ),
            pagination = Pagination(totalPages = 1)
        )

        whenever(apiService.getRoverIds()).thenReturn(Single.just(roverIds))
        whenever(apiService.getRover("curiosity")).thenReturn(Single.just(roverData))
        whenever(apiService.getPhoto("curiosity", 1)).thenReturn(Single.just(photoData))

        // When
        val testObserver = repository.fetchRovers().test()

        // Then
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        
        val result = testObserver.values()[0]
        assertEquals(1, result.size)
        assertEquals("Curiosity", result[0].attributes.name)
        assertEquals("https://example.com/photo.jpg", result[0].photoData?.full)
    }
}
