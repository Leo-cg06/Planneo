package com.leo.trailov2.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.leo.trailov2.R
import com.leo.trailov2.model.ActividadConFavorito
import com.leo.trailov2.ui.theme.Amarillo

import com.leo.trailov2.ui.theme.RojoOscuro
import com.leo.trailov2.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadesScreen(
    viewModel: MainViewModel,
    onActividadClick: (ActividadConFavorito) -> Unit
) {
    val actividades by viewModel.actividades.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.cargarActividades()
    }

    Scaffold(

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.buscarActividades(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.buscar)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.buscarActividades("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )

            if (cargando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(actividades, key = { it.actividad.id }) { actividadConFavorito ->
                        ActividadCard(
                            actividadConFavorito = actividadConFavorito,
                            onClick = { onActividadClick(actividadConFavorito) },
                            onFavoritoClick = {
                                viewModel.alternarActividadFavorito(actividadConFavorito)
                                val mensaje = if (! actividadConFavorito.esFavorito)
                                    R.string.anadido_favoritos
                                else
                                    R.string.eliminado_favoritos
                                Toast.makeText(context, mensaje,Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActividadCard(
    actividadConFavorito: ActividadConFavorito,
    onClick:  () -> Unit,
    onFavoritoClick: () -> Unit
) {
    val context = LocalContext.current
    val actividad = actividadConFavorito.actividad
    val urlImagen = buildImageUrl(actividad.fotoUrl, context)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(urlImagen)
                        .crossfade(true)
                        .build(),
                    contentDescription = actividad.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale. Crop
                )

                IconButton(
                    onClick = onFavoritoClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    ) {
                        Icon(
                            imageVector = if (actividadConFavorito.esFavorito) Icons. Filled.Favorite
                            else Icons. Filled.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp),
                            tint = if (actividadConFavorito.esFavorito) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = actividad.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, null, Modifier.size(16.dp), tint= RojoOscuro)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(                        text = actividad.ubicacion,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null, Modifier.size(16.dp), tint = Amarillo)
                        Text(" ${actividad.valoracion}", style = MaterialTheme.typography.bodySmall)
                    }


                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Timer, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Text(" ${actividad.duracion}", style = MaterialTheme.typography.bodySmall)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Stairs, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Text(" ${actividad.dificultad}", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Straighten, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Text(" ${actividad.km}", style = MaterialTheme.typography.bodySmall)
                    }


                }
            }
        }
    }
}

fun buildImageUrl(fotoUrl: String, context: android.content.Context): String {
    val baseUrl = context.getString(R.string.url_base_imagen)
    return if (fotoUrl.startsWith("http", ignoreCase = true)) fotoUrl
    else "$baseUrl$fotoUrl"
}