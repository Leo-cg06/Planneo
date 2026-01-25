package com.leo.trailov2.model

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Actividad(
    @SerialName("id") val id: Int = 0,
    @SerialName("nombre") val nombre: String = "",
    @SerialName("informacion") val informacion: String = "",
    @SerialName("duracion") val duracion: String = "",
    @SerialName("dificultad") val dificultad: String = "",
    @SerialName("km") val km: Double = 0.0,
    @SerialName("ubicacion") val ubicacion: String = "",
    @SerialName("valoracion") val valoracion: Double = 0.0,
    @SerialName("foto_url") val fotoUrl: String = "",
    @SerialName("latitud") val latitud: Double = 0.0,
    @SerialName("longitud") val longitud: Double = 0.0
)

//Para que se pueda saber si es favorito o no
data class ActividadConFavorito(
    val actividad: Actividad,
    val esFavorito: Boolean = false
)