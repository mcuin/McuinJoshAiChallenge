package com.joshai.nasajoshaichallenge.dataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoData(
    @SerialName("data") val data: List<PhotoItem>,
    @SerialName("pagination") val pagination: Pagination)

@Serializable
data class PhotoItem(
    @SerialName("attributes") val attributes: PhotoAttributes
)

@Serializable
data class PhotoAttributes(
    @SerialName("images") val images: PhotoLinks? = null
)

@Serializable
data class PhotoLinks(
    @SerialName("full") val full: String? = null
)

@Serializable
data class Pagination(
    @SerialName("total_pages") val totalPages: Int)