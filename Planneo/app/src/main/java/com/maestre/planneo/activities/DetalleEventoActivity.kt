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
    val context = LocalContext.current
    
    val evento = eventos.find { it.id == eventoId }
    val lugar = lugares.find { it.lugar.id == evento?.lugarId }

    LaunchedEffect(Unit) {
        if (eventos.isEmpty()) viewModel.cargarEventos()
        if (lugares.isEmpty()) viewModel.cargarLugares()
    }
    if (evento == null) {
        IndicadorCargando()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detalle_evento)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    val shareMsg = stringResource(R.string.mirar_evento, evento.nombre)
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareMsg)
                        }
                        context.startActivity(Intent.createChooser(intent, null))
                    }) { Icon(Icons.Default.Share, null) }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {


            Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                if (!evento.fotoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = buildImageUrl(evento.fotoUrl, context),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primaryContainer) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Event, null, Modifier.size(100.dp))
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(evento.nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                if (!evento.tipoEvento.isNullOrEmpty()) {
                    SuggestionChip(onClick = {}, label = { Text(evento.tipoEvento) },
                        icon = { Icon(Icons.Default.Category, null, Modifier.size(16.dp)) })
                }

                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoRow(Icons.Default.CalendarToday, "Inicio", formatFechaCompleta(evento.fechaInicio))
                        evento.fechaFin?.let { InfoRow(Icons.Default.EventAvailable, "Fin", formatFechaCompleta(it)) }
                        calcularDuracion(evento.fechaInicio, evento.fechaFin).takeIf { it.isNotEmpty() }?.let {
                            InfoRow(Icons.Default.Schedule, "Duración", it)
                        }
                    }
                }

                evento.descripcion.let {
                    Text(stringResource(R.string.descripcion), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                lugar?.let { item ->
                    Text(stringResource(R.string.ubicacion), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            LocationOnIcon()
                            Column(Modifier.padding(start = 8.dp)) {
                                Text(item.lugar.nombre, fontWeight = FontWeight.Bold)
                                Text(item.lugar.ubicacionTexto, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Button(onClick = { onVerLugar(item.lugar.id) }, Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Place, null); Text(" Ver detalles", Modifier.padding(start = 8.dp))
                    }

                    OutlinedButton(onClick = {
                        val uri = Uri.parse("geo:${item.lugar.latitud},${item.lugar.longitud}?q=${item.lugar.latitud},${item.lugar.longitud}")
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri).setPackage("com.google.android.apps.maps"))
                    }, Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Map, null); Text(" Cómo llegar", Modifier.padding(start = 8.dp))
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