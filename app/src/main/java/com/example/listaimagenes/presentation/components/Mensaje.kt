package com.example.listaimagenes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.listaimagenes.domain.model.MensajeUI
import kotlinx.coroutines.delay
@Composable
fun MostrarMensaje(mensaje: MensajeUI, onLimpiar: () -> Unit) {
    when (mensaje) {
        is MensajeUI.Exito -> {
            Text(
                text = mensaje.texto,
                color = MaterialTheme.colorScheme.tertiary, // éxito → terciario
                style = MaterialTheme.typography.bodyMedium
            )
            LaunchedEffect(mensaje) {
                delay(3000)
                onLimpiar()
            }
        }
        is MensajeUI.Error -> {
            Text(
                text = mensaje.texto,
                color = MaterialTheme.colorScheme.error, // error → error
                style = MaterialTheme.typography.bodyMedium
            )
            LaunchedEffect(mensaje) {
                delay(3000)
                onLimpiar()
            }
        }
        else -> {}
    }
}

@Composable
fun MensajeVacio(
    mensaje: String,
    textoBoton: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                mensaje,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    textoBoton,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
