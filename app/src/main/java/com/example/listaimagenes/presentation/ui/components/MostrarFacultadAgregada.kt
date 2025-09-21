package com.example.listaimagenes.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.listaimagenes.data.model.Facultad
import com.example.listaimagenes.presentation.theme.AppTypography
import com.example.listaimagenes.presentation.theme.ColoresApp
import com.example.listaimagenes.presentation.theme.Tamaños
import java.io.File

@Composable
fun MostrarFacultadAgregada(
    facultad: Facultad,
    alEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(Tamaños.EspacioGrande),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🆕 Mostrar foto personalizada o imagen por defecto
            if (facultad.fotoPersonalizada != null) {
                AsyncImage(
                    model = File(facultad.fotoPersonalizada),
                    contentDescription = facultad.nombre,
                    modifier = Modifier
                        .size(Tamaños.ImagenFacultad)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = facultad.imagen),
                    contentDescription = facultad.nombre,
                    modifier = Modifier.size(Tamaños.ImagenFacultad)
                )
            }

            Spacer(modifier = Modifier.height(Tamaños.EspacioChico))

            Text(
                text = facultad.nombre,
                style = AppTypography.titleLarge,
                color = ColoresApp.TextoPrincipal,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Año: ${facultad.año}",
                style = AppTypography.bodyMedium,
                color = ColoresApp.TextoSecundario,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Tamaños.EspacioChico))

            Text(
                text = facultad.descripcion,
                style = AppTypography.bodyMedium,
                color = ColoresApp.TextoPrincipal,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(Tamaños.EspacioGrande))

            Button(
                onClick = alEliminar,
                colors = ButtonDefaults.buttonColors(containerColor = ColoresApp.Error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = ColoresApp.TextoInverso
                )
                Spacer(modifier = Modifier.width(Tamaños.EspacioChico))
                Text(
                    "Eliminar Facultad",
                    style = AppTypography.labelLarge,
                    color = ColoresApp.TextoInverso
                )
            }
        }
    }
}