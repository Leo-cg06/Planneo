package com.leo.trailov2.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Favorito(
    @SerialName("id") val id: Int = 0,
    @SerialName("user_id") val userId: String = "",
    @SerialName("lugar_id") val lugarId: Int = 0, // ✅ Usa lugar_id
    @SerialName("created_at") val createdAt: String = ""
)