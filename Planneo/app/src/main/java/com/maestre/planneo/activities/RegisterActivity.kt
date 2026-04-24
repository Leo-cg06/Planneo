package com.maestre.planneo.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.maestre.planneo.components.AppLogo
import com.maestre.planneo.components.PersonAddIcon
import com.maestre.planneo.components.RegisterForm
import com.maestre.planneo.ui.theme.PlanneoTheme
import com.maestre.planneo.viewmodel.AuthViewModel
import com.maestre.planneo.viewmodel.LoginState

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmcontrasena by remember { mutableStateOf("") }
    var contrasenaVisible by remember { mutableStateOf(false) }


    var cif by remember { mutableStateOf("") }
    var dominioWeb by remember { mutableStateOf("") }


    val opciones = listOf("Usuario", "Empresa")
    var expandido by remember { mutableStateOf(false) }
    var tipoUsuarioSeleccionado by remember { mutableStateOf(opciones[0]) }

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


            ExposedDropdownMenuBox(
                expanded = expandido,
                onExpandedChange = { expandido = !expandido },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = tipoUsuarioSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Registrarme como...") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandido,
                    onDismissRequest = { expandido = false }
                ) {
                    opciones.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                tipoUsuarioSeleccionado = opcion
                                expandido = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            if (tipoUsuarioSeleccionado == "Empresa") {

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico de empresa") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = cif,
                    onValueChange = { cif = it },
                    label = { Text("CIF de la empresa") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = dominioWeb,
                    onValueChange = { dominioWeb = it },
                    label = { Text("Dominio Web (ej: www.miempresa.com)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (contrasenaVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmcontrasena,
                    onValueChange = { confirmcontrasena = it },
                    label = { Text("Confirmar Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (contrasenaVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        when {
                            // 1. Validar que no haya campos vacíos
                            correo.isBlank() || cif.isBlank() || dominioWeb.isBlank() || contrasena.isBlank() -> {
                                Toast.makeText(context, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
                            }
                            // 2. Validar que las contraseñas coincidan
                            contrasena != confirmcontrasena -> {
                                Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                authViewModel.registrarEmpresa(
                                    email = correo,
                                    password = contrasena,
                                    cif = cif,
                                    web = dominioWeb
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar Empresa")
                }

            } else {
                RegisterForm(
                    correo,
                    { correo = it },
                    contrasena,
                    { contrasena = it },
                    confirmcontrasena,
                    { confirmcontrasena = it },
                    contrasenaVisible,
                    { contrasenaVisible = it },
                    onNavigateBack
                )
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