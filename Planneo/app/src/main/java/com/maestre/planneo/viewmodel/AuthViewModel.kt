package com.maestre.planneo.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maestre.planneo.db.AuthenticationRepository
import com.maestre.planneo.db.AuthenticationRepositoryImpl
import com.maestre.planneo.db.PerfilRepository
import com.maestre.planneo.db.SupabaseClient
import com.maestre.planneo.model.Empresa
import com.maestre.planneo.model.Perfil
import com.maestre.planneo.model.Usuario
import io.github.jan.supabase.postgrest.from
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

                var correo = repository.getCurrentUserEmail() ?: ""
                var estaLogueado = repository.isUserLoggedIn()



                val perfilDescargado = if (correo.isNotEmpty()) {
                    PerfilRepository.getPerfil(correo)
                } else {
                    null
                }

                _state.value = _state.value.copy(
                    estaLogueado = perfilDescargado != null || estaLogueado,
                    userEmail = perfilDescargado?.email ?: correo,
                    perfil = perfilDescargado,
                    cargando = false
                )
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error: ${e.message}")
            }
        }
    }

    fun cargarPerfil(email: String) {
        viewModelScope.launch {
            val perfilDescargado = PerfilRepository.getPerfil(email)
            if (perfilDescargado != null) {
                _state.value = _state.value.copy(
                    perfil = perfilDescargado,
                    userEmail =  email,
                    estaLogueado = true
                )
                Log.d("AuthViewModel", "Perfil cargado: ${perfilDescargado.email}")
            } else {
                Log.e("AuthViewModel", "No se encontró el perfil para el email: $email")
            }
        }
    }
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val passLong = password.toLongOrNull()

            if (passLong == null) {
                _loginState.value = LoginState.Error("La contraseña debe ser numérica")
                return@launch
            }

            val resultado = repository.signIn(email.trim(), passLong)

            if (resultado) {
                cargarPerfil(email.trim())
                _loginState.value = LoginState.Success(email.trim())
            } else {
                _loginState.value = LoginState.Error("Credenciales incorrectas")
            }
        }
    }

    fun registrarUsuario(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val passLong = password.toLongOrNull()
            if (passLong == null) {
                _loginState.value = LoginState.Error("La contraseña debe ser solo números")
                return@launch
            }

            try {
                val nuevoUsuario = Usuario(
                    email = email.trim(),
                    contrasena = passLong
                )


                val resultado = repository.registrarUsuario(nuevoUsuario)

                if (resultado) {
                    _loginState.value = LoginState.Success(email)
                    _state.value = _state.value.copy(mesnajeExito = "Usuario registrado con éxito")
                } else {
                    _loginState.value = LoginState.Error("No se pudo guardar en la tabla usuario")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error: ${e.message}")
            }
        }
    }
    fun registrarEmpresa(email: String, password: String, cif: String, web: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val passLong = password.toLongOrNull()
            if (passLong == null) {
                _loginState.value = LoginState.Error("La contraseña debe ser numérica")
                return@launch
            }

            try {
                val nuevaEmpresa = Empresa(
                    email = email.trim(),
                    contrasena = passLong,
                    cif = cif.trim(),
                    web = web.trim()
                )

                val resultado = repository.registrarEmpresa(nuevaEmpresa)

                if (resultado) {
                    _loginState.value = LoginState.Success(email)
                    _state.value = _state.value.copy(mesnajeExito = "Empresa registrada con éxito")
                } else {
                    _loginState.value = LoginState.Error("No se pudo guardar la empresa")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error: ${e.message}")
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

    fun actualizarPreferencias(nuevas: List<String>) {
        viewModelScope.launch {

            val emailActual = _state.value.perfil?.email ?: _state.value.userEmail

            if (emailActual.isNotEmpty()) {
                try {
                    SupabaseClient.client.from("perfiles").update(
                        mapOf("preferencias" to nuevas)
                    ) {
                        filter { eq("email", emailActual) }
                    }
                    val perfilBase = _state.value.perfil ?: Perfil(email = emailActual)
                    val perfilActualizado = perfilBase.copy(preferencias = nuevas)

                    _state.value = _state.value.copy(perfil = perfilActualizado)

                    Log.d("AuthViewModel", "Preferencias guardadas: $nuevas")
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Error al actualizar: ${e.message}")
                }
            } else {
                Log.e("AuthViewModel", "No hay email para actualizar preferencias")
            }
        }
    }


}