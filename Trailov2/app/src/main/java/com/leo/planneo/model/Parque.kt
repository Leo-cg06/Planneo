package com.leo.planneo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Parque(
    @SerialName("id") val id: Int = 0,
    @SerialName("nombre") val nombre: String = "",
    @SerialName("informacion") val informacion: String = "",
    @SerialName("valoracion") val valoracion: Float = 0f,
    @SerialName("ubicacion") val ubicacion: String = "",
    @SerialName("foto_url") val fotoUrl: String = "",
    @SerialName("fauna") val fauna: String = "",
    @SerialName("flora") val flora: String = "",
    @SerialName("extension") val extension: String = "",
    @SerialName("latitud") val latitud: Double = 0.0,
    @SerialName("longitud") val longitud: Double = 0.0
)

//Para que se pueda saber si es favorito o no
data class ParqueConFavorito(
    val parque: Parque,
    val esFavorito: Boolean = false
)