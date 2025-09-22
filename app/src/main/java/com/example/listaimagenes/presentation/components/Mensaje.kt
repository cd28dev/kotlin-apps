package com.example.listaimagenes.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
            Text(text = mensaje.texto, color = Color.Green)
            LaunchedEffect(mensaje) {
                delay(3000)
                onLimpiar()
            }
        }
        is MensajeUI.Error -> {
            Text(text = mensaje.texto, color = Color.Red)
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(mensaje, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                Text(textoBoton, color = Color.White)
            }
        }
    }
}
