package com.example.listaimagenes.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.listaimagenes.data.model.Facultad

@Composable
fun MenuFacultadesAgregadas(
    facultadSeleccionada: Facultad?,
    facultadesAgregadas: List<Facultad>,
    alSeleccionar: (Facultad) -> Unit,   // 👈 ahora recibe Facultad
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = facultadSeleccionada?.nombre?: "",
            onValueChange = {},
            label = { Text("Selexpandidoecciona facultad para ver") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expandido = !expandido }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir menú")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            facultadesAgregadas.forEach { facultad ->
                DropdownMenuItem(
                    text = { Text(facultad.nombre) },
                    onClick = {
                        alSeleccionar(facultad)
                        expandido = false
                    }
                )
            }
        }
    }
}