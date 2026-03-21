package com.maestre.planneo.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maestre.planneo.components.LoginContent
import com.maestre.planneo.ui.theme.PlanneoTheme
import com.maestre.planneo.viewmodel.AuthViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PlanneoTheme {
                val authViewModel: AuthViewModel = viewModel()

                LoginContent(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        val intent = Intent(this, LugaresActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onNavigateToRegister = {
                        val intent = Intent(this, RegisterActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

