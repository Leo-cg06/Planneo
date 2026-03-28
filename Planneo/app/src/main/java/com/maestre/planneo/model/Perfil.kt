package com.maestre.planneo.model

import kotlinx.serialization.Serializable

@Serializable
data class Perfil(
    val id: String = "",
    val email: String? = null,
    val nombre: String? = null,
    val apellido: String? = null,
    val edad: Int? = null,
    val icono: String? = null,
    val preferencias: List<String> = emptyList()
)