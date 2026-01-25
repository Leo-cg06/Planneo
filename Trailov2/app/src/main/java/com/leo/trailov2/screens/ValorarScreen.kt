package com.leo.trailov2.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leo.trailov2.R
import com.leo.trailov2.model.Valoracion
import com.leo.trailov2.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValorarScreen(
    tipo: String,
    idReferencia: Int,
    nombre: String,
    viewModel: MainViewModel,
    onBack:  () -> Unit
) {
    var nombreUsuario by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }
    var calificacion by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.valoracion), maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.volver))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.valoracion),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Estrellas interactivas
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..5) {
                    IconButton(
                        onClick = { calificacion = i.toFloat() }
                    ) {
                        Icon(
                            imageVector = if (i <= calificacion) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = "Estrella $i",
                            modifier = Modifier.size(40.dp),
                            tint = if (i <= calificacion) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = "${calificacion.toInt()}/5",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = nombreUsuario,
                onValueChange = { nombreUsuario = it },
                label = { Text(stringResource(R.string.tu_nombre)) },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text(stringResource(R.string.tu_comentario)) },
                leadingIcon = { Icon(Icons.Filled.Comment, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    when {
                        nombreUsuario.isBlank() -> {
                            Toast.makeText(context, context.getString(R.string.error_nombre_vacio), Toast.LENGTH_SHORT).show()
                        }
                        comentario.isBlank() -> {
                            Toast.makeText(context, context.getString(R.string.error_comentario_vacio), Toast.LENGTH_SHORT).show()
                        }
                        calificacion == 0f -> {
                            Toast.makeText(context, context.getString(R.string.error_sin_valoracion), Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            val valoracion = Valoracion(
                                tipo = tipo,
                                idReferencia = idReferencia,
                                nombreUsuario = nombreUsuario.trim(),
                                calificacion = calificacion,
                                comentario = comentario.trim()
                            )
                            viewModel.insertValoracion(
                                valoracion,
                                onSuccess = {
                                    Toast.makeText(context, context.getString(R.string.exito_resena), Toast.LENGTH_SHORT).show()
                                    onBack()
                                },
                                onError = {
                                    Toast.makeText(context, "Error al enviar", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Filled.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.enviar))
            }
        }
    }
}