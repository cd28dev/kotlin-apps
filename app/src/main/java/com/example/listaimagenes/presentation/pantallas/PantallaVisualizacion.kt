package com.example.listaimagenes.presentation.pantallas

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listaimagenes.presentation.components.BarraSuperior
import com.example.listaimagenes.presentation.components.DialogoConfirmacion
import com.example.listaimagenes.presentation.components.BotonesVisualizacion
import com.example.listaimagenes.presentation.components.MensajeVacio
import com.example.listaimagenes.presentation.components.PersonaCard
import com.example.listaimagenes.presentation.components.PiePagina
import com.example.listaimagenes.presentation.viewmodel.PersonaViewModel

@SuppressLint("ContextCastToActivity")
@Composable
fun PantallaVisualizacionPersonas(
    viewModel: PersonaViewModel = viewModel(),
    alVolverFormulario: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val activity = LocalContext.current as Activity
    val context = LocalContext.current
    var dniBusqueda by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarPersonas()
    }

    Column(
        Modifier.fillMaxSize().
        background(MaterialTheme.colorScheme.background)
    ) {
        BarraSuperior("Personas Registradas",activity)

        OutlinedTextField(
            value = dniBusqueda,
            onValueChange = { dniBusqueda = it },
            label = { Text("Buscar por DNI", style = MaterialTheme.typography.labelMedium) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )


        if(estado.personas == null){
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }else {
            val personasFiltradas = if (dniBusqueda.isBlank()) {
                estado.personas
            } else {
                estado.personas.filter { it.dni.contains(dniBusqueda, ignoreCase = true) }
            }

            if (personasFiltradas.isNotEmpty()) {
                LazyColumn(
                    Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(personasFiltradas) { persona ->
                        PersonaCard(
                            persona = persona,
                            onEditar = {
                                viewModel.iniciarEdicion(persona)
                                alVolverFormulario()
                            },
                            onEliminar = { viewModel.seleccionarPersona(persona) }
                        )
                    }
                }

                BotonesVisualizacion(alVolverFormulario) {
                    viewModel.toggleConfirmacionLimpiar(true)
                }
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MensajeVacio("No se encontraron coincidencias.", "Registrar", alVolverFormulario)
                }
            }
        }
        PiePagina()
    }

    if (estado.mostrarConfirmacionEliminar && estado.personaSeleccionada != null) {
        DialogoConfirmacion(
            titulo = "Eliminar persona",
            mensaje = "¿Deseas eliminar a ${estado.personaSeleccionada!!.nombre} ${estado.personaSeleccionada!!.apellido}?",
            alConfirmar = { viewModel.eliminar(context,estado.personaSeleccionada!!) },
            alCancelar = { viewModel.seleccionarPersona(null) }
        )
    }

    if (estado.mostrarConfirmacionLimpiarTodo) {
        DialogoConfirmacion(
            titulo = "Limpiar todas las personas",
            mensaje = "¿Deseas eliminar todas las personas registradas?",
            alConfirmar = { viewModel.limpiarTodo(context) },
            alCancelar = { viewModel.toggleConfirmacionLimpiar(false) }
        )
    }
}
