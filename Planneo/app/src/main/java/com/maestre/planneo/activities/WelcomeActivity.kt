package com.maestre.planneo.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.maestre.planneo.db.AuthenticationRepositoryImpl
import com.maestre.planneo.components.WelcomeContent
import com.maestre.planneo.ui.theme.PlanneoTheme

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (AuthenticationRepositoryImpl.isUserLoggedIn()) {
            val intent = Intent(this, LugaresActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContent {
            PlanneoTheme {
                WelcomeContent(
                    onComenzar = {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

