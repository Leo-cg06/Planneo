package com.maestre.planneo.model
import kotlinx.serialization.Serializable

@Serializable
data class OverpassResponse(
    val elements: List<OverpassElement>
)