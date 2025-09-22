package com.example.listaimagenes.presentation.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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

    val todosLosCamposLlenos = estado.nombre.isNotBlank()
            && estado.apellido.isNotBlank()
            && estado.dni.isNotBlank()
            && !estado.foto.isNullOrBlank()

    if (estado.mostrarCamara) {
        PantallaCamara(
            alVolver = { viewModel.mostrarCamara(false) }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BarraSuperior("Registro de Persona")

        Column(
            modifier = Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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


            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Foto de la persona")
                    Spacer(Modifier.height(8.dp))

                    estado.foto?.let {
                        AsyncImage(File(it), contentDescription = "Foto", Modifier.size(150.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                        TextButton(onClick = { viewModel.limpiarFoto() }) { Text("Eliminar foto") }
                    } ?: OutlinedButton(onClick = { viewModel.mostrarCamara(true) }) { Text("Tomar Foto") }
                }
            }

            Button(
                onClick = {
                    viewModel.agregarPersona { exito ->
                        if (exito) alIrAVisualizacion()
                    }
                },
                enabled = todosLosCamposLlenos,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Registrar Persona")
            }
            Button(
                onClick = {
                    viewModel.cargarPersonas {
                        alIrAVisualizacion()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Ver Personas")
            }


            MostrarMensaje(estado.mensaje) { viewModel.limpiarMensaje() }
        }
    }
}

