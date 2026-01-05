package com.example.listaimagenes.reconocimiento.presentation.components

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.listaimagenes.presentation.theme.ColoresApp
import com.example.listaimagenes.reconocimiento.presentation.theme.Tamaños
import com.example.listaimagenes.presentation.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    activity: Activity
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Spacer(modifier = Modifier.width(Tamaños.EspacioChico))
                Text(
                    text = titulo,
                    style = AppTypography.titleLarge.copy(
                        color = ColoresApp.TextoInverso
                    )
                )
            }
        },
        actions = {
            IconButton(onClick = { activity.finish() }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = ColoresApp.TextoInverso
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ColoresApp.Primario,
            titleContentColor = ColoresApp.TextoInverso
        )
    )
}

