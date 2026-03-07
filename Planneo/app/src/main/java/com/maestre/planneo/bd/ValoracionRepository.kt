package com.maestre.planneo.bd

import android.util.Log
import com.maestre.planneo.model.Valoracion
import io.github.jan.supabase.postgrest.from

object ValoracionRepository {

    private const val TABLA = "valoraciones"

    suspend fun getByLugarId(lugarId: Int): List<Valoracion> {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .select {
                    filter {
                        eq("lugar_id", lugarId)
                    }
                }
                .decodeList<Valoracion>()
        } catch (e: Exception) {
            Log.e("ValoracionRepository", "Error al obtener valoraciones: ${e.message}")
            emptyList()
        }
    }

    suspend fun insertarValoracion(valoracion: Valoracion): Boolean {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .insert(valoracion)
            true
        } catch (e: Exception) {
            Log.e("ValoracionRepository", "Error al insertar valoración: ${e.message}")
            false
        }
    }
}