package com.leo.planneo.bd

import android.util.Log
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

object AuthenticationRepositoryImpl : AuthenticationRepository {

    override suspend fun signIn(email: String, password: String): Boolean {
        return try {
            SupabaseClient.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Log.d("AuthRepository", "Login exitoso")
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en signIn: ${e.message}")
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
