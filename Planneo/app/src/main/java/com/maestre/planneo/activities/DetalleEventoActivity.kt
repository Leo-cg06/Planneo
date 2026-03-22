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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maestre.planneo.components.CategoryIcon
import com.maestre.planneo.components.EventIcon
import com.maestre.planneo.components.EventoAsyncImage
import com.maestre.planneo.components.EventoFechasCard
import com.maestre.planneo.components.EventoNombre
import com.maestre.planneo.components.EventoTipo
import com.maestre.planneo.components.IndicadorCargando
import com.maestre.planneo.components.LocationOnIcon
import com.maestre.planneo.components.ShareEventoButton
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
                    ShareEventoButton(shareMsg)
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {


            Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                if (!evento.fotoUrl.isNullOrEmpty()) {
                    EventoAsyncImage(evento)
                } else {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primaryContainer) {
                        Box(contentAlignment = Alignment.Center) {
                            EventIcon()
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                EventoNombre(evento)

                if (!evento.tipoEvento.isNullOrEmpty()) {
                    SuggestionChip(onClick = {}, label = { EventoTipo(evento) },
                        icon = { CategoryIcon() })
                }

                EventoFechasCard(evento)

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
fun InfoRow(
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
