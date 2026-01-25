package com.leo.trailov2.bd

import android.util.Log
import com.leo.trailov2.model.Actividad
import io.github.jan.supabase.postgrest.from

object ActividadRepository {

    private const val TABLA = "actividades"

    suspend fun getAll(): List<Actividad> {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .select()
                .decodeList<Actividad>()
        } catch (e: Exception) {
            Log.e("ActividadRepository", "Error al obtener:  ${e.message}")
            emptyList()
        }
    }

    suspend fun search(query: String): List<Actividad> {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .select {
                    filter {
                        ilike("nombre", "%$query%")
                    }
                }
                .decodeList<Actividad>()
        } catch (e: Exception) {
            Log.e("ActividadRepository", "Error al buscar: ${e.message}")
            emptyList()
        }
    }

}