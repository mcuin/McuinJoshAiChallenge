package com.joshai.nasajoshaichallenge.dataClasses

import kotlinx.serialization.Serializable

@Serializable
object RoversListRoute

@Serializable
data class RoverDetailRoute (
    val roverId: String
)