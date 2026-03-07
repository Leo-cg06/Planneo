package com.leo.trailov2.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Evento(
    @SerialName("id") val id: Int = 0,
    @SerialName("lugar_id") val lugarId: Int = 0,
    @SerialName("nombre") val nombre: String = "",
    @SerialName("descripcion") val descripcion: String = "",
    @SerialName("fecha_inicio") val fechaInicio: String = "",
    @SerialName("fecha_fin") val fechaFin: String? = null,
    @SerialName("tipo_evento") val tipoEvento: String? = null,
    @SerialName("foto_url") val fotoUrl: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("created_at") val createdAt: String = ""
)