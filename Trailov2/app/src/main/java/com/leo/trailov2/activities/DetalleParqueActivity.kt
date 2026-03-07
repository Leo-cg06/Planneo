package com.leo.trailov2.activities

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
import com.leo.trailov2.R
import com.leo.trailov2.bd.AuthRepositoryImpl
import com.leo.trailov2.bd.SupabaseClient
import com.leo.trailov2.ui.theme.Amarillo
import com.leo.trailov2.ui.theme.MarronOscuro
import com.leo.trailov2.ui.theme.RojoOscuro
import com.leo.trailov2.ui.theme.Trailov2Theme
import com.leo.trailov2.viewmodel.MainViewModel

class DetalleParqueActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        SupabaseClient.init(this)
        AuthRepositoryImpl.init(this)

        val parqueId = intent.getIntExtra("parqueId", 0)

        setContent {
            Trailov2Theme {
                val mainViewModel: MainViewModel = viewModel()

                DetalleParqueContent(
                    parqueId = parqueId,
                    viewModel = mainViewModel,
                    onBack = { finish() },
                    onValorar = { tipo, id, nombre ->
                        val intent = Intent(this, ValorarActivity::class.java)
                        intent.putExtra("tipo", tipo)
                        intent.putExtra("idReferencia", id)
                        intent.putExtra("nombre", nombre)
                        startActivity(intent)
                    },
                    onVerResenas = { tipo, id, nombre ->
                        val intent = Intent(this, VerResenasActivity::class.java)
                        intent.putExtra("tipo", tipo)
                        intent.putExtra("idReferencia", id)
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
fun DetalleParqueContent(
    parqueId: Int,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onValorar: (String, Int, String) -> Unit,
    onVerResenas: (String, Int, String) -> Unit
) {
    val parques by viewModel.parques.collectAsState()
    val parqueConFavorito = parques.find { it.parque.id == parqueId }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (parques.isEmpty()) {
            viewModel.buscarParques()
        }
    }

    if (parqueConFavorito == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val parque = parqueConFavorito.parque
    val esFavorito = parqueConFavorito.esFavorito

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parque", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.volver))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.alternarParqueFavorito(parqueConFavorito)
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
                    .data(buildImageUrl(parque.fotoUrl, context))
                    .crossfade(true)
                    .build(),
                contentDescription = parque.nombre,
                modifier = Modifier.fillMaxWidth().height(250.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = parque.nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, null, Modifier.size(20.dp), tint = RojoOscuro)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(parque.ubicacion, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Star, null, tint = Amarillo)
                        Text(parque.valoracion.toString(), fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.valoracion), style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Landscape, null, tint = MarronOscuro)
                        Text(parque.extension, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.extension), style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = stringResource(R.string.informacion), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(parque.informacion, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = stringResource(R.string.fauna), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(parque.fauna, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = stringResource(R.string.flora), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(parque.flora, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val uri = Uri.parse("geo:${parque.latitud},${parque.longitud}?q=${parque.latitud},${parque.longitud}(${parque.nombre})")
                        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${parque.latitud},${parque.longitud}"))
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
                    Button(onClick = { onValorar("parque", parque.id, parque.nombre) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Star, null, Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.valorar))
                    }
                    Button(onClick = { onVerResenas("parque", parque.id, parque.nombre) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.RateReview, null, Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.ver_resenas))
                    }
                }
            }
        }
    }
}