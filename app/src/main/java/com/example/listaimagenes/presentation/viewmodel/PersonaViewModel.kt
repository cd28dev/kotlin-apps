package com.example.listaimagenes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listaimagenes.domain.model.EstadoPersona
import com.example.listaimagenes.domain.model.MensajeUI
import com.example.listaimagenes.domain.model.Persona
import com.example.listaimagenes.domain.usecase.PersonaManager
import com.example.listaimagenes.domain.usecase.Resultado
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

    fun actualizarCorreo(v:String){
        _estado.update { it.copy(correo = v) }
    }
    fun actualizarDni(v: String) {
        _estado.update { it.copy(dni = v) }
    }
    fun establecerFoto(ruta: String) {
        _estado.update { it.copy(foto = ruta) }
    }
    fun limpiarFoto() {
        _estado.update { it.copy(foto = null) }
        System.gc()
    }

    fun crear(onExito: (Boolean) -> Unit) {
        val e = _estado.value
        val persona: Persona = Persona(nombre=e.nombre, apellido = e.apellido,dni = e.dni,correo=e.correo,foto=e.foto)
        viewModelScope.launch {
            when (val resultado = casoUso.crear(persona)) {
                is Resultado.Exito -> {
                    val personas = casoUso.listar()
                    _estado.update {
                        it.copy(
                            dni = "", nombre = "", apellido = "",correo="",foto = null,
                            personas = personas,
                            mensaje = MensajeUI.Exito("Persona agregada correctamente")
                        )
                    }
                    delay(3000)
                    onExito(true)
                }
                is Resultado.Error -> {
                    _estado.update { it.copy(mensaje = MensajeUI.Error(resultado.mensaje)) }
                    onExito(false)
                }
            }
        }
    }

    fun cargarPersonas(onListo: () -> Unit = {}) {
        viewModelScope.launch {
            val personas = casoUso.listar()
            _estado.update { it.copy(personas = personas) }
            onListo()
        }
    }


    fun eliminar(persona:Persona) {
        viewModelScope.launch {
            _estado.update { it.copy(personaSeleccionada = null) }
            System.gc()
            delay(100)

            val exitoso = casoUso.eliminar(persona)
            if (exitoso==1) {
                _estado.update {
                    it.copy(
                        personas = casoUso.listar(),
                        personaSeleccionada = null,
                        mostrarConfirmacionEliminar = false,
                        mensaje = MensajeUI.Exito("Persona eliminada correctamente")
                    )
                }
            } else {
                _estado.update {
                    it.copy(
                        mensaje = MensajeUI.Error("Error al eliminar la persona"),
                        mostrarConfirmacionEliminar = false
                    )
                }
            }
        }
    }
    fun limpiarTodo() {
        viewModelScope.launch {
            _estado.update {
                it.copy(
                    personas = emptyList(),
                    personaSeleccionada = null,
                    foto = null
                )
            }
            System.gc()
            delay(200)

            val exitoso = casoUso.limpiarTodas()
            if (exitoso) {
                _estado.update {
                    it.copy(
                        personas = emptyList(),
                        mensaje = MensajeUI.Exito("Todo eliminado correctamente"),
                        mostrarConfirmacionLimpiar = false
                    )
                }
            } else {
                _estado.update {
                    it.copy(
                        mensaje = MensajeUI.Error("Error al limpiar todo"),
                        mostrarConfirmacionLimpiar = false
                    )
                }
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

