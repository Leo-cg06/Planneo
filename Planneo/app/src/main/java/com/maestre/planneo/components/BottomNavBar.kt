package com.maestre.planneo.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.maestre.planneo.activities.EventoActivity
import com.maestre.planneo.activities.LugaresActivity
import com.maestre.planneo.activities.PerfilActivity

@Composable
fun BottomNavBar(
    currentScreen: String,
    context: Context
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Place, contentDescription = null) },
            label = { Text("Lugares") },
            selected = currentScreen == "lugares",
            onClick = {
                if (currentScreen != "lugares") {
                    val intent = Intent(context, LugaresActivity::class.java)
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Event, contentDescription = null) },
            label = { Text("Eventos") },
            selected = currentScreen == "eventos",
            onClick = {
                if (currentScreen != "eventos") {
                    val intent = Intent(context, EventoActivity::class.java)
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = null) },
            label = { Text("Favoritos") },
            selected = currentScreen == "Favoritos",
            onClick = {
                if (currentScreen != "Favoritos") {
                    val intent = Intent(context, PerfilActivity::class.java)
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                }
            }
        )
    }
}