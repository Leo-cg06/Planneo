package com.maestre.planneo.db

import android.util.Log
import com.maestre.planneo.model.Perfil
import io.github.jan.supabase.postgrest.from

object PerfilRepository {

    private const val TABLA = "perfiles"

    suspend fun getPerfil(userId: String): Perfil? {
        return try {
            Log.d("PerfilRepository", "Obteniendo perfil para el usuario: $userId...")
            val result = SupabaseClient.client
                .from(TABLA)
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<Perfil>()

            if (result != null) {
                Log.d("PerfilRepository", "Perfil obtenido con éxito")
            } else {
                Log.d("PerfilRepository", "No se encontró el perfil en la base de datos")
            }

            result
        } catch (e: Exception) {
            Log.e("PerfilRepository", "Error al obtener el perfil: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }

    suspend fun updatePerfil(perfil: Perfil): Boolean {
        return try {
            Log.d("PerfilRepository", "Actualizando perfil del usuario: ${perfil.id}...")
            SupabaseClient.client
                .from(TABLA)
                .update(perfil) {
                    filter {
                        eq("id", perfil.id)
                    }
                }
            Log.d("PerfilRepository", "Perfil actualizado correctamente")
            true
        } catch (e: Exception) {
            Log.e("PerfilRepository", "Error al actualizar perfil: ${e.message}", e)
            e.printStackTrace()
            false
        }
    }
}