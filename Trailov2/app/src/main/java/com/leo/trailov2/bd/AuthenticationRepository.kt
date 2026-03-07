package com.leo.trailov2.bd

interface AuthenticationRepository {

    suspend fun signIn(email: String, password: String): Boolean

    suspend fun signUp(email: String, password: String): Boolean

    suspend fun signOut(): Boolean

    fun isUserLoggedIn(): Boolean

    fun getCurrentUserEmail(): String?

    fun getCurrentUserId(): String?
}