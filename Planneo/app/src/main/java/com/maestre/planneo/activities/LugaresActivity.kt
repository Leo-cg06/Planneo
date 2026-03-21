package com.maestre.planneo.activities

import com.maestre.planneo.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maestre.planneo.activitiess.DetalleLugarActivity
import com.maestre.planneo.components.BottomNavBar
import com.maestre.planneo.components.LugarAsyncImage
import com.maestre.planneo.model.LugarConFavorito
import com.maestre.planneo.ui.theme.Amarillo
import com.maestre.planneo.ui.theme.RojoOscuro
import com.maestre.planneo.ui.theme.PlanneoTheme
import com.maestre.planneo.viewmodel.MainViewModel

class LugaresActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            PlanneoTheme {
                val mainViewModel: MainViewModel = viewModel()
                val context = LocalContext.current

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavBar(currentScreen = "lugares", context = context)
                    }
                ) { innerPadding ->
                    LugaresContent(
                        viewModel = mainViewModel,
                        modifier = Modifier.padding(innerPadding),
                        onLugarClick = { lugarConFavorito ->
                            val intent = Intent(context, DetalleLugarActivity::class.java)
                            intent.putExtra("lugarId", lugarConFavorito.lugar.id)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LugaresContent(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onLugarClick: (LugarConFavorito) -> Unit
) {
    val lugares by viewModel.lugares.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    Log.d("LugaresContent", "Lugares: ${lugares.size}, Cargando: $cargando")

    LaunchedEffect(Unit) {
        Log.d("LugaresContent", "Cargando TODOS los lugares...")
        viewModel.cargarLugares()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Título
        Text(
            text = "Lugares de Interés",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Buscador
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.buscarLugares(it) }, // Sin tipo
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text(stringResource(R.string.buscar)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.buscarLugares("") }) {
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
        } else if (lugares.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.LocationOff,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay lugares disponibles",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(lugares, key = { it.lugar.id }) { lugarConFavorito ->
                    LugarCard(
                        lugarConFavorito = lugarConFavorito,
                        onClick = { onLugarClick(lugarConFavorito) },
                        onFavoritoClick = {
                            viewModel.alternarLugarFavorito(lugarConFavorito)
                            val mensaje = if (!lugarConFavorito.esFavorito)
                                R.string.anadido_favoritos
                            else
                                R.string.eliminado_favoritos
                            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LugarCard(
    lugarConFavorito: LugarConFavorito,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    val context = LocalContext.current
    val lugar = lugarConFavorito.lugar
    val urlImagen = buildImageUrl(lugar.fotoUrl ?: "", context)

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
                LugarAsyncImage(urlImagen, lugar)

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
                            imageVector = if (lugarConFavorito.esFavorito) Icons.Filled.Favorite
                            else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp),
                            tint = if (lugarConFavorito.esFavorito) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = lugar.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, null, Modifier.size(16.dp), tint = RojoOscuro)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = lugar.ubicacionTexto ?: "",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null, Modifier.size(16.dp), tint = Amarillo)
                        Text(" ${lugar.valoracionMedia}", style = MaterialTheme.typography.bodySmall)
                    }

                    Chip(
                        text = lugar.tipo.replaceFirstChar { it.uppercase() },
                        color = when(lugar.tipo) {
                            "restaurante" -> MaterialTheme.colorScheme.primaryContainer
                            "parque" -> MaterialTheme.colorScheme.secondaryContainer
                            "museo" -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                }

                if (!lugar.descripcion.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = lugar.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun Chip(text: String, color: androidx.compose.ui.graphics.Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

fun buildImageUrl(fotoUrl: String, context: android.content.Context): String {
    val baseUrl = context.getString(R.string.url_base_imagen)
    return if (fotoUrl.startsWith("http", ignoreCase = true)) fotoUrl
    else "$baseUrl$fotoUrl"
}