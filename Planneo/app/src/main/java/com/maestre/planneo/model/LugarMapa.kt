package com.maestre.planneo.model

data class LugarMapa(
    val id: Long,
    val nombre: String,
    val categoria: String,
    val latitud: Double,
    val longitud: Double
)