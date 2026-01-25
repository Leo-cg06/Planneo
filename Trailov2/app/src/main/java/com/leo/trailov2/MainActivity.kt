package com.leo.trailov2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.leo.trailov2.bd.AuthRepositoryImpl
import com.leo.trailov2.bd.SupabaseClient
import com.leo.trailov2.components.BottomNavBar
import com.leo.trailov2.components.NavGraph
import com.leo.trailov2.components.Screen
import com.leo.trailov2.ui.theme.Trailov2Theme
import com.leo.trailov2.viewmodel.AuthViewModel
import com.leo.trailov2.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        SupabaseClient.init(this)
        AuthRepositoryImpl.init(this)


        setContent {
            Trailov2Theme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val mainViewModel: MainViewModel = viewModel()

                val authState by authViewModel.state.collectAsState()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Mostrar barra de navegación solo en pantallas principales
                val showBottomBar = currentRoute in listOf(
                    Screen.ACTIVIDADES,
                    Screen.PARQUES,
                    Screen.PERFIL
                )

                // Mostrar animacion de cargando mienntras se verifica el estado de autenticación
                if (authState.cargando) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            if (showBottomBar && authState.estaLogueado) {
                                BottomNavBar(
                                    currentRoute = currentRoute,
                                    onNavigate = { route ->
                                        navController.navigate(route) {
                                            popUpTo(Screen.ACTIVIDADES) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavGraph(
                            navController = navController,
                            authViewModel = authViewModel,
                            mainViewModel = mainViewModel,
                            estaLogueado = authState.estaLogueado,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}