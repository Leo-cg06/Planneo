package com.leo.trailov2.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.leo.trailov2.bd.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//  Estado de la autenticación que observan las pantallas
data class AuthState(
    val cargando: Boolean = true,
    val estaLogueado: Boolean = false,
    val userEmail: String = "",
    val error: String? = null,
    val mesnajeExito: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    init {
        // Al crear el ViewModel, comprobamos si ya hay sesión guardada
        Log.d("AuthViewModel", "Inicializando ViewModel")
        comprobarSesion()
    }

    //Comprueba si existe una sesión activa al iniciar la app
    private fun comprobarSesion() {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Comprobando sesión...")

                val estaLogueado = AuthRepositoryImpl.isAuthenticated()
                val correo = AuthRepositoryImpl.getCorreoUserActual() ?: ""

                Log.d("AuthViewModel", "Sesión:  logueado=$estaLogueado, correo=$correo")

                _state.value = AuthState(
                    cargando = false,
                    estaLogueado = estaLogueado,
                    userEmail = correo
                )
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al comprobar sesión: ${e.message}")
                _state.value = AuthState(
                    cargando = false,
                    estaLogueado = false,
                    error = "Error al verificar sesión"
                )
            }
        }
    }

    //Se usa viewModelScope.launch para ejecutar la operación de red sin bloquear la interfaz
    fun login(correo: String, contrasena: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(cargando = true, error = null)

            if (correo.isBlank() || contrasena.isBlank()) {
                _state.value = _state.value.copy(cargando = false, error = "error_campos_vacios")
                return@launch
            }

            val resultado = AuthRepositoryImpl.iniciarSesion(correo.trim(), contrasena)

            if (resultado) {
                _state.value = _state.value.copy(
                    cargando = false,
                    estaLogueado = true,
                    userEmail = correo.trim()
                )
                onSuccess()
            } else {
                _state.value = _state.value.copy(cargando = false, error = "error_credenciales")
            }
        }
    }

    fun register(correo: String, contrasena: String, confirmarContrasena: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(cargando = true, error = null, mesnajeExito = null)

            when {
                correo.isBlank() || contrasena.isBlank() || confirmarContrasena.isBlank() -> {
                    _state.value = _state.value.copy(cargando = false, error = "error_campos_vacios")
                }
                ! android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                    _state.value = _state.value.copy(cargando = false, error = "error_correo_invalido")
                }
                contrasena.length < 6 -> {
                    _state.value = _state.value.copy(cargando = false, error = "error_contrasena_corta")
                }
                contrasena != confirmarContrasena -> {
                    _state.value = _state.value.copy(cargando = false, error = "error_contrasenas_no_coinciden")
                }
                else -> {
                    val resultado = AuthRepositoryImpl.registrarse(correo.trim(), contrasena)
                    if (resultado) {
                        _state.value = _state.value.copy(
                            cargando = false,
                            mesnajeExito = "exito_registro"
                        )
                    } else {
                        _state.value = _state.value.copy(cargando = false, error = "error_registro")
                    }
                }
            }
        }
    }

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Iniciando logout...")

                // Poner estado de carga ANTES de cerrar sesión
                _state.value = _state.value.copy(cargando = true)

                // Cerrar sesión
                AuthRepositoryImpl.cerrarSesion()

                Log.d("AuthViewModel", "Logout completado")

                // Resetear estado
                _state.value = AuthState(cargando = false, estaLogueado = false)

                // Navegar DESPUÉS de actualizar el estado
                onLogout()

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error en logout:  ${e.message}")

                // Aunque falle, resetear sesión
                _state.value = AuthState(cargando = false, estaLogueado = false)
                onLogout()
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _state.value = _state.value.copy(mesnajeExito = null)
    }
}