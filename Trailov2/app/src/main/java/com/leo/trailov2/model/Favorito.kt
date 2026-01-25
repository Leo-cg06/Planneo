package com.leo.trailov2.model

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Favorito(
    @SerialName("id") val id: Int = 0,
    @SerialName("user_id") val userId: String = "",
    @SerialName("tipo") val tipo: String = "",
    @SerialName("item_id") val itemId: Int = 0
)

@Serializable
data class FavoritoInsert(
    @SerialName("user_id") val userId: String,
    @SerialName("tipo") val tipo: String,
    @SerialName("item_id") val itemId: Int
)