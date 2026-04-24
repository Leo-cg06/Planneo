package com.maestre.planneo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Empresa(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("email")
    val email: String,

    @SerialName("contrasena")
    val contrasena: Long,

    @SerialName("cif")
    val cif: String,

    @SerialName("web")
    val web: String
)