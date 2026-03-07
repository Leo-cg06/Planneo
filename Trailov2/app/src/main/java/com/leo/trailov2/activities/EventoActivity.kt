package com.leo.trailov2.activities

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.leo.trailov2.R
import com.leo.trailov2.bd.SupabaseClient
import com.leo.trailov2.components.BottomNavBar
import com.leo.trailov2.model.Evento
import com.leo.trailov2.ui.theme.Trailov2Theme
import com.leo.trailov2.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class EventoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()




        setContent {
            Trailov2Theme {
                val mainViewModel: MainViewModel = viewModel()
                val context = LocalContext.current

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavBar(currentScreen = "eventos", context = context)
                    }
                ) { innerPadding ->
                    EventosContent(
                        viewModel = mainViewModel,
                        modifier = Modifier.padding(innerPadding),
                        onEventoClick = { evento ->
                            // Navegar al detalle del evento
                            val intent = Intent(context, DetalleEventoActivity::class.java)
                            intent.putExtra("eventoId", evento.id)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosContent(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onEventoClick: (Evento) -> Unit
) {
    val eventos by viewModel.eventos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val lugares by viewModel.lugares.collectAsState()
    val context = LocalContext.current

    var mostrarSoloProximos by remember { mutableStateOf(true) }

    LaunchedEffect(mostrarSoloProximos) {
        if (mostrarSoloProximos) {
            viewModel.cargarEventosProximos()
        } else {
            viewModel.cargarEventos()
        }
        // Cargar lugares para mostrar información adicional
        viewModel.cargarLugares()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top Bar personalizado
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 3.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Eventos",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (mostrarSoloProximos) "Próximos eventos" else "Todos los eventos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilterChip(
                        selected = mostrarSoloProximos,
                        onClick = { mostrarSoloProximos = !mostrarSoloProximos },
                        label = {
                            Text(if (mostrarSoloProximos) "Próximos" else "Todos")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (mostrarSoloProximos) Icons.Filled.CalendarMonth else Icons.Filled.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
        }

        if (cargando) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (eventos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.EventBusy,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay eventos disponibles",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(eventos, key = { it.id }) { evento ->
                    val lugar = lugares.find { it.lugar.id == evento.lugarId }
                    EventoItemCard(
                        evento = evento,
                        nombreLugar = lugar?.lugar?.nombre ?: "Lugar desconocido",
                        ubicacionLugar = lugar?.lugar?.ubicacionTexto ?: "",
                        onClick = { onEventoClick(evento) }
                    )
                }
            }
        }
    }
}

@Composable
fun EventoItemCard(
    evento: Evento,
    nombreLugar: String,
    ubicacionLugar: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Imagen del evento o icono
            if (!evento.fotoUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(buildImageUrl(evento.fotoUrl, context))
                        .crossfade(true)
                        .build(),
                    contentDescription = evento.nombre,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Event,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = evento.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Lugar del evento
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Place,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = nombreLugar,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Fecha del evento
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatFechaEvento(evento.fechaInicio),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Tipo de evento
                    if (!evento.tipoEvento.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = evento.tipoEvento,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }

            // Flecha para indicar que es clickeable
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatFechaEvento(fechaInicio: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(fechaInicio)
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        fechaInicio.take(10)
    }
}

