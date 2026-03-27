package com.maestre.planneo.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maestre.planneo.R
import com.maestre.planneo.model.Evento
import com.maestre.planneo.model.Lugar
import com.maestre.planneo.model.LugarConFavorito
import com.maestre.planneo.ui.theme.Amarillo
import com.maestre.planneo.viewmodel.MainViewModel

@Composable
fun LugarCard(
    lugarConFavorito: LugarConFavorito,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    val context = LocalContext.current
    val lugar = lugarConFavorito.lugar
    val mVM = viewModel<MainViewModel>()
    val urlImagen = mVM.buildImageUrl(lugar.fotoUrl, context)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            LugarImageBox(urlImagen, lugar, onFavoritoClick, lugarConFavorito)

            LugarDatos(lugar)
        }
    }
}

@Composable
fun InfoValoracion(lugar: Lugar) {
    Icon(Icons.Filled.Star, null, Modifier.size(16.dp), tint = Amarillo)
    Text(" ${lugar.valoracionMedia}", style = MaterialTheme.typography.bodySmall)
}

@Composable
fun Buscador(searchQuery: String, viewModel: MainViewModel) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.buscarLugares(it) }, // Sin tipo
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(stringResource(R.string.buscar)) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { viewModel.buscarLugares("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = null)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun LugaresEmptyBox(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LocationOffIcon()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                //TODO Esto hay que cambiarlo para que lo saque de la carpeta recursos
                text = "No hay lugares disponibles",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LugarLazyColumn(
    lugares: List<LugarConFavorito>,
    onLugarClick: (LugarConFavorito) -> Unit,
    viewModel: MainViewModel
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(lugares, key = { it.lugar.id }) { lugarConFavorito ->
            LugarCard(
                lugarConFavorito = lugarConFavorito,
                onClick = { onLugarClick(lugarConFavorito) },
                onFavoritoClick = {
                    viewModel.alternarLugarFavorito(lugarConFavorito)
                    val mensaje = if (!lugarConFavorito.esFavorito)
                        R.string.anadido_favoritos
                    else
                        R.string.eliminado_favoritos
                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun LugarImageBox(
    urlImagen: String,
    lugar: Lugar,
    onFavoritoClick: () -> Unit,
    lugarConFavorito: LugarConFavorito
) {
    Box {
        LugarAsyncImage(urlImagen, lugar)

        IconButton(
            onClick = onFavoritoClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            FavoritoIcon(lugarConFavorito)
        }
    }
}

@Composable
fun LugarNombre(lugar: Lugar) {
    Text(
        text = lugar.nombre,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun LugarUbicacion(lugar: Lugar) {
    Text(
        text = lugar.ubicacionTexto,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
fun LugarDescripcion(
    lugar: Lugar
){
    Text(
        text = lugar.descripcion,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2
    )
}

@Composable
fun LugarDatos(lugar: Lugar) {
    Column(modifier = Modifier.padding(16.dp)) {
        LugarNombre(lugar)

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            LocationOnIcon()
            Spacer(modifier = Modifier.width(4.dp))
            LugarUbicacion(lugar)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                InfoValoracion(lugar)
            }

            ChipTipo(lugar)
        }

        if (lugar.descripcion.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LugarDescripcion(lugar)
        }
    }
}

@Composable
fun LugarEventos(eventos: List<Evento>) {
    Text(
        //TODO Esto hay que cambiarlo para que lo saque de la carpeta recursos
        text = "Eventos",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))

    eventos.forEach { evento ->
        EventoCard(evento = evento)
        Spacer(modifier = Modifier.height(8.dp))
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun ValorarYResenasRow(
    onValorar: (Int, String) -> Unit,
    onVerResenas: (Int, String) -> Unit,
    lugar: Lugar
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { onValorar(lugar.id, lugar.nombre) }, modifier = Modifier.weight(1f)) {
            Icon(Icons.Filled.Star, null, Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.valorar))
        }

        Button(onClick = { onVerResenas(lugar.id, lugar.nombre) }, modifier = Modifier.weight(1f)) {
            Icon(Icons.Filled.RateReview, null, Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.ver_resenas))
        }
    }
}

@Composable
fun VerUbicacionButton(lugar: Lugar) {
    val context = LocalContext.current

    Button(
        onClick = {
            val uri =
                "geo:${lugar.latitud},${lugar.longitud}?q=${lugar.latitud},${lugar.longitud}(${lugar.nombre})".toUri()
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            mapIntent.setPackage("com.google.android.apps.maps")
            //TODO Hay que modificar el manifest para esto
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW,
                    "https://www.google.com/maps/search/?api=1&query=${lugar.latitud},${lugar.longitud}".toUri())
                context.startActivity(browserIntent)
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        MapIcon()
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.ver_ubicacion))
    }
}

@Composable
fun LugarDetalle(
    lugar: Lugar,
    eventos: List<Evento>,
    onValorar: (Int, String) -> Unit,
    onVerResenas: (Int, String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        LugarNombre(lugar)

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            LocationOnIcon()
            Spacer(modifier = Modifier.width(4.dp))
            LugarUbicacion(lugar)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoValoracion(lugar)
            ChipTipo(lugar)
            //No sé por qué aquí se usa StatItem, esto ya estaba hecho en
            //LugaresActivity de otra forma, lo dejo comentado por si acaso
//                    StatItem(
//                        icon = Icons.Filled.Star,
//                        value = String.format("%.1f", lugar.valoracionMedia),
//                        label = stringResource(R.string.valoracion),
//                        colorIcono = Amarillo
//                    )
//                    StatItem(
//                        icon = Icons.Filled.Category,
//                        value = lugar.tipo.replaceFirstChar { it.uppercase() },
//                        label = "Tipo"
//                    )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (lugar.descripcion.isNotEmpty()) {
            LugarDescripcion(lugar)
        }

        // Eventos
        if (eventos.isNotEmpty()) {
            LugarEventos(eventos)
        }

        VerUbicacionButton(lugar)

        Spacer(modifier = Modifier.height(8.dp))

        ValorarYResenasRow(onValorar, onVerResenas, lugar)
    }
}