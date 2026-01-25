package com.leo.trailov2.bd

import android.util.Log
import com.leo.trailov2.model.Valoracion
import io.github.jan.supabase.postgrest.from


object ValoracionRepository {

    private const val TABLA = "valoraciones"

    suspend fun getByTipoAndId(tipo: String, idReferencia: Int): List<Valoracion> {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .select {
                    filter {
                        eq("tipo", tipo)
                        eq("id_referencia", idReferencia)
                    }
                }
                .decodeList<Valoracion>()
        } catch (e: Exception) {
            Log.e("ValoracionRepository", "Error al obtener:  ${e.message}")
            emptyList()
        }
    }

    suspend fun insertarValoracion(valoracion: Valoracion): Boolean {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .insert(valoracion)
            true
        } catch (e:  Exception) {
            Log.e("ValoracionRepository", "Error al insertar: ${e.message}")
            false
        }
    }
}