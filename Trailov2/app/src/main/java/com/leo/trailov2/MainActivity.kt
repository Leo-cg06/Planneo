package com.leo.trailov2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.leo.trailov2.bd.AuthRepositoryImpl
import com.leo.trailov2.bd.SupabaseClient
import com.leo.trailov2.activities.ActividadesActivity
import com.leo.trailov2.activities.WelcomeActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SupabaseClient.init(this)
        AuthRepositoryImpl.init(this)

        // Redirigir según estado de autenticación
        val destino = if (AuthRepositoryImpl.isAuthenticated()) {
            ActividadesActivity::class.java
        } else {
            WelcomeActivity::class.java
        }

        val intent = Intent(this, destino)
        startActivity(intent)
        finish()
    }
}