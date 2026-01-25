package com.leo.trailov2.model

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Valoracion(
    @SerialName("id") val id: Int = 0,
    @SerialName("tipo") val tipo: String = "",
    @SerialName("id_referencia") val idReferencia: Int = 0,
    @SerialName("nombre_usuario") val nombreUsuario: String = "",
    @SerialName("calificacion") val calificacion: Float = 0f,
    @SerialName("comentario") val comentario: String = "",
    @SerialName("fecha") val fecha: Long = System.currentTimeMillis()
)