package com.maestre.planneo.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maestre.planneo.components.RegisterContent
import com.maestre.planneo.ui.theme.PlanneoTheme
import com.maestre.planneo.viewmodel.AuthViewModel

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PlanneoTheme {
                val authViewModel: AuthViewModel = viewModel()

                RegisterContent(
                    onRegisterSuccess = { finish() },
                    onNavigateBack = { finish() }
                )
            }
        }
    }
}

