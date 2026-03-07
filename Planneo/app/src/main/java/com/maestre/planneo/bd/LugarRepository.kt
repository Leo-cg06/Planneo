package com.maestre.planneo.bd

import android.util.Log
import com.maestre.planneo.model.Lugar
import io.github.jan.supabase.postgrest.from

object LugarRepository {

    private const val TABLA = "lugares"

    suspend fun getAll(): List<Lugar> {
        return try {
            Log.d("LugarRepository", "Obteniendo todos los lugares...")
            val result = SupabaseClient.client
                .from(TABLA)
                .select()
                .decodeList<Lugar>()
            Log.d("LugarRepository", "Lugares obtenidos: ${result.size}")
            result
        } catch (e: Exception) {
            Log.e("LugarRepository", "Error al obtener lugares: ${e.message}", e)
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getByTipo(tipo: String): List<Lugar> {
        return try {
            Log.d("LugarRepository", "Obteniendo lugares de tipo: $tipo")
            val result = SupabaseClient.client
                .from(TABLA)
                .select {
                    filter {
                        eq("tipo", tipo)
                    }
                }
                .decodeList<Lugar>()
            Log.d("LugarRepository", "Lugares de tipo $tipo obtenidos: ${result.size}")
            result
        } catch (e: Exception) {
            Log.e("LugarRepository", "Error al obtener lugares por tipo: ${e.message}", e)
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun search(query: String): List<Lugar> {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .select {
                    filter {
                        ilike("nombre", "%$query%")
                    }
                }
                .decodeList<Lugar>()
        } catch (e: Exception) {
            Log.e("LugarRepository", "Error al buscar lugares: ${e.message}")
            emptyList()
        }
    }

    suspend fun searchByTipo(query: String, tipo: String): List<Lugar> {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .select {
                    filter {
                        ilike("nombre", "%$query%")
                        eq("tipo", tipo)
                    }
                }
                .decodeList<Lugar>()
        } catch (e: Exception) {
            Log.e("LugarRepository", "Error al buscar lugares por tipo: ${e.message}")
            emptyList()
        }
    }

    suspend fun getById(id: Int): Lugar? {
        return try {
            SupabaseClient.client
                .from(TABLA)
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<Lugar>()
        } catch (e: Exception) {
            Log.e("LugarRepository", "Error al obtener lugar por ID: ${e.message}")
            null
        }
    }
}