package com.maestre.planneo.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maestre.planneo.R
import com.maestre.planneo.viewmodel.AuthViewModel
import com.maestre.planneo.viewmodel.LoginState

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
        leadingIcon = { EmailIcon() },
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
    onContrasenaVisibleChange: ((Boolean) -> Unit)? = null, //Opcional
    loginState: LoginState
) {
    OutlinedTextField(
        value = contrasena,
        onValueChange = { onContrasenaChange(it) },
        label = { Text(stringResource(R.string.contrasena)) },
        leadingIcon = { PasswordIcon() },
        trailingIcon = {
            //Sólo se muestra el icono si se pasa función onChange
            //Esto es para utilizar la misma función para los campos de contraseña en
            //la pantalla de registro
            if(onContrasenaVisibleChange != null) {
                ShowPasswordIcon(contrasenaVisible, onContrasenaVisibleChange)
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
fun RegisterForm(
    correo: String,
    onCorreoChange: (String) -> Unit,
    contrasena: String,
    onContrasenaChange: (String) -> Unit,
    confirmcontrasena: String,
    onConfirmContrasenaChange: (String) -> Unit,
    contrasenaVisible: Boolean,
    onContrasenaVisibleChange: (Boolean) -> Unit,
) {
    val authViewModel: AuthViewModel = viewModel()
    val loginState by authViewModel.loginState.collectAsState()

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

    Spacer(modifier = Modifier.height(16.dp))

    ContrasenaInputForm(
        contrasena = confirmcontrasena,
        onConfirmContrasenaChange,
        contrasenaVisible = contrasenaVisible,
        loginState = loginState
    )

    Spacer(modifier = Modifier.height(32.dp))

    if (loginState is LoginState.Loading) {
        CircularProgressIndicator()
    } else {
        RegisterButton(correo, contrasena, confirmcontrasena, authViewModel)
    }
}