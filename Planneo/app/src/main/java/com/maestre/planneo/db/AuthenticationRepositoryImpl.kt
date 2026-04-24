package com.maestre.planneo.db

import android.util.Log
import com.maestre.planneo.model.Empresa
import com.maestre.planneo.model.Perfil
import com.maestre.planneo.model.Usuario
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest

object AuthenticationRepositoryImpl : AuthenticationRepository {

    override suspend fun signIn(email: String, pass: Long): Boolean {
        return try {
            val usuario = SupabaseClient.client.postgrest["usuario"]
                .select {
                    filter {
                        eq("email", email)
                        eq("contraseña", pass)
                    }
                }.decodeList<Usuario>()

            if (usuario.isNotEmpty()) return true


            val empresa = SupabaseClient.client.postgrest["empresas"]
                .select {
                    filter {
                        eq("email", email)
                        eq("contrasena", pass)
                    }
                }.decodeList<Empresa>()
            empresa.isNotEmpty()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en signIn: ${e.message}")
            false
        }
    }
    override suspend fun registrarUsuario(usuario: Usuario): Boolean {
        return try {

            val usuarioInsertado = SupabaseClient.client.postgrest["usuario"]
                .insert(usuario) { select() }
                .decodeSingle<Usuario>()


            val nuevoPerfil = Perfil(
                id = usuarioInsertado.id,
                email = usuarioInsertado.email,
                nombre = "",
                apellido = "",
                preferencias = emptyList()
            )

            SupabaseClient.client.postgrest["perfiles"].insert(nuevoPerfil)
            Log.d("AuthRepository", "Perfil vinculado al ID: ${usuarioInsertado.id}")
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error al registrar perfil: ${e.message}")
            false
        }
    }
    override suspend fun registrarEmpresa(empresa: Empresa): Boolean {
        return try {
            SupabaseClient.client.postgrest["empresas"].insert(empresa)
            Log.d("AuthRepository", "Empresa guardada exitosamente")
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error al registrar empresa: ${e.message}")
            false
        }
    }

    override suspend fun signUp(email: String, password: String): Boolean {
        return try {
            SupabaseClient.client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Log.d("AuthRepository", "Registro exitoso")
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en signUp: ${e.message}")
            false
        }
    }

    override suspend fun signOut(): Boolean {
        return try {
            SupabaseClient.client.auth.signOut()
            Log.d("AuthRepository", "Logout exitoso")
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en signOut: ${e.message}")
            false
        }
    }

    override fun isUserLoggedIn(): Boolean {
        val isLogged = SupabaseClient.client.auth.currentUserOrNull() != null
        Log.d("AuthRepository", "Usuario logueado: $isLogged")
        return isLogged
    }

    override fun getCurrentUserEmail(): String? {
        return SupabaseClient.client.auth.currentUserOrNull()?.email
    }

    override fun getCurrentUserId(): String? {
        return SupabaseClient.client.auth.currentUserOrNull()?.id
    }
}
