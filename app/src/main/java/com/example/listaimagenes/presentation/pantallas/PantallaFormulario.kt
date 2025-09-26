package com.example.listaimagenes.presentation.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.listaimagenes.presentation.ui.BarraSuperior
import com.example.listaimagenes.presentation.components.MostrarMensaje
import com.example.listaimagenes.presentation.viewmodel.PersonaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
@Composable
fun PantallaFormularioPersona(
    viewModel: PersonaViewModel = viewModel(),
    alIrAVisualizacion: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    val camposLlenos = estado.nombre.isNotBlank()
            && estado.apellido.isNotBlank()
            && estado.dni.isNotBlank()
            && !estado.foto.isNullOrBlank()
            && estado.correo.isNotBlank()

    if (estado.mostrarCamara) {
        PantallaCamara(
            alVolver = { viewModel.mostrarCamara(false) }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BarraSuperior("Registro de Persona")

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = estado.nombre,
                onValueChange = viewModel::actualizarNombre,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.apellido,
                onValueChange = viewModel::actualizarApellido,
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.dni,
                onValueChange = viewModel::actualizarDni,
                label = { Text("DNI") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.correo,
                onValueChange = viewModel::actualizarCorreo,
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    estado.foto?.let {
                        AsyncImage(
                            File(it),
                            contentDescription = "Foto",
                            modifier = Modifier
                                .size(150.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(Modifier.height(8.dp))

                        TextButton(onClick = { viewModel.limpiarFoto() }) {
                            Text("Eliminar foto", style = MaterialTheme.typography.labelLarge)
                        }
                    } ?: OutlinedButton(onClick = { viewModel.mostrarCamara(true) }) {
                        Icon(Icons.Default.AccountBox, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Tomar Foto")
                    }
                }
            }

            // Botón principal
            Button(
                onClick = {
                    viewModel.crear { exito ->
                        if (exito) alIrAVisualizacion()
                    }
                },
                enabled = camposLlenos,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Registrar Persona")
            }

            // Botón secundario
            OutlinedButton(
                onClick = {
                    viewModel.cargarPersonas {
                        alIrAVisualizacion()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.Default.List, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Ver Personas")
            }

            MostrarMensaje(estado.mensaje) { viewModel.limpiarMensaje() }
        }
    }
}


