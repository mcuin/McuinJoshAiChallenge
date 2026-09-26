package com.joshai.nasajoshaichallenge.dataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FullRoverData(
    var attributes: Attributes,
    var relationships: Relationships,
    var photoData: PhotoLinks?
)

@Serializable
data class RoverIds(
    @SerialName("data") val data: List<RoverId>
)

@Serializable
data class RoverId(
    @SerialName("id") val id: String,
)

@Serializable
data class RoverData(
    @SerialName("data") val data: Rover
)

@Serializable
data class Rover(
    @SerialName("attributes") val attributes: Attributes,
    @SerialName("relationships") val relationships: Relationships)

@Serializable
data class Attributes(
    @SerialName("name") val name: String,
    @SerialName("landing_date") val landingDate: String,
    @SerialName("launch_date") val launchDate: String,
    @SerialName("status") val status: String,
    @SerialName("total_photos") val totalPhotos: Int)

@Serializable
data class Relationships(
    @SerialName("cameras") val cameras: List<Camera>)

@Serializable
data class Camera(
    @SerialName("id") val id: String,
    @SerialName("attributes") val attributes: CameraAttributes)

@Serializable
data class CameraAttributes(
    @SerialName("full_name") val fullName: String)