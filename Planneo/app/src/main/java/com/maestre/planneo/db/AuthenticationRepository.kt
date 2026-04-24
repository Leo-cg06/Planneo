package com.maestre.planneo.db

import com.maestre.planneo.model.Empresa
import com.maestre.planneo.model.Perfil
import com.maestre.planneo.model.Usuario

interface AuthenticationRepository {

    suspend fun signIn(email: String, password: Long): Boolean

    suspend fun signUp(email: String, password: String): Boolean

    suspend fun signOut(): Boolean

    fun isUserLoggedIn(): Boolean

    fun getCurrentUserEmail(): String?

    fun getCurrentUserId(): String?

    suspend fun registrarUsuario(usuario: Usuario): Boolean
    suspend fun registrarEmpresa(empresa: Empresa): Boolean
}