package com.leo.trailov2.bd

import android.util.Log
import com.leo.trailov2.model.Evento
import io.github.jan.supabase.postgrest.from

object EventoRepository {

    private const val TABLA = "eventos"

    suspend fun getAll(): List<Evento> {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .select()
                .decodeList<Evento>()
        } catch (e: Exception) {
            Log.e("EventoRepository", "Error al obtener eventos: ${e.message}")
            emptyList()
        }
    }

    suspend fun getByLugarId(lugarId: Int): List<Evento> {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .select {
                    filter {
                        eq("lugar_id", lugarId)
                    }
                }
                .decodeList<Evento>()
        } catch (e: Exception) {
            Log.e("EventoRepository", "Error al obtener eventos por lugar: ${e.message}")
            emptyList()
        }
    }

    suspend fun getProximos(): List<Evento> {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .select {
                    filter {
                        gte("fecha_inicio", "now()")
                    }
                }
                .decodeList<Evento>()
        } catch (e: Exception) {
            Log.e("EventoRepository", "Error al obtener eventos próximos: ${e.message}")
            emptyList()
        }
    }
}