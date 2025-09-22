package com.example.listaimagenes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listaimagenes.domain.model.EstadoPersona
import com.example.listaimagenes.domain.model.MensajeUI
import com.example.listaimagenes.domain.model.Persona
import com.example.listaimagenes.domain.usecase.PersonaManager
import com.example.listaimagenes.domain.usecase.ResultadoAgregarPersona
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PersonaViewModel() : ViewModel() {
    private val casoUso = PersonaManager.casoUso
    private val _estado = MutableStateFlow(EstadoPersona())
    val estado: StateFlow<EstadoPersona> = _estado


    fun actualizarNombre(v: String) {
        _estado.update { it.copy(nombre = v) }
    }
    fun actualizarApellido(v: String) {
        _estado.update { it.copy(apellido = v) }
    }
    fun actualizarDni(v: String) {
        _estado.update { it.copy(dni = v) }
    }
    fun establecerFoto(ruta: String) {
        _estado.update { it.copy(foto = ruta) }
    }
    fun limpiarFoto() {
        _estado.update { it.copy(foto = null) }
    }

    fun agregarPersona(onExito: (Boolean) -> Unit) {
        val e = _estado.value
        viewModelScope.launch {
            when (val resultado = casoUso.agregarPersona(e.nombre, e.apellido, e.dni, e.foto)) {
                is ResultadoAgregarPersona.Exito -> {
                    val personas = casoUso.obtenerPersonas()
                    _estado.update {
                        it.copy(
                            dni = "", nombre = "", apellido = "",foto = null,
                            personas = personas,
                            mensaje = MensajeUI.Exito("Persona agregada correctamente")
                        )
                    }
                    delay(3000)
                    onExito(true)
                }
                is ResultadoAgregarPersona.Error -> {
                    _estado.update { it.copy(mensaje = MensajeUI.Error(resultado.mensaje)) }
                    onExito(false)
                }
            }
        }
    }

    fun cargarPersonas(onListo: () -> Unit = {}) {
        viewModelScope.launch {
            val personas = casoUso.obtenerPersonas()
            _estado.update { it.copy(personas = personas) }
            onListo()
        }
    }


    fun eliminarPersona(dni: String) {
        viewModelScope.launch {
            casoUso.eliminarPersona(dni)
            _estado.update {
                it.copy(
                    personas = casoUso.obtenerPersonas(),
                    personaSeleccionada = null,
                    mostrarConfirmacionEliminar = false
                )
            }
        }
    }
    fun limpiarTodo() {
        viewModelScope.launch {
            casoUso.limpiarTodas()
            _estado.update {
                it.copy(
                    personas = emptyList(),
                    mensaje = MensajeUI.Ninguno,
                    mostrarConfirmacionLimpiar = false
                )
            }
        }
    }

    fun limpiarMensaje() {
        _estado.update { it.copy(mensaje = MensajeUI.Ninguno) }
    }

    fun mostrarCamara(show: Boolean) {
        _estado.update { it.copy(mostrarCamara = show) }
    }

    fun seleccionarPersonaParaEliminar(persona: Persona?) {
        _estado.update { it.copy(personaSeleccionada = persona, mostrarConfirmacionEliminar = persona != null) }
    }

    fun toggleConfirmacionLimpiar(show: Boolean) {
        _estado.update { it.copy(mostrarConfirmacionLimpiar = show) }
    }
}

