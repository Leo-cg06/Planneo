package com.leo.trailov2.bd

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AuthRepositoryImpl {

    //Para que se guarde si el usuario ha iniciado sesión(local)
    private var sharedPrefs: SharedPreferences? = null

    //Inicializa el repositorio con el contexto de la aplicación.
    fun init(context: Context) {
        sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        Log.d("AuthRepository", "SharedPreferences inicializado")
    }

    suspend fun iniciarSesion(correo: String, contrasena:  String): Boolean {
        return withContext(Dispatchers.IO) { //Es un hilo que se ejucta de fondo (para hacer la peticion a la red a supabase) , mientras la interfaz de usuario sigue funcionando
            try {
                //Hace la peticion
                SupabaseClient.client.auth.signInWith(Email) {
                    this.email = correo
                    this.password = contrasena
                }

                // Guardar sesión manualmente para q cuando se vuelva a abrir la app siga logueado
                sharedPrefs?.edit()?.apply {
                    putBoolean("is_logged_in", true)
                    putString("user_email", correo)
                    apply()
                }

                Log.d("AuthRepository", "Login exitoso, sesión guardada")
                true
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error en login:  ${e.message}")
                false
            }
        }
    }

    suspend fun registrarse(correo: String, contrasena: String): Boolean {
        return withContext(Dispatchers.IO) { //Es un hilo que se ejucta de fondo (para hacer la peticion a la red a supabase) , mientras la interfaz de usuario sigue funcionando
            try {
                //Crea cuenta
                SupabaseClient.client.auth.signUpWith(Email) {
                    this.email = correo
                    this.password = contrasena
                }
                Log.d("AuthRepository", "Registro exitoso")
                true
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error en registro: ${e.message}")
                false
            }
        }
    }

    suspend fun cerrarSesion() {
        withContext(Dispatchers.IO) { //Es un hilo que se ejucta de fondo (para hacer la peticion a la red a supabase) , mientras la interfaz de usuario sigue funcionando
            try {
                //Cierra sesion en Supabase
                try {
                    SupabaseClient.client.auth.signOut()
                    Log.d("AuthRepository", "Sesión cerrada en Supabase")
                } catch (e: Exception) {
                    Log.e("AuthRepository", "Error al cerrar sesión en Supabase: ${e.message}")
                }

                // Limpia el estado de sesión
                sharedPrefs?.edit()?.clear()?.apply()
                Log.d("AuthRepository", "Logout completado exitosamente")

            } catch (e: Exception) {
                Log.e("AuthRepository", "Error general al cerrar sesión: ${e.message}")

                // Aunque de fallo se borra la sesión local
                try {
                    sharedPrefs?.edit()?.clear()?.apply()
                } catch (cleanupError: Exception) {
                }
            }
        }
    }
    //Verifica si hay una sesión activa, primero desde supabase y luego desde local.
    fun isAuthenticated(): Boolean {
        val supabaseAuth = try {
            SupabaseClient.client.auth.currentUserOrNull() != null
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error verificando Supabase auth: ${e.message}")
            false
        }

        val localAuth = sharedPrefs?.getBoolean("is_logged_in", false) ?: false

        val isAuth = supabaseAuth || localAuth
        Log.d("AuthRepository", "isAuthenticated:  $isAuth (supabase=$supabaseAuth, local=$localAuth)")
        return isAuth
    }

    //Obtiene el email del usuario actual, primero desde supabase y luego desde local.
    fun getCorreoUserActual(): String? {
        val supabaseEmail = try {
            SupabaseClient.client.auth.currentUserOrNull()?.email
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error obteniendo email de Supabase: ${e.message}")
            null
        }

        val localEmail = sharedPrefs?.getString("user_email", null)

        return supabaseEmail ?: localEmail
    }

    //Obtiene el ID del usuario actual desde supabase
    fun getUserIdActual(): String? {
        return try {
            SupabaseClient.client.auth.currentUserOrNull()?.id
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error obteniendo user ID:  ${e.message}")
            null
        }
    }
}