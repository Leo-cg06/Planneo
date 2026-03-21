package com.maestre.planneo.components

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
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
    loginState: LoginState,
    onNavigateToRegister: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()

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

    Spacer(modifier = Modifier.height(32.dp))

    if (loginState is LoginState.Loading) {
        CircularProgressIndicator()
    } else {
        ButtonIniciarSesion(authViewModel, correo, contrasena)
    }

    Spacer(modifier = Modifier.height(16.dp))

    TextButton(onClick = onNavigateToRegister) {
        Text(stringResource(R.string.no_tienes_cuenta))
    }
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
    onNavigateBack: () -> Unit,
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

    Spacer(modifier = Modifier.height(16.dp))

    TextButton(onClick = onNavigateBack) {
        Text(stringResource(R.string.ya_tienes_cuenta))
    }
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