package com.leo.trailov2.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.leo.trailov2.R
import com.leo.trailov2.ui.theme.Amarillo
import com.leo.trailov2.ui.theme.RojoOscuro
import com.leo.trailov2.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleActividadScreen(
    actividadId: Int,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onValorar: (String, Int, String) -> Unit,
    onVerResenas: (String, Int, String) -> Unit
) {
    val actividades by viewModel.actividades.collectAsState()
    val actividadConFavorito = actividades.find { it.actividad.id == actividadId }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (actividades.isEmpty()) {
            viewModel.cargarActividades()
        }
    }

    if (actividadConFavorito == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val actividad = actividadConFavorito.actividad
    val esFavorito = actividadConFavorito.esFavorito

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Actividad", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.volver))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.alternarActividadFavorito(actividadConFavorito)
                        val mensaje = if (! esFavorito) R.string.anadido_favoritos else R.string.eliminado_favoritos
                        Toast.makeText(context, mensaje, Toast. LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = if (esFavorito) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
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
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(buildImageUrl(actividad.fotoUrl, context))
                    .crossfade(true)
                    .build(),
                contentDescription = actividad.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = actividad.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier. height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons. Filled.LocationOn, null, Modifier.size(20.dp) , tint = RojoOscuro)
                    Spacer(modifier = Modifier. width(4.dp))
                    Text(actividad.ubicacion, style = MaterialTheme.typography. bodyMedium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Estadísticas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(icon = Icons.Filled.Star, value = actividad.valoracion.toString(), label = stringResource(R.string.valoracion) , colorIcono = Amarillo)
                    StatItem(icon = Icons.Filled.Schedule, value = actividad.duracion, label = stringResource(R.string.duracion))
                    StatItem(icon = Icons.Filled.Stairs, value = actividad. dificultad, label = stringResource(R.string.dificultad))
                    StatItem(icon = Icons. Filled. Straighten, value = String.format("%.1f km", actividad.km), label = stringResource(R. string.distancia))
                }

                Spacer(modifier = Modifier. height(16.dp))

                Text(
                    text = stringResource(R.string.informacion),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = actividad.informacion,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón ubicación
                Button(
                    onClick = {
                        val uri = Uri.parse("geo:${actividad.latitud},${actividad.longitud}?q=${actividad.latitud},${actividad.longitud}(${actividad.nombre})")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        intent.setPackage("com.google.android.apps.maps")
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        } else {
                            val browserIntent = Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.google.com/maps/search/?api=1&query=${actividad.latitud},${actividad.longitud}"))
                            context.startActivity(browserIntent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Map, null, Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.ver_ubicacion))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onValorar("actividad", actividad.id, actividad.nombre) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Star, null, Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.valorar))
                    }

                    Button(
                        onClick = { onVerResenas("actividad", actividad.id, actividad.nombre) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.RateReview, null, Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.ver_resenas))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    icon:  androidx.compose.ui.graphics. vector.ImageVector,
    value: String,
    label: String,
    colorIcono: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary  // ← Parámetro nuevo
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorIcono,  // ← Usa el color que le pasaste
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography. bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme. onSurfaceVariant
        )
    }
}