package com.maestre.planneo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Perfil(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("nombre")
    val nombre: String? = null,
    @SerialName("apellido")
    val apellido: String? = null,
    @SerialName("edad")
    val edad: Int? = null,
    @SerialName("icono")
    val icono: String? = null,
    @SerialName("preferencias")
    val preferencias: List<String> = emptyList()
)