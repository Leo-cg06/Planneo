package com.leo.planneo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Lugar(
    @SerialName("id") val id: Int = 0,
    @SerialName("nombre") val nombre: String = "",
    @SerialName("descripcion") val descripcion: String = "", // Sin ?
    @SerialName("tipo") val tipo: String = "",
    @SerialName("ubicacion_texto") val ubicacionTexto: String = "", // Sin ?
    @SerialName("latitud") val latitud: Double = 0.0,
    @SerialName("longitud") val longitud: Double = 0.0,
    @SerialName("foto_url") val fotoUrl: String = "", // Sin ?
    @SerialName("valoracion_media") val valoracionMedia: Float = 0f,
    @SerialName("created_at") val createdAt: String = "" // Sin ?
)

data class LugarConFavorito(
    val lugar: Lugar,
    val esFavorito: Boolean = false
)