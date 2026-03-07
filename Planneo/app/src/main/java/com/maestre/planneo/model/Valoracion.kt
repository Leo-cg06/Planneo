package com.maestre.planneo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Valoracion(
    @SerialName("id") val id: Int = 0,
    @SerialName("lugar_id") val lugarId: Int = 0,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("nombre_usuario") val nombreUsuario: String = "",
    @SerialName("puntuacion") val puntuacion: Int = 0, // 1-5
    @SerialName("comentario") val comentario: String = "",
    @SerialName("created_at") val createdAt: String = ""
)