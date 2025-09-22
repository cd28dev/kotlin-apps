package com.example.listaimagenes.presentation.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listaimagenes.presentation.ui.BarraSuperior
import com.example.listaimagenes.presentation.ui.DialogoConfirmacion
import com.example.listaimagenes.presentation.components.AccionesVisualizacion
import com.example.listaimagenes.presentation.components.MensajeVacio
import com.example.listaimagenes.presentation.components.PersonaCard
import com.example.listaimagenes.presentation.viewmodel.PersonaViewModel

@Composable
fun PantallaVisualizacionPersonas(
    viewModel: PersonaViewModel = viewModel(),
    alVolverFormulario: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    Column(Modifier.fillMaxSize()) {
        BarraSuperior("Personas Registradas")

        if (estado.personas.isNotEmpty()) {
            LazyColumn(Modifier.weight(1f).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(estado.personas) { persona ->
                    PersonaCard(persona) {
                        viewModel.seleccionarPersonaParaEliminar(persona) }
                }
            }

            AccionesVisualizacion(alVolverFormulario) { viewModel.toggleConfirmacionLimpiar(true) }
        } else {
            MensajeVacio("No hay personas registradas aún.", "Registrar Primera Persona", alVolverFormulario)
        }
    }

    estado.personaSeleccionada?.let { persona ->
        DialogoConfirmacion(
            titulo = "Eliminar persona",
            mensaje = "¿Deseas eliminar a ${persona.nombre} ${persona.apellido}?",
            alConfirmar = { viewModel.eliminarPersona(persona.dni) },
            alCancelar = { viewModel.seleccionarPersonaParaEliminar(null) }
        )
    }

    if (estado.mostrarConfirmacionLimpiar) {
        DialogoConfirmacion(
            "Limpiar todas las personas",
            "¿Deseas eliminar todas las personas registradas?",
            alConfirmar = { viewModel.limpiarTodo() },
            alCancelar = { viewModel.toggleConfirmacionLimpiar(false) }
        )
    }
}
