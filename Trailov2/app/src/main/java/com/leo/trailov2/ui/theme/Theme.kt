package com.leo.trailov2.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime. Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EsquemaColoresOscuro = darkColorScheme(
    primary = VerdeSecundarioClaro,
    onPrimary = Negro,
    primaryContainer = VerdePrimarioOscuro,
    onPrimaryContainer = Blanco,

    secondary = VerdeTerciarioClaro,
    onSecondary = Negro,
    secondaryContainer = VerdeTerciarioOscuro,
    onSecondaryContainer = Blanco,

    tertiary = VerdeExitoClaro,
    onTertiary = Negro,
    tertiaryContainer = VerdeExito,
    onTertiaryContainer = Blanco,

    background = FondoOscuro,
    onBackground = Blanco,
    surface = SuperficieOscura,
    onSurface = Blanco,
    surfaceVariant = FondoTarjetaOscura,
    onSurfaceVariant = Blanco,

    error = RojoClaro,
    onError = Negro,
    errorContainer = RojoOscuro,
    onErrorContainer = RojoClaro,

    outline = GrisMedio,
    outlineVariant = GrisOscuro,

    inverseSurface = Blanco,
    inverseOnSurface = FondoOscuro,
    inversePrimary = VerdePrimario
)

private val EsquemaColoresClaro = lightColorScheme(
    primary = VerdePrimario,
    onPrimary = Blanco,
    primaryContainer = FondoTarjetaClara,
    onPrimaryContainer = Negro,

    secondary = VerdeSecundario,
    onSecondary = Blanco,
    secondaryContainer = VerdeSecundarioClaro,
    onSecondaryContainer = Negro,

    tertiary = VerdeTerciario,
    onTertiary = Blanco,
    tertiaryContainer = VerdeTerciarioClaro,
    onTertiaryContainer = Negro,

    background = FondoClaro,
    onBackground = Negro,
    surface = SuperficieClara,
    onSurface = Negro,
    surfaceVariant = FondoTarjetaClara,
    onSurfaceVariant = GrisOscuro,

    error = Rojo,
    onError = Blanco,
    errorContainer = RojoClaro,
    onErrorContainer = RojoOscuro,

    outline = GrisMedio,
    outlineVariant = GrisClaro,

    inverseSurface = GrisOscuro,
    inverseOnSurface = Blanco,
    inversePrimary = VerdeSecundarioClaro
)


@Composable
fun Trailov2Theme(
    temaOscuro: Boolean = isSystemInDarkTheme(),
    colorDinamico: Boolean = false,
    contenido: @Composable () -> Unit
) {
    val esquemaColores = when {
        colorDinamico && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val contexto = LocalContext.current
            if (temaOscuro) dynamicDarkColorScheme(contexto)
            else dynamicLightColorScheme(contexto)
        }
        temaOscuro -> EsquemaColoresOscuro
        else -> EsquemaColoresClaro
    }

    val vista = LocalView.current
    if (!vista.isInEditMode) {
        SideEffect {
            val ventana = (vista. context as Activity).window
            ventana.statusBarColor = esquemaColores.primary.toArgb()
            WindowCompat.getInsetsController(ventana, vista).isAppearanceLightStatusBars = ! temaOscuro
        }
    }

    MaterialTheme(
        colorScheme = esquemaColores,
        typography = Typography,
        content = contenido
    )
}