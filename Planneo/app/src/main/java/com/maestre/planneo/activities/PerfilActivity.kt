package com.maestre.planneo.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maestre.planneo.R
import com.maestre.planneo.components.BottomNavBar
import com.maestre.planneo.ui.theme.PlanneoTheme
import com.maestre.planneo.viewmodel.AuthViewModel

class PerfilActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val emailSession = intent.getStringExtra("USER_EMAIL") ?: ""

        setContent {
            PlanneoTheme {
                val authViewModel: AuthViewModel = viewModel()
                val authState by authViewModel.state.collectAsState()
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    if (emailSession.isNotEmpty()) {
                        authViewModel.cargarPerfil(emailSession)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavBar(currentScreen = "Perfil", context = context)
                    }
                ) { innerPadding ->
                    PerfilContent(
                        authState = authState,
                        emailSession = emailSession,
                        authViewModel = authViewModel,
                        modifier = Modifier.padding(innerPadding),
                        onLogout = {
                            authViewModel.logout {
                                val intent = Intent(context, WelcomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilContent(
    authState: com.maestre.planneo.viewmodel.AuthState,
    emailSession: String,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    val perfil = authState.perfil

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(title = { Text(stringResource(R.string.perfil)) })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.informacion_cuenta),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PerfilDetalleRow(
                        icon = Icons.Filled.Email,
                        label = stringResource(R.string.correo_electronico),
                        value = perfil?.email ?: emailSession.ifEmpty { "No disponible" }
                    )
                }
            }

            PreferenciasSection(
                preferenciasActuales = perfil?.preferencias ?: emptyList(),
                onGuardar = { nuevas -> authViewModel.actualizarPreferencias(nuevas) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.cerrar_sesion))
            }
        }
    }
}

@Composable
fun PerfilDetalleRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PreferenciasSection(
    preferenciasActuales: List<String>,
    onGuardar: (List<String>) -> Unit
) {
    val opciones = listOf("Restaurantes", "Parques", "Museos", "Ocio", "Naturaleza", "Deporte", "Fiesta", "Familia")
    var editando by remember { mutableStateOf(false) }

    val seleccionadas = remember(preferenciasActuales) {
        mutableStateOf(preferenciasActuales.toSet())
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mis Preferencias", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                IconButton(onClick = {
                    if (editando) onGuardar(seleccionadas.value.toList())
                    editando = !editando
                }) {
                    Icon(imageVector = if (editando) Icons.Filled.Check else Icons.Filled.Edit, contentDescription = null)
                }
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                opciones.forEach { opcion ->
                    val isSelected = seleccionadas.value.contains(opcion)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (editando) {
                                seleccionadas.value = if (isSelected) seleccionadas.value - opcion else seleccionadas.value + opcion
                            }
                        },
                        label = { Text(opcion) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Filled.Check, null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }
        }
    }
}