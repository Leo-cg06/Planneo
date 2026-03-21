package com.maestre.planneo.components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maestre.planneo.R
import com.maestre.planneo.viewmodel.AuthViewModel
import com.maestre.planneo.viewmodel.LoginState

@Composable
fun RegisterContent(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmcontrasena by remember { mutableStateOf("") }
    var contrasenaVisible by remember { mutableStateOf(false) }

    val authViewModel: AuthViewModel = viewModel()
    val loginState by authViewModel.loginState.collectAsState()
    val context = LocalContext.current
    val resources = LocalResources.current

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginState.Success -> {
                Toast.makeText(context, resources.getString(R.string.exito_registro), Toast.LENGTH_LONG).show()
                authViewModel.resetState()
                onRegisterSuccess()
            }
            is LoginState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            RegisterTopApp(onNavigateBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLogo()
            PersonAddIcon()

            Spacer(modifier = Modifier.height(32.dp))

            RegisterForm(
                correo,
                {correo = it},
                contrasena,
                { contrasena = it },
                confirmcontrasena,
                {confirmcontrasena = it},
                contrasenaVisible,
                { contrasenaVisible = it },
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateBack) {
                Text(stringResource(R.string.ya_tienes_cuenta))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTopApp(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.registrarse)) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.volver))
            }
        }
    )
}

@Composable
fun RegisterButton(
    correo: String,
    contrasena: String,
    confirmcontrasena: String,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val resources = LocalResources.current

    Button(
        onClick = {
            when {
                correo.isBlank() || contrasena.isBlank() || confirmcontrasena.isBlank() -> {
                    Toast.makeText(context, resources.getString(R.string.error_campos_vacios), Toast.LENGTH_SHORT).show()
                }
                contrasena != confirmcontrasena -> {
                    Toast.makeText(context, resources.getString(R.string.error_contrasenas_no_coinciden), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    viewModel.signUp(correo, contrasena)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(stringResource(R.string.registrarse))
    }
}