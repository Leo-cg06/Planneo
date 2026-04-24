package com.maestre.planneo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("email")
    val email: String,
    @SerialName("contraseña")
    val contrasena: Long

)