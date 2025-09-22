package com.example.listaimagenes.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.listaimagenes.presentation.theme.ColoresApp
import com.example.listaimagenes.presentation.theme.Tamaños
import com.example.listaimagenes.R
import com.example.listaimagenes.presentation.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String = "Facultades UNP",
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
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ColoresApp.Primario,
            titleContentColor = ColoresApp.TextoInverso
        )
    )
}
