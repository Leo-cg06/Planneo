package com.leo.trailov2.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.leo.trailov2.R
import com.leo.trailov2.activities.ActividadesActivity
import com.leo.trailov2.activities.ParquesActivity
import com.leo.trailov2.activities.PerfilActivity

@Composable
fun BottomNavBar(
    currentScreen: String,
    context: Context
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.DirectionsWalk, contentDescription = null) },
            label = { Text(stringResource(R.string.actividades)) },
            selected = currentScreen == "actividades",
            onClick = {
                if (currentScreen != "actividades") {
                    val intent = Intent(context, ActividadesActivity::class.java)
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Park, contentDescription = null) },
            label = { Text(stringResource(R.string.parques)) },
            selected = currentScreen == "parques",
            onClick = {
                if (currentScreen != "parques") {
                    val intent = Intent(context, ParquesActivity::class.java)
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) },
            label = { Text(stringResource(R.string.perfil)) },
            selected = currentScreen == "perfil",
            onClick = {
                if (currentScreen != "perfil") {
                    val intent = Intent(context, PerfilActivity::class.java)
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                }
            }
        )
    }
}