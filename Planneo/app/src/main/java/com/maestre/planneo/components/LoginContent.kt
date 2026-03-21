package com.maestre.planneo.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.maestre.planneo.R
import com.maestre.planneo.viewmodel.AuthViewModel
import com.maestre.planneo.viewmodel.LoginState

@Composable
fun LoginContent(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var contrasenaVisible by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginState.Success -> {
                onLoginSuccess()
                viewModel.resetState()
            }
            is LoginState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppLogo()

        LoginInputForm(
            correo,
            {correo = it},
            contrasena,
            {contrasena = it},
            contrasenaVisible,
            { contrasenaVisible = it },
            loginState
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (loginState is LoginState.Loading) {
            CircularProgressIndicator()
        } else {
            ButtonIniciarSesion(viewModel, correo, contrasena)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text(stringResource(R.string.no_tienes_cuenta))
        }
    }
}

@Composable
fun LoginInputForm(
    correo: String,
    onCorreoChange: (String) -> Unit,
    contrasena: String,
    onContrasenaChange: (String) -> Unit,
    contrasenaVisible: Boolean,
    onContrasenaVisibleChange: (Boolean) -> Unit,
    loginState: LoginState
) {
    CorreoInputForm(
        correo,
        onCorreoChange,
        loginState)

    Spacer(modifier = Modifier.height(16.dp))

    ContrasenaInputForm(
        contrasena,
        onContrasenaChange,
        contrasenaVisible,
        onContrasenaVisibleChange,
        loginState
    )
}

@Composable
fun CorreoInputForm(
    correo: String,
    onCorreoChange: (String) -> Unit,
    loginState: LoginState) {
    OutlinedTextField(
        value = correo,
        onValueChange = { onCorreoChange(it) },
        label = { Text(stringResource(R.string.correo_electronico)) },
        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        isError = loginState is LoginState.Error
    )
}

@Composable
fun ContrasenaInputForm(
    contrasena: String,
    onContrasenaChange: (String) -> Unit,
    contrasenaVisible: Boolean,
    onContrasenaVisibleChange: (Boolean) -> Unit,
    loginState: LoginState
) {
    OutlinedTextField(
        value = contrasena,
        onValueChange = { onContrasenaChange(it) },
        label = { Text(stringResource(R.string.contrasena)) },
        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { onContrasenaVisibleChange(!contrasenaVisible) }) {
                Icon(
                    imageVector = if (contrasenaVisible) Icons.Filled.VisibilityOff
                    else Icons.Filled.Visibility,
                    contentDescription = null
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (contrasenaVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        isError = loginState is LoginState.Error
    )
}

@Composable
fun ButtonIniciarSesion(
    viewModel: AuthViewModel,
    correo: String,
    contrasena: String) {
    Button(
        onClick = { viewModel.signIn(correo, contrasena) },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(stringResource(R.string.iniciar_sesion))
    }
}