package com.maestre.planneo.activities

import com.maestre.planneo.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.maestre.planneo.model.Evento
import com.maestre.planneo.ui.theme.Amarillo
import com.maestre.planneo.ui.theme.RojoOscuro
import com.maestre.planneo.ui.theme.Trailov2Theme
import com.maestre.planneo.viewmodel.MainViewModel

class DetalleLugarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        val lugarId = intent.getIntExtra("lugarId", 0)

        setContent {
            Trailov2Theme {
                val mainViewModel: MainViewModel = viewModel()

                DetalleLugarContent(
                    lugarId = lugarId,
                    viewModel = mainViewModel,
                    onBack = { finish() },
                    onValorar = { id, nombre ->
                        val intent = Intent(this, ValorarActivity::class.java)
                        intent.putExtra("lugarId", id)
                        intent.putExtra("nombre", nombre)
                        startActivity(intent)
                    },
                    onVerResenas = { id, nombre ->
                        val intent = Intent(this, VerResenasActivity::class.java)
                        intent.putExtra("lugarId", id)
                        intent.putExtra("nombre", nombre)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleLugarContent(
    lugarId: Int,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onValorar: (Int, String) -> Unit,
    onVerResenas: (Int, String) -> Unit
) {
    val lugares by viewModel.lugares.collectAsState()
    val eventos by viewModel.eventos.collectAsState()
    val lugarConFavorito = lugares.find { it.lugar.id == lugarId }
    val context = LocalContext.current

    LaunchedEffect(lugarId) {
        if (lugares.isEmpty()) {
            viewModel.cargarLugares()
        }
        viewModel.cargarEventosPorLugar(lugarId)
    }

    if (lugarConFavorito == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val lugar = lugarConFavorito.lugar
    val esFavorito = lugarConFavorito.esFavorito

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.volver))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.alternarLugarFavorito(lugarConFavorito)
                        val mensaje = if (!esFavorito) R.string.anadido_favoritos else R.string.eliminado_favoritos
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
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
                    .data(buildImageUrl(lugar.fotoUrl, context))
                    .crossfade(true)
                    .build(),
                contentDescription = lugar.nombre,
                modifier = Modifier.fillMaxWidth().height(250.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = lugar.nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, null, Modifier.size(20.dp), tint = RojoOscuro)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(lugar.ubicacionTexto, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        icon = Icons.Filled.Star,
                        value = String.format("%.1f", lugar.valoracionMedia),
                        label = stringResource(R.string.valoracion),
                        colorIcono = Amarillo
                    )
                    StatItem(
                        icon = Icons.Filled.Category,
                        value = lugar.tipo.replaceFirstChar { it.uppercase() },
                        label = "Tipo"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (lugar.descripcion.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.informacion),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = lugar.descripcion, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Eventos
                if (eventos.isNotEmpty()) {
                    Text(
                        text = "Eventos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    eventos.forEach { evento ->
                        EventoCard(evento = evento)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        val uri = Uri.parse("geo:${lugar.latitud},${lugar.longitud}?q=${lugar.latitud},${lugar.longitud}(${lugar.nombre})")
                        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${lugar.latitud},${lugar.longitud}"))
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

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { onValorar(lugar.id, lugar.nombre) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Star, null, Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.valorar))
                    }
                    Button(onClick = { onVerResenas(lugar.id, lugar.nombre) }, modifier = Modifier.weight(1f)) {
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
fun EventoCard(evento: Evento) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = evento.nombre,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            if (evento.descripcion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = evento.descripcion,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Event, null, Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = evento.fechaInicio.take(10), // Solo la fecha
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    colorIcono: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = colorIcono, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}