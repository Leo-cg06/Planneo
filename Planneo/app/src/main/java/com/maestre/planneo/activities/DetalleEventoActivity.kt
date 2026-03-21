package com.maestre.planneo.activities

import com.maestre.planneo.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.maestre.planneo.activitiess.DetalleLugarActivity
import com.maestre.planneo.components.IndicadorCargando
import com.maestre.planneo.components.LocationOnIcon
import com.maestre.planneo.ui.theme.PlanneoTheme
import com.maestre.planneo.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class DetalleEventoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val eventoId = intent.getIntExtra("eventoId", 0)

        setContent {
            PlanneoTheme {
                val mainViewModel: MainViewModel = viewModel()

                DetalleEventoContent(
                    eventoId = eventoId,
                    viewModel = mainViewModel,
                    onBack = { finish() },
                    onVerLugar = { lugarId ->
                        val intent = Intent(this, DetalleLugarActivity::class.java)
                        intent.putExtra("lugarId", lugarId)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEventoContent(
    eventoId: Int,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onVerLugar: (Int) -> Unit
) {
    val eventos by viewModel.eventos.collectAsState()
    val lugares by viewModel.lugares.collectAsState()
    val evento = eventos.find { it.id == eventoId }
    val context = LocalContext.current

    LaunchedEffect(eventoId) {
        if (eventos.isEmpty()) {
            viewModel.cargarEventos()
        }
        if (lugares.isEmpty()) {
            viewModel.cargarLugares()
        }
    }

    if (evento == null) {
        IndicadorCargando()
        return
    }

    val lugar = lugares.find { it.lugar.id == evento.lugarId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Evento", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.volver))
                    }
                },
                actions = {
                    // Botón para compartir
                    IconButton(onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "¡Mira este evento! ${evento.nombre}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Compartir evento"))
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Compartir")
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
            // Imagen del evento
            if (!evento.fotoUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(buildImageUrl(evento.fotoUrl, context))
                        .crossfade(true)
                        .build(),
                    contentDescription = evento.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Event,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Título del evento
                Text(
                    text = evento.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tipo de evento (si existe)
                if (!evento.tipoEvento.isNullOrEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Category,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = evento.tipoEvento,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Información del evento en cards
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Fecha de inicio
                        InfoRow(
                            icon = Icons.Filled.CalendarToday,
                            label = "Inicio",
                            value = formatFechaCompleta(evento.fechaInicio)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Fecha de fin (si existe)
                        if (!evento.fechaFin.isNullOrEmpty()) {
                            InfoRow(
                                icon = Icons.Filled.EventAvailable,
                                label = "Fin",
                                value = formatFechaCompleta(evento.fechaFin)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Duración calculada
                        val duracion = calcularDuracion(evento.fechaInicio, evento.fechaFin)
                        if (duracion.isNotEmpty()) {
                            InfoRow(
                                icon = Icons.Filled.Schedule,
                                label = "Duración",
                                value = duracion
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción
                if (!evento.descripcion.isNullOrEmpty()) {
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = evento.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Información del lugar
                if (lugar != null) {
                    Text(
                        text = "Ubicación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = lugar.lugar.nombre,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LocationOnIcon()
                                //Lo dejo comentado porque el color varía un poco y quiero ver si se nota
//                                Icon(
//                                    Icons.Filled.LocationOn,
//                                    contentDescription = null,
//                                    modifier = Modifier.size(16.dp),
//                                    tint = MaterialTheme.colorScheme.primary
//                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = lugar.lugar.ubicacionTexto,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para ver el lugar
                    Button(
                        onClick = { onVerLugar(lugar.lugar.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Place, null, Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver detalles del lugar")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón para abrir en mapa
                    OutlinedButton(
                        onClick = {
                            val uri = Uri.parse("geo:${lugar.lugar.latitud},${lugar.lugar.longitud}?q=${lugar.lugar.latitud},${lugar.lugar.longitud}(${evento.nombre})")
                            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            if (mapIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(mapIntent)
                            } else {
                                val browserIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.google.com/maps/search/?api=1&query=${lugar.lugar.latitud},${lugar.lugar.longitud}")
                                )
                                context.startActivity(browserIntent)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Map, null, Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cómo llegar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Información adicional
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Recuerda consultar con el organizador para confirmar detalles y disponibilidad.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatFechaCompleta(fecha: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(fecha)
        val outputFormat = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy 'a las' HH:mm", Locale("es", "ES"))
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        fecha
    }
}

private fun calcularDuracion(fechaInicio: String, fechaFin: String?): String {
    if (fechaFin.isNullOrEmpty()) return ""
    
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val inicio = format.parse(fechaInicio)
        val fin = format.parse(fechaFin)
        
        if (inicio != null && fin != null) {
            val diffMillis = fin.time - inicio.time
            val horas = diffMillis / (1000 * 60 * 60)
            val minutos = (diffMillis / (1000 * 60)) % 60
            val dias = horas / 24
            
            when {
                dias > 0 -> "${dias} día${if (dias > 1) "s" else ""}"
                horas > 0 -> "${horas}h ${if (minutos > 0) "${minutos}min" else ""}"
                else -> "${minutos} minutos"
            }
        } else ""
    } catch (e: Exception) {
        ""
    }
}