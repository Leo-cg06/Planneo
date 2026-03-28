package com.maestre.planneo.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maestre.planneo.db.AuthenticationRepository
import com.maestre.planneo.db.AuthenticationRepositoryImpl
import com.maestre.planneo.db.PerfilRepository
import com.maestre.planneo.model.Perfil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val email: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

data class AuthState(
    val cargando: Boolean = false,
    val estaLogueado: Boolean = false,
    val userEmail: String = "",
    val error: String? = null,
    val mesnajeExito: String? = null,
    val perfil: Perfil? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AuthenticationRepository = AuthenticationRepositoryImpl

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    init {
        comprobarSesion()
    }

    private fun comprobarSesion() {
        viewModelScope.launch {
            try {
                val estaLogueado = repository.isUserLoggedIn()
                val correo = repository.getCurrentUserEmail() ?: ""
                val userId = repository.getCurrentUserId()

                val perfilDescargado = if (userId != null) {
                    PerfilRepository.getPerfil(userId)
                } else {
                    null
                }

                _state.value = AuthState(
                    cargando = false,
                    estaLogueado = estaLogueado,
                    userEmail = correo,
                    perfil = perfilDescargado
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

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            if (email.isBlank() || password.isBlank()) {
                _loginState.value = LoginState.Error("Por favor, completa todos los campos")
                return@launch
            }

            val resultado = repository.signIn(email.trim(), password)

            if (resultado) {
                _loginState.value = LoginState.Success(email.trim())
                _state.value = _state.value.copy(
                    estaLogueado = true,
                    userEmail = email.trim()
                )
            } else {
                _loginState.value = LoginState.Error("Credenciales incorrectas")
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            when {
                email.isBlank() || password.isBlank() -> {
                    _loginState.value = LoginState.Error("Por favor, completa todos los campos")
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    _loginState.value = LoginState.Error("Correo electrónico inválido")
                }
                password.length < 6 -> {
                    _loginState.value = LoginState.Error("La contraseña debe tener al menos 6 caracteres")
                }
                else -> {
                    val resultado = repository.signUp(email.trim(), password)
                    if (resultado) {
                        _loginState.value = LoginState.Success(email.trim())
                        _state.value = _state.value.copy(mesnajeExito = "Registro exitoso")
                    } else {
                        _loginState.value = LoginState.Error("Error al registrarse")
                    }
                }
            }
        }
    }

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(cargando = true)
                repository.signOut()
                _state.value = AuthState(cargando = false, estaLogueado = false)
                _loginState.value = LoginState.Idle
                onLogout()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error en logout: ${e.message}")
                _state.value = AuthState(cargando = false, estaLogueado = false)
                onLogout()
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _state.value = _state.value.copy(mesnajeExito = null)
    }

    fun actualizarPreferencias(nuevasPreferencias: List<String>) {
        val perfilActual = _state.value.perfil ?: return

        val perfilActualizado = perfilActual.copy(preferencias = nuevasPreferencias)

        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(cargando = true)

                val exito = PerfilRepository.updatePerfil(perfilActualizado)

                if (exito) {
                    _state.value = _state.value.copy(
                        cargando = false,
                        perfil = perfilActualizado,
                        mesnajeExito = "Preferencias guardadas con éxito"
                    )
                } else {
                    _state.value = _state.value.copy(
                        cargando = false,
                        error = "Error al guardar las preferencias"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(cargando = false, error = e.message)
            }
        }
    }


}