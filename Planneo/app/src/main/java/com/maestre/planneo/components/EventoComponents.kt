package com.maestre.planneo.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maestre.planneo.model.Evento

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
                EventIcon()
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
fun EventoFecha(evento: Evento){
    Text(
        text = evento.fechaInicio.take(10), // Solo la fecha
        style = MaterialTheme.typography.bodySmall
    )
}