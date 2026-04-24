package com.maestre.planneo.db

import android.util.Log
import com.maestre.planneo.model.Perfil
import io.github.jan.supabase.postgrest.from

object PerfilRepository {

    private const val TABLA = "perfiles"

    suspend fun getPerfil(email: String): Perfil? {
        return try {
            val lista = SupabaseClient.client
                .from("perfiles")
                .select {
                    filter {
                        eq("email", email)
                    }
                }.decodeList<Perfil>()
            lista.firstOrNull()
        } catch (e: Exception) {
            Log.e("PerfilRepository", "Error: ${e.message}")
            null
        }
    }
}