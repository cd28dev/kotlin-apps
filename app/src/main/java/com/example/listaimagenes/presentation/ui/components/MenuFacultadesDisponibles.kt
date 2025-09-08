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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun MenuFacultadesDisponibles(
    facultadSeleccionada: String,
    facultadesDisponibles: List<String>,
    alSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(//ExposedDropdownMenuBox() mejor es este componente
            value = facultadSeleccionada,
            onValueChange = {},
            label = { Text("Selecciona facultad a agregar") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expandido = !expandido }) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Abrir menú"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            facultadesDisponibles.forEach { nombre ->
                DropdownMenuItem(
                    text = { Text(nombre) },
                    onClick = {
                        alSeleccionar(nombre)
                        expandido = false
                    }
                )
            }
        }
    }
}