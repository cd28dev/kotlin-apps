package com.example.listaimagenes.reconocimiento.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listaimagenes.reconocimiento.domain.model.EstadoPersona
import com.example.listaimagenes.reconocimiento.domain.model.MensajeUI
import com.example.listaimagenes.reconocimiento.domain.model.Persona
import com.example.listaimagenes.reconocimiento.domain.usecase.PersonaManager
import com.example.listaimagenes.reconocimiento.domain.usecase.Resultado
import com.example.listaimagenes.reconocimiento.domain.utils.UtilidadesImagen
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
    fun establecerImagenFacial(bitmap: Bitmap) {
        Log.d("PersonaViewModel", "Estableciendo imagen facial")
        val byteArray = UtilidadesImagen.bitmapAByteArray(bitmap)
        _estado.update { it.copy(imagenFacial = byteArray) }
        Log.d("PersonaViewModel", "Estado actualizado: imagenFacial size=${byteArray?.size ?: 0}")
    }

    fun limpiarImagenFacial() {
        _estado.update { it.copy(imagenFacial = null) }
        System.gc()
    }

    fun crear(context: Context, onExito: (Boolean) -> Unit) {
        val e = _estado.value
        
        if (e.procesandoRegistro) {
            Log.d("PersonaViewModel", "⚠Ya se está procesando un registro, ignorando...")
            return
        }
        
        _estado.update { it.copy(procesandoRegistro = true) }
        
        viewModelScope.launch {
            try {
                val persona = Persona(
                    nombre = e.nombre,
                    apellido = e.apellido,
                    dni = e.dni,
                    correo = e.correo,
                    imagenFacial = e.imagenFacial
                )

                when (val resultado = casoUso.crear(persona)) {
                    is Resultado.Exito -> {
                        val personas = casoUso.listar()
                        _estado.update {
                            it.copy(
                                dni = "", nombre = "", apellido = "", correo = "", imagenFacial = null,
                                personas = personas,
                                procesandoRegistro = false,
                                mensaje = MensajeUI.Exito("Persona agregada correctamente")
                            )
                        }
                        onExito(true)
                    }
                    is Resultado.Error -> {
                        _estado.update { it.copy(procesandoRegistro = false, mensaje = MensajeUI.Error(resultado.mensaje)) }
                        onExito(false)
                    }
                }
            } catch (e: Exception) {
                _estado.update { it.copy(procesandoRegistro = false) }
                onExito(false)
            }
        }
    }

    fun actualizar(context: Context, onExito: (Boolean) -> Unit) {
        val e = _estado.value
        val personaOriginal = e.personaSeleccionada ?: return
        
        if (e.procesandoRegistro) {
            return
        }
        
        _estado.update { it.copy(procesandoRegistro = true) }

        viewModelScope.launch {
            try {
                val persona = personaOriginal.copy(
                    nombre = e.nombre,
                    apellido = e.apellido,
                    dni = e.dni,
                    correo = e.correo,
                    imagenFacial = e.imagenFacial ?: personaOriginal.imagenFacial
                )

                when (val resultado = casoUso.actualizar(persona)) {
                    is Resultado.Exito -> {
                        val personas = casoUso.listar()
                        _estado.update {
                            it.copy(
                                nombre = "", apellido = "", dni = "", correo = "", imagenFacial = null,
                                personaSeleccionada = null,
                                esEdicion = false,
                                procesandoRegistro = false,
                                personas = personas,
                                mensaje = MensajeUI.Exito("Persona actualizada")
                            )
                        }
                        onExito(true)
                    }
                    is Resultado.Error -> {
                        _estado.update {
                            it.copy(procesandoRegistro = false, mensaje = MensajeUI.Error(resultado.mensaje))
                        }
                        onExito(false)
                    }
                }
            } catch (e: Exception) {
                _estado.update { it.copy(procesandoRegistro = false) }
                onExito(false)
            }
        }
    }





    fun cancelarEdicion() {
        _estado.update {
            it.copy(
                nombre = "",
                apellido = "",
                dni = "",
                correo = "",
                imagenFacial = null,
                personaSeleccionada = null,
                esEdicion = false
            )
        }
    }

    fun cargarPersonas(onListo: () -> Unit = {}) {
        viewModelScope.launch {
            val personas = casoUso.listar()
            _estado.update { it.copy(personas = personas) }
            onListo()
        }
    }

    fun eliminar(context: Context, persona: Persona) {
        viewModelScope.launch {
            _estado.update { it.copy(personaSeleccionada = null) }
            System.gc()
            delay(100)

            val exitoso = casoUso.eliminar(persona)
            if (exitoso == 1) {
                _estado.update {
                    it.copy(
                        personas = casoUso.listar(),
                        personaSeleccionada = null,
                        mostrarConfirmacionEliminar = false,
                        mensaje = MensajeUI.Exito("Persona eliminada")
                    )
                }
            } else {
                _estado.update {
                    it.copy(
                        mensaje = MensajeUI.Error("Error al eliminar"),
                        mostrarConfirmacionEliminar = false
                    )
                }
            }
        }
    }

    fun limpiarTodo(context: Context) {
        viewModelScope.launch {
            _estado.update {
                it.copy(
                    personas = emptyList(),
                    personaSeleccionada = null,
                    imagenFacial = null
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
                        mostrarConfirmacionLimpiarTodo = false
                    )
                }
            } else {
                _estado.update {
                    it.copy(
                        mensaje = MensajeUI.Error("Error al limpiar todo"),
                        mostrarConfirmacionLimpiarTodo = false
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

    fun seleccionarPersona(persona: Persona?) {
        _estado.update {
            it.copy(
                personaSeleccionada = persona,
                mostrarConfirmacionEliminar = persona != null
            )
        }
    }

    fun toggleConfirmacionLimpiar(show: Boolean) {
        _estado.update { it.copy(mostrarConfirmacionLimpiarTodo = show) }
    }

    fun iniciarEdicion(persona: Persona) {
        _estado.update {
            it.copy(
                nombre = persona.nombre,
                apellido = persona.apellido,
                dni = persona.dni,
                correo = persona.correo,
                imagenFacial = persona.imagenFacial,
                personaSeleccionada = persona,
                esEdicion = true
            )
        }
    }



}