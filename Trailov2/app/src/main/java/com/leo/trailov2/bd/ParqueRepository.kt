package com.leo.trailov2.bd

import android.util.Log
import com.leo.trailov2.model.Parque
import io.github.jan.supabase.postgrest.from

object  ParqueRepository {

    private const val TABLA = "parques"

    suspend fun getAll(): List<Parque> {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .select()
                .decodeList<Parque>()
        } catch (e: Exception) {
            Log.e("ParqueRepository", "Error al obtener:  ${e.message}")
            emptyList()
        }
    }

    suspend fun search(query: String): List<Parque> {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .select {
                    filter {
                        ilike("nombre", "%$query%")
                    }
                }
                .decodeList<Parque>()
        } catch (e: Exception) {
            Log.e("ParqueRepository", "Error al buscar: ${e.message}")
            emptyList()
        }
    }


}