package com.maestre.planneo.bd

import android.util.Log
import com.maestre.planneo.model.Favorito
import io.github.jan.supabase.postgrest.from

object FavoritoRepository {

    private const val TABLA = "favoritos"

    // Obtener todos los favoritos de un usuario
    suspend fun getFavoritosByUser(userId: String): List<Favorito> {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<Favorito>()
        } catch (e: Exception) {
            Log.e("FavoritoRepository", "Error al obtener favoritos: ${e.message}")
            emptyList()
        }
    }

    // Verificar si es favorito
    suspend fun isFavorito(userId: String, lugarId: Int): Boolean {
        return try {
            val result = SupabaseClient.client
                .from(TABLA)
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("lugar_id", lugarId)
                    }
                }
                .decodeList<Favorito>()
            result.isNotEmpty()
        } catch (e: Exception) {
            Log.e("FavoritoRepository", "Error al verificar favorito: ${e.message}")
            false
        }
    }

    // Añadir favorito
    suspend fun añadirFavorito(userId: String, lugarId: Int): Boolean {
        return try {
            Log.d("FavoritoRepository", "Añadiendo favorito: userId=$userId, lugarId=$lugarId")

            val favorito = Favorito(
                userId = userId,
                lugarId = lugarId
            )

            SupabaseClient.client
                .from(TABLA)
                .insert(favorito)

            Log.d("FavoritoRepository", "Favorito añadido exitosamente")
            true
        } catch (e: Exception) {
            Log.e("FavoritoRepository", "Error al añadir favorito: ${e.message}", e)
            false
        }
    }

    // Eliminar favorito
    suspend fun eliminarFavorito(userId: String, lugarId: Int): Boolean {
        return try {
            Log.d("FavoritoRepository", "Eliminando favorito: userId=$userId, lugarId=$lugarId")

            SupabaseClient.client
                .from(TABLA)
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("lugar_id", lugarId)
                    }
                }

            Log.d("FavoritoRepository", "Favorito eliminado exitosamente")
            true
        } catch (e: Exception) {
            Log.e("FavoritoRepository", "Error al eliminar favorito: ${e.message}", e)
            false
        }
    }

    // Alternar favorito
    suspend fun alternarFavorito(userId: String, lugarId: Int): Boolean {
        return try {
            val esFavorito = isFavorito(userId, lugarId)
            Log.d("FavoritoRepository", "Alternando favorito: lugarId=$lugarId, esFavorito=$esFavorito")

            val resultado = if (esFavorito) {
                eliminarFavorito(userId, lugarId)
            } else {
                añadirFavorito(userId, lugarId)
            }

            Log.d("FavoritoRepository", "Resultado: $resultado")
            resultado
        } catch (e: Exception) {
            Log.e("FavoritoRepository", "Error al alternar favorito", e)
            false
        }
    }
}