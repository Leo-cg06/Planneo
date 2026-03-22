package com.maestre.planneo.activities

import com.maestre.planneo.R
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maestre.planneo.activities.ValorarActivity
import com.maestre.planneo.activities.VerResenasActivity
import com.maestre.planneo.components.ArrowBackIconButton
import com.maestre.planneo.components.EventIcon
import com.maestre.planneo.components.IndicadorCargando
import com.maestre.planneo.components.LugarAsyncImage
import com.maestre.planneo.components.LugarDetalle
import com.maestre.planneo.model.Evento
import com.maestre.planneo.model.LugarConFavorito
import com.maestre.planneo.ui.theme.PlanneoTheme
import com.maestre.planneo.viewmodel.MainViewModel

class DetalleLugarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        val lugarId = intent.getIntExtra("lugarId", 0)

        setContent {
            PlanneoTheme {
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

    LaunchedEffect(lugarId) {
        if (lugares.isEmpty()) {
            viewModel.cargarLugares()
        }
        viewModel.cargarEventosPorLugar(lugarId)
    }

    if (lugarConFavorito == null) {
        IndicadorCargando()
        return
    }

    val lugar = lugarConFavorito.lugar
    val esFavorito = lugarConFavorito.esFavorito

    Scaffold(
        topBar = {
            DetalleLugarTopBar(onBack, lugarConFavorito, esFavorito, viewModel)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            LugarAsyncImage(lugar = lugar)

            LugarDetalle(lugar, eventos, onValorar, onVerResenas)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleLugarTopBar(
    onBack: () -> Unit,
    lugarConFavorito: LugarConFavorito,
    esFavorito: Boolean,
    viewModel: MainViewModel
) {
    TopAppBar(
        title = { Text("Detalle", maxLines = 1) },
        navigationIcon = {
            ArrowBackIconButton(onBack)
        },
        actions = {
            AlternarFavoritoIconButton(viewModel, lugarConFavorito, esFavorito)
        }
    )
}

//@Composable
//private fun StatItem(
//    icon: ImageVector,
//    value: String,
//    label: String,
//    colorIcono: Color = MaterialTheme.colorScheme.primary
//) {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Icon(imageVector = icon, contentDescription = null, tint = colorIcono, modifier = Modifier.size(28.dp))
//        Spacer(modifier = Modifier.height(4.dp))
//        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
//        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
//    }
//}

@Composable
fun AlternarFavoritoIconButton(
    viewModel: MainViewModel,
    lugarConFavorito: LugarConFavorito,
    esFavorito: Boolean
) {
    val context = LocalContext.current

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