package com.maestre.planneo.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maestre.planneo.components.BottomNavBar
import com.maestre.planneo.model.Evento
import com.maestre.planneo.ui.theme.PlanneoTheme
import com.maestre.planneo.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

class CalendarioActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanneoTheme {
                val viewModel: MainViewModel = viewModel()
                val context = LocalContext.current
                var fechaSeleccionada by remember { mutableStateOf(LocalDate.now()) }

                Scaffold(
                    bottomBar = { BottomNavBar(currentScreen = "calendario", context = context) }
                ) { padding ->
                    Column(modifier = Modifier.padding(padding).fillMaxSize()) {

                        CalendarioGrid(
                            fechaSeleccionada = fechaSeleccionada,
                            onFechaChange = { fechaSeleccionada = it },
                            eventos = viewModel.eventos.collectAsState().value
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))


                        Text(
                            text = "Eventos para el ${fechaSeleccionada.dayOfMonth}",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        val eventos by viewModel.eventos.collectAsState()
                        val eventosDia = remember(fechaSeleccionada, eventos) {
                            eventos.filter { evento ->

                                val fechaLimpiaDB = evento.fechaInicio.trim().take(10)
                                val fechaCalendario = fechaSeleccionada.toString()


                                println("DEBUG: DB($fechaLimpiaDB) vs CAL($fechaCalendario)")

                                fechaLimpiaDB == fechaCalendario
                            }
                        }

                        if (eventosDia.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No hay planes para este día", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                                items(eventosDia) { evento ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                                            val intent = Intent(context, DetalleEventoActivity::class.java)
                                            intent.putExtra("eventoId", evento.id)
                                            startActivity(intent)
                                        }
                                    ) {
                                        ListItem(
                                            headlineContent = { Text(evento.nombre, fontWeight = FontWeight.Bold) },
                                            supportingContent = { Text(evento.descripcion, maxLines = 1) },
                                            leadingContent = { Icon(Icons.Default.Event, contentDescription = null) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarioGrid(
    fechaSeleccionada: LocalDate,
    onFechaChange: (LocalDate) -> Unit,
    eventos: List<Evento>
) {
    val yearMonth = YearMonth.from(fechaSeleccionada)
    val primerDiaMes = yearMonth.atDay(1)
    val diasEnMes = yearMonth.lengthOfMonth()
    val diaSemanaEmpieza = primerDiaMes.dayOfWeek.value % 7

    Column(modifier = Modifier.padding(16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = yearMonth.month.getDisplayName(TextStyle.FULL, Locale("es")).uppercase() + " " + yearMonth.year,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(onClick = { onFechaChange(fechaSeleccionada.minusMonths(1)) }) {
                    Icon(Icons.Default.ChevronLeft, null)
                }
                IconButton(onClick = { onFechaChange(fechaSeleccionada.plusMonths(1)) }) {
                    Icon(Icons.Default.ChevronRight, null)
                }
            }
        }

        // Días de la semana
        val diasSemana = listOf("D", "L", "M", "X", "J", "V", "S")
        Row(modifier = Modifier.fillMaxWidth()) {
            diasSemana.forEach { dia ->
                Text(
                    text = dia,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }


        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(280.dp)
        ) {

            items(diaSemanaEmpieza) { Spacer(modifier = Modifier.aspectRatio(1f)) }


            items(diasEnMes) { día ->
                val fechaActual = yearMonth.atDay(día + 1)
                val esHoy = fechaActual == LocalDate.now()
                val esSeleccionado = fechaActual == fechaSeleccionada
                val tieneEvento = eventos.any { it.fechaInicio.startsWith(fechaActual.toString()) }

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (esSeleccionado) MaterialTheme.colorScheme.primary
                            else if (esHoy) MaterialTheme.colorScheme.primaryContainer
                            else Color.Transparent
                        )
                        .clickable { onFechaChange(fechaActual) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (día + 1).toString(),
                            color = if (esSeleccionado) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (esSeleccionado || esHoy) FontWeight.Bold else FontWeight.Normal
                        )
                        if (tieneEvento) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(if (esSeleccionado) Color.White else MaterialTheme.colorScheme.secondary)
                            )
                        }
                    }
                }
            }
        }
    }
}