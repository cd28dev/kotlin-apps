package com.example.listaimagenes.presentation.ui
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.listaimagenes.presentation.theme.AppTypography
import com.example.listaimagenes.presentation.theme.ColoresApp
import com.example.listaimagenes.presentation.theme.Tamaños
import com.example.listaimagenes.presentation.components.obtenerHoraActual
import kotlinx.coroutines.delay

@Composable
fun PiePagina(modifier: Modifier = Modifier) {
    var horaActual by remember { mutableStateOf(obtenerHoraActual()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            horaActual = obtenerHoraActual()
        }
    }

    Text(
        text = "© 2025 - Adrianzen, Huanca, Pasache $horaActual",
        modifier = modifier
            .fillMaxWidth()
            .padding(Tamaños.EspacioChico),
        textAlign = TextAlign.Center,
        style = AppTypography.labelSmall,
        color = ColoresApp.Secundario
    )
}
