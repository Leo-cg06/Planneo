package com.leo.trailov2.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.leo.trailov2.R


@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.DirectionsWalk, contentDescription = null) },
            label = { Text(stringResource(R.string.actividades)) },
            selected = currentRoute == Screen.ACTIVIDADES,
            onClick = { onNavigate(Screen.ACTIVIDADES) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Park, contentDescription = null) },
            label = { Text(stringResource(R.string.parques)) },
            selected = currentRoute == Screen.PARQUES,
            onClick = { onNavigate(Screen.PARQUES) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) },
            label = { Text(stringResource(R.string.perfil)) },
            selected = currentRoute == Screen.PERFIL,
            onClick = { onNavigate(Screen.PERFIL) }
        )
    }
}