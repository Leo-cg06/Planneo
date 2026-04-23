package com.maestre.planneo.model

import kotlinx.serialization.Serializable

@Serializable
//Para lugares que no son nodos
data class OverpassCenter(
    val lat: Double,
    val lon: Double
)