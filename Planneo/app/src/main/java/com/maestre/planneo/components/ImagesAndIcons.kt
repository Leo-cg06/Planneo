package com.maestre.planneo.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.maestre.planneo.R
import com.maestre.planneo.model.Evento
import com.maestre.planneo.model.Lugar
import com.maestre.planneo.model.LugarConFavorito
import com.maestre.planneo.ui.theme.RojoOscuro
import com.maestre.planneo.viewmodel.MainViewModel

@Composable
fun AppLogo(){
    Image(
        painter = painterResource(id = R.drawable.planneologo),
        contentDescription = "Logo de la app",
        modifier = Modifier.size(300.dp)
    )
}

@Composable
fun PersonAddIcon(){
    Icon(
        imageVector = Icons.Filled.PersonAdd,
        contentDescription = null,
        modifier = Modifier.size(80.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun EmailIcon(){
    Icon(Icons.Filled.Email, contentDescription = null)
}

@Composable
fun PasswordIcon(){
    Icon(Icons.Filled.Lock, contentDescription = null)
}

@Composable
fun ShowPasswordIcon(contrasenaVisible: Boolean, onContrasenaVisibleChange: (Boolean) -> Unit) {
    IconButton(onClick = { onContrasenaVisibleChange(!contrasenaVisible) }) {
        Icon(
            imageVector = if (contrasenaVisible) Icons.Filled.VisibilityOff
            else Icons.Filled.Visibility,
            contentDescription = null
        )
    }
}

@Composable
fun LugarAsyncImage(urlImagen: String = lugar.fotoUrl,
                    lugar: Lugar) {
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(urlImagen)
            .crossfade(true)
            .build(),
        contentDescription = lugar.nombre,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun EventoAsyncImage(evento: Evento) {
    val mainViewModel: MainViewModel = viewModel()
    val context = LocalContext.current

    if(evento.fotoUrl != null) {
        AsyncImage(
            model = mainViewModel.buildImageUrl(evento.fotoUrl, context),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun FavoritoIcon(lugarConFavorito: LugarConFavorito) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
    ) {
        Icon(
            imageVector = if (lugarConFavorito.esFavorito) Icons.Filled.Favorite
            else Icons.Filled.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.padding(8.dp),
            tint = if (lugarConFavorito.esFavorito) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ChipTipo(lugar: Lugar) {
    AssistChip(
        onClick = { },
        label = { Text(lugar.tipo.replaceFirstChar { it.uppercase() }) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = when(lugar.tipo.lowercase()) {
                "restaurante" -> MaterialTheme.colorScheme.primaryContainer
                "parque" -> MaterialTheme.colorScheme.secondaryContainer
                "museo" -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
    )
}

@Composable
fun IndicadorCargando(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun LocationOffIcon(){
    Icon(
        imageVector = Icons.Filled.LocationOff,
        contentDescription = null,
        modifier = Modifier.size(80.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun LocationOnIcon(){
    Icon(Icons.Filled.LocationOn,
        null,
        Modifier.size(16.dp),
        tint = RojoOscuro)
}

@Composable
fun ArrowBackIconButton(onBack: () -> Unit) {
    IconButton(onClick = onBack) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.volver))
    }
}

@Composable
fun MapIcon(){
    Icon(Icons.Filled.Map, null, Modifier.size(18.dp))
}

@Composable
fun EventFilledIcon(){
    Icon(Icons.Filled.Event, null, Modifier.size(16.dp))
}

@Composable
fun EventIcon(){
    Icon(Icons.Default.Event, null, Modifier.size(100.dp))
}

@Composable
fun ShareIcon(){
    Icon(Icons.Default.Share, null)
}

@Composable
fun CategoryIcon(){
    Icon(Icons.Default.Category, null, Modifier.size(16.dp))
}