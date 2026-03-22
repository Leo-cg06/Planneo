package com.maestre.planneo.components

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maestre.planneo.activities.InfoRow
import com.maestre.planneo.model.Evento
import com.maestre.planneo.viewmodel.MainViewModel

@Composable
fun EventoCard(evento: Evento) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            EventoNombre(evento)
            if (evento.descripcion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                EventoDescripcion(evento)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                EventFilledIcon()
                Spacer(modifier = Modifier.width(4.dp))
                EventoFecha(evento)
            }
        }
    }
}

@Composable
fun EventoNombre(evento: Evento) {
    Text(
        text = evento.nombre,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun EventoDescripcion(evento: Evento) {
    Text(
        text = evento.descripcion,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
fun EventoTipo(evento: Evento){
    Text(evento.tipoEvento!!)
}

@Composable
fun EventoFecha(evento: Evento){
    Text(
        text = evento.fechaInicio.take(10), // Solo la fecha
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
fun ShareEventoButton(shareMsg: String) {
    val context = LocalContext.current

    IconButton(onClick = {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareMsg)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }) { ShareIcon() }
}

@Composable
fun EventoFechasCard(evento: Evento){
    val mVM: MainViewModel = viewModel()

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoRow(Icons.Default.CalendarToday, "Inicio", mVM.formatFechaCompleta(evento.fechaInicio))
            evento.fechaFin?.let { InfoRow(Icons.Default.EventAvailable, "Fin", mVM.formatFechaCompleta(it)) }
            mVM.calcularDuracion(evento.fechaInicio, evento.fechaFin).takeIf { it.isNotEmpty() }?.let {
                InfoRow(Icons.Default.Schedule, "Duración", it)
            }
        }
    }
}