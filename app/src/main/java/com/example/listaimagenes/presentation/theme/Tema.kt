package com.example.listaimagenes.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val EsquemaOscuro = darkColorScheme(
    primary = ColoresApp.Primario,
    onPrimary = ColoresApp.TextoInverso,

    secondary = ColoresApp.Secundario,
    onSecondary = ColoresApp.TextoInverso,

    tertiary = ColoresApp.Terciario,
    onTertiary = ColoresApp.TextoInverso,

    background = ColoresApp.FondoOscuro,
    onBackground = ColoresApp.TextoInverso,

    surface = ColoresApp.SuperficieOscura,
    onSurface = ColoresApp.TextoInverso,

    error = ColoresApp.Error,
    onError = ColoresApp.TextoInverso,

    outline = ColoresApp.Outline,
    outlineVariant = ColoresApp.OutlineVariant,
    scrim = ColoresApp.Scrim
)

private val EsquemaClaro = lightColorScheme(
    primary = ColoresApp.Primario,
    onPrimary = ColoresApp.TextoInverso,

    secondary = ColoresApp.Secundario,
    onSecondary = ColoresApp.TextoPrincipal,

    tertiary = ColoresApp.Terciario,
    onTertiary = ColoresApp.TextoInverso,

    background = ColoresApp.FondoClaro,
    onBackground = ColoresApp.TextoPrincipal,

    surface = ColoresApp.SuperficieClaro,
    onSurface = ColoresApp.TextoPrincipal,

    error = ColoresApp.Error,
    onError = ColoresApp.TextoInverso,

    outline = ColoresApp.Outline,
    outlineVariant = ColoresApp.OutlineVariant,
    scrim = ColoresApp.Scrim
)

@Composable
fun TemaApp(
    temaOscuro: Boolean = isSystemInDarkTheme(),
    colorDinamico: Boolean = true,
    contenido: @Composable () -> Unit
) {
    val esquemaColor = when {
        colorDinamico && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val contexto = LocalContext.current
            if (temaOscuro) dynamicDarkColorScheme(contexto) else dynamicLightColorScheme(contexto)
        }
        temaOscuro -> EsquemaOscuro
        else -> EsquemaClaro
    }

    MaterialTheme(
        colorScheme = esquemaColor,
        typography = AppTypography,
        content = contenido
    )
}
