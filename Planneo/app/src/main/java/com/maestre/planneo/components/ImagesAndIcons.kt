package com.maestre.planneo.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.maestre.planneo.R
import com.maestre.planneo.model.Lugar

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
fun LugarAsyncImage(urlImagen: String, lugar: Lugar) {
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