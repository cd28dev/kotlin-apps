package com.example.listaimagenes.presentation.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.listaimagenes.presentation.theme.AppTypography
import com.example.listaimagenes.presentation.theme.ColoresApp

@Composable
fun DialogoConfirmacion(
    titulo: String,
    mensaje: String,
    alConfirmar: () -> Unit,
    alCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = alCancelar,
        title = {
            Text(
                text = titulo,
                style = AppTypography.titleLarge.copy(
                    color = ColoresApp.Primario
                )
            )
        },
        text = {
            Text(
                text = mensaje,
                style = AppTypography.bodyMedium.copy(
                    color = ColoresApp.TextoSecundario
                )
            )
        },
        confirmButton = {
            TextButton(onClick = alConfirmar) {
                Text(
                    text = "Sí",
                    style = AppTypography.bodyMedium.copy(
                        color = ColoresApp.Confirmacion
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = alCancelar) {
                Text(
                    text = "Cancelar",
                    style = AppTypography.bodyMedium.copy(
                        color = ColoresApp.Error
                    )
                )
            }
        }
    )
}
