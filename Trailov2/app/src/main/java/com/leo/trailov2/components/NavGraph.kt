package com.leo.trailov2.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.leo.trailov2.screens.*
import com.leo.trailov2.viewmodel.AuthViewModel
import com.leo.trailov2.viewmodel.MainViewModel


@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
    estaLogueado: Boolean,
    modifier: Modifier = Modifier
) {
    val startDestination = if (estaLogueado) Screen.ACTIVIDADES else Screen.WELCOME

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(Screen.WELCOME) {
            WelcomeScreen(
                onComenzar = {
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(Screen.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.ACTIVIDADES) {
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.REGISTER)
                }
            )
        }

        composable(Screen.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }


        composable(Screen.ACTIVIDADES) {
            ActividadesScreen(
                viewModel = mainViewModel,
                onActividadClick = { actividadConFavorito ->
                    navController.navigate(
                        Screen.detalleActividad(actividadConFavorito.actividad.id)
                    )
                }
            )
        }

        composable(Screen.PARQUES) {
            ParquesScreen(
                viewModel = mainViewModel,
                onParqueClick = { parqueConFavorito ->
                    navController.navigate(
                        Screen.detalleParque(parqueConFavorito.parque.id)
                    )
                }
            )
        }

        composable(Screen.PERFIL) {
            PerfilScreen(
                authViewModel = authViewModel,
                mainViewModel = mainViewModel,
                onActividadClick = { actividadConFavorito ->
                    navController.navigate(
                        Screen.detalleActividad(actividadConFavorito.actividad.id)
                    )
                },
                onParqueClick = { parqueConFavorito ->
                    navController.navigate(
                        Screen.detalleParque(parqueConFavorito.parque.id)
                    )
                },
                onLogout = {
                    navController.navigate(Screen.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }



        composable(
            route = Screen.DETALLE_ACTIVIDAD,
            arguments = listOf(navArgument("actividadId") { type = NavType.IntType })
        ) { backStackEntry ->
            val actividadId = backStackEntry.arguments?.getInt("actividadId") ?: 0
            DetalleActividadScreen(
                actividadId = actividadId,
                viewModel = mainViewModel,
                onBack = { navController.popBackStack() },
                onValorar = { tipo, id, nombre ->
                    navController.navigate(Screen.valorar(tipo, id, nombre))
                },
                onVerResenas = { tipo, id, nombre ->
                    navController.navigate(Screen.verResenas(tipo, id, nombre))
                }
            )
        }

        composable(
            route = Screen.DETALLE_PARQUE,
            arguments = listOf(navArgument("parqueId") { type = NavType.IntType })
        ) { backStackEntry ->
            val parqueId = backStackEntry.arguments?.getInt("parqueId") ?: 0
            DetalleParqueScreen(
                parqueId = parqueId,
                viewModel = mainViewModel,
                onBack = { navController.popBackStack() },
                onValorar = { tipo, id, nombre ->
                    navController.navigate(Screen.valorar(tipo, id, nombre))
                },
                onVerResenas = { tipo, id, nombre ->
                    navController.navigate(Screen.verResenas(tipo, id, nombre))
                }
            )
        }


        composable(
            route = Screen.VALORAR,
            arguments = listOf(
                navArgument("tipo") { type = NavType.StringType },
                navArgument("id") { type = NavType.IntType },
                navArgument("nombre") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            ValorarScreen(
                tipo = backStackEntry.arguments?.getString("tipo") ?: "",
                idReferencia = backStackEntry.arguments?.getInt("id") ?: 0,
                nombre = backStackEntry.arguments?.getString("nombre") ?: "",
                viewModel = mainViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.VER_RESENAS,
            arguments = listOf(
                navArgument("tipo") { type = NavType.StringType },
                navArgument("id") { type = NavType.IntType },
                navArgument("nombre") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            VerResenasScreen(
                tipo = backStackEntry.arguments?.getString("tipo") ?: "",
                idReferencia = backStackEntry.arguments?.getInt("id") ?: 0,
                viewModel = mainViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}