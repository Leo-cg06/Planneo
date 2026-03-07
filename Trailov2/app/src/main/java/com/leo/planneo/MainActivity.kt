package com.leo.planneo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.leo.planneo.bd.AuthenticationRepositoryImpl
import com.leo.planneo.activities.LugaresActivity
import com.leo.planneo.activities.WelcomeActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val destino = if (AuthenticationRepositoryImpl.isUserLoggedIn()) {
            LugaresActivity::class.java
        } else {
            WelcomeActivity::class.java
        }

        val intent = Intent(this, destino)
        startActivity(intent)
        finish()
    }
}