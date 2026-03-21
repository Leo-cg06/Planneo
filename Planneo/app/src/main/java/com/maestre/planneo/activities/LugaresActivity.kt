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
import com.maestre.planneo.components.Buscador
import com.maestre.planneo.components.FavoritoIcon
import com.maestre.planneo.components.LugarAsyncImage
import com.maestre.planneo.components.ChipTipo
import com.maestre.planneo.components.IndicadorCargando
import com.maestre.planneo.components.LocationOffIcon
import com.maestre.planneo.components.LocationOnIcon
import com.maestre.planneo.components.LugarLazyColumn
import com.maestre.planneo.components.LugaresEmptyBox
import com.maestre.planneo.model.Lugar
import com.maestre.planneo.model.LugarConFavorito
import com.maestre.planneo.ui.theme.Amarillo
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
        Buscador(searchQuery, viewModel)

        if (cargando) {
            IndicadorCargando()
        } else if (lugares.isEmpty()) {
            LugaresEmptyBox()
        } else {
            LugarLazyColumn(lugares, onLugarClick, viewModel)
        }
    }
}

fun buildImageUrl(fotoUrl: String, context: android.content.Context): String {
    val baseUrl = context.getString(R.string.url_base_imagen)
    return if (fotoUrl.startsWith("http", ignoreCase = true)) fotoUrl
    else "$baseUrl$fotoUrl"
}