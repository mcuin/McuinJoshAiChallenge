package com.joshai.nasajoshaichallenge

import com.joshai.nasajoshaichallenge.dataClasses.*
import com.joshai.nasajoshaichallenge.networking.APIService
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class NASARoverDetailRepositoryTest {

    private val apiService: APIService = mock()
    private val repository = NASARoverDetailRepository(apiService)

    @Test
    fun `fetchRoverDetails returns rover data correctly`() = runTest {
        // Given
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
        whenever(apiService.getRover("curiosity")).thenReturn(Single.just(roverData))

        // When
        val result = repository.fetchRoverDetails("curiosity").first()

        // Then
        assertEquals("Curiosity", result.data?.attributes?.name)
    }

    @Test
    fun `fetchRoverPhotos returns list of photos correctly`() = runTest {
        // Given
        val photoData = PhotoData(
            data = listOf(
                PhotoItem(
                    attributes = PhotoAttributes(
                        images = PhotoLinks(full = "https://example.com/photo_detail.jpg")
                    )
                )
            ),
            pagination = Pagination(totalPages = 1)
        )
        whenever(apiService.getPhotosDate("curiosity", 1, 100, "2023-10-20", "2023-10-20")).thenReturn(photoData)

        // When
        val result = repository.fetchRoverPhotos("curiosity", "2023-10-20").first()

        // Then
        assertEquals(1, result.size)
        assertEquals("https://example.com/photo_detail.jpg", result[0].attributes.images?.full)
    }
}
