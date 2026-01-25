package com.leo.trailov2.bd

import android.util.Log
import com.leo.trailov2.model.Favorito
import com.leo.trailov2.model.FavoritoInsert
import io.github.jan.supabase.postgrest.from

object FavoritoRepository {

    private const val TABLA = "favoritos"

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
            Log.e("FavoritoRepository", "Error al obtener favoritos:  ${e.message}")
            emptyList()
        }
    }

    suspend fun getFavoritosByUserAndTipo(userId: String, tipo:  String): List<Favorito> {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("tipo", tipo)
                    }
                }
                .decodeList<Favorito>()
        } catch (e: Exception) {
            Log.e("FavoritoRepository", "Error al obtener favoritos por tipo: ${e.message}")
            emptyList()
        }
    }

    suspend fun isFavorito(userId: String, tipo: String, itemId: Int): Boolean {
        return try {
            val result = SupabaseClient.client
                .from(TABLA)
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("tipo", tipo)
                        eq("item_id", itemId)
                    }
                }
                .decodeList<Favorito>()
            result.isNotEmpty()
        } catch (e: Exception) {
            Log.e("FavoritoRepository", "Error al verificar favorito: ${e.message}")
            false
        }
    }

    suspend fun añadirFavorito(userId: String, tipo: String, itemId: Int): Boolean {
        return try {
            val favorito = FavoritoInsert(
                userId = userId,
                tipo = tipo,
                itemId = itemId
            )
            SupabaseClient.client
                .from(TABLA)
                .insert(favorito)
            true
        } catch (e:  Exception) {
            Log.e("FavoritoRepository", "Error al añadir favorito: ${e.message}")
            false
        }
    }

    suspend fun eliminarFavorito(userId: String, tipo: String, itemId: Int): Boolean {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("tipo", tipo)
                        eq("item_id", itemId)
                    }
                }
            true
        } catch (e:  Exception) {
            Log.e("FavoritoRepository", "Error al eliminar favorito: ${e.message}")
            false
        }
    }

    suspend fun alternarFavorito(userId: String, tipo: String, itemId: Int): Boolean {
        val esFavorito = isFavorito(userId, tipo, itemId)
        return if (esFavorito) {
            eliminarFavorito(userId, tipo, itemId)
        } else {
            añadirFavorito(userId, tipo, itemId)
        }
    }
}