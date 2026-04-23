package com.maestre.planneo.model
import kotlinx.serialization.Serializable

@Serializable
data class OverpassElement(
    val id: Long,
    val lat: Double? = null,
    val lon: Double? = null,
    val center: OverpassCenter? = null,
    val tags: Map<String, String>? = null
)