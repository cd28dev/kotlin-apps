package com.example.listaimagenes.presentation.viewmodel

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listaimagenes.domain.model.EstadoPersona
import com.example.listaimagenes.domain.model.MensajeUI
import com.example.listaimagenes.domain.model.Persona
import com.example.listaimagenes.domain.usecase.PersonaManager
import com.example.listaimagenes.domain.usecase.Resultado
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.let

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
        android.util.Log.d("PersonaViewModel", "🔥 Estableciendo foto: $ruta")
        _estado.update { it.copy(foto = ruta) }
        android.util.Log.d("PersonaViewModel", "✅ Estado actualizado: foto=${_estado.value.foto}")
    }

    fun limpiarFoto() {
        _estado.value.foto?.let { ruta ->
            if (ruta.contains("temp_foto")) {
                try {
                    File(ruta).delete()
                } catch (e: Exception) {
                    Log.e("ViewModel", "Error al eliminar foto temporal", e)
                }
            }
        }
        _estado.update { it.copy(foto = null) }
        System.gc()
    }

    fun crear(context: Context, onExito: (Boolean) -> Unit) {
        val e = _estado.value
        
        // 🛡️ Evitar múltiples ejecuciones
        if (e.procesandoRegistro) {
            Log.d("PersonaViewModel", "⚠️ Ya se está procesando un registro, ignorando...")
            return
        }
        
        Log.d("PersonaViewModel", "🚀 Iniciando registro de persona...")
        _estado.update { it.copy(procesandoRegistro = true) }
        
        viewModelScope.launch {
            try {
                val fotoFinal = e.foto?.let { rutaTemp ->
                    guardarFotoEnGaleria(File(rutaTemp), context)
                }

                val persona = Persona(
                    nombre = e.nombre,
                    apellido = e.apellido,
                    dni = e.dni,
                    correo = e.correo,
                    foto = fotoFinal
                )

                when (val resultado = casoUso.crear(persona)) {
                    is Resultado.Exito -> {
                        e.foto?.let { rutaTemp ->
                            if (rutaTemp.contains("temp_foto")) {
                                try {
                                    File(rutaTemp).delete()
                                } catch (ex: Exception) {
                                    Log.e("ViewModel", "Error al eliminar archivo temporal", ex)
                                }
                            }
                        }

                        val personas = casoUso.listar()
                        _estado.update {
                            it.copy(
                                dni = "", nombre = "", apellido = "", correo = "", foto = null,
                                personas = personas,
                                procesandoRegistro = false,
                                mensaje = MensajeUI.Exito("Persona agregada correctamente")
                            )
                        }
                        Log.d("PersonaViewModel", "✅ Persona registrada exitosamente")
                        onExito(true)
                    }
                    is Resultado.Error -> {
                        e.foto?.let { rutaTemp ->
                            if (rutaTemp.contains("temp_foto")) {
                                try {
                                    File(rutaTemp).delete()
                                } catch (ex: Exception) {
                                    Log.e("ViewModel", "Error al eliminar archivo temporal", ex)
                                }
                            }
                        }
                        _estado.update { it.copy(procesandoRegistro = false, mensaje = MensajeUI.Error(resultado.mensaje)) }
                        Log.e("PersonaViewModel", "❌ Error al registrar persona: ${resultado.mensaje}")
                        onExito(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "💥 Exception en crear", e)
                _estado.update { it.copy(procesandoRegistro = false) }
                onExito(false)
            }
        }
    }

    fun actualizar(context: Context, onExito: (Boolean) -> Unit) {
        val e = _estado.value
        val personaOriginal = e.personaSeleccionada ?: return
        
        // 🛡️ Evitar múltiples ejecuciones
        if (e.procesandoRegistro) {
            Log.d("PersonaViewModel", "⚠️ Ya se está procesando una actualización, ignorando...")
            return
        }
        
        Log.d("PersonaViewModel", "🔄 Iniciando actualización de persona...")
        _estado.update { it.copy(procesandoRegistro = true) }

        viewModelScope.launch {
            try {
                var fotoFinal = personaOriginal.foto

                if (e.foto != null && e.foto != personaOriginal.foto) {
                    personaOriginal.foto?.let { uriAntigua ->
                        eliminarFotoDeGaleria(context, uriAntigua)
                    }
                    fotoFinal = guardarFotoEnGaleria(File(e.foto!!), context)

                    if (e.foto.contains("temp_foto")) {
                        try {
                            File(e.foto).delete()
                        } catch (ex: Exception) {
                            Log.e("ViewModel", "Error al eliminar archivo temporal", ex)
                        }
                    }
                }

                val persona = personaOriginal.copy(
                    nombre = e.nombre,
                    apellido = e.apellido,
                    dni = e.dni,
                    correo = e.correo,
                    foto = fotoFinal
                )

                when (val resultado = casoUso.actualizar(persona)) {
                    is Resultado.Exito -> {
                        val personas = casoUso.listar()
                        _estado.update {
                            it.copy(
                                nombre = "", apellido = "", dni = "", correo = "", foto = null,
                                personaSeleccionada = null,
                                esEdicion = false,
                                procesandoRegistro = false,
                                personas = personas,
                                mensaje = MensajeUI.Exito("Persona actualizada")
                            )
                        }
                        Log.d("PersonaViewModel", "✅ Persona actualizada exitosamente")
                        onExito(true)
                    }
                    is Resultado.Error -> {
                        _estado.update {
                            it.copy(procesandoRegistro = false, mensaje = MensajeUI.Error(resultado.mensaje))
                        }
                        Log.e("PersonaViewModel", "❌ Error al actualizar persona: ${resultado.mensaje}")
                        onExito(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "💥 Exception en actualizar", e)
                _estado.update { it.copy(procesandoRegistro = false) }
                onExito(false)
            }
        }
    }


    private fun guardarFotoEnGaleria(archivo: File, contexto: Context): String? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, archivo.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Camera")
        }

        val resolver = contexto.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                archivo.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return it.toString()
        }
        return null
    }


    fun cancelarEdicion() {
        _estado.value.foto?.let { ruta ->
            if (ruta.contains("temp_foto")) {
                try {
                    File(ruta).delete()
                } catch (e: Exception) {
                    Log.e("ViewModel", "Error al eliminar archivo temporal", e)
                }
            }
        }

        _estado.update {
            it.copy(
                nombre = "",
                apellido = "",
                dni = "",
                correo = "",
                foto = null,
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
                eliminarFotoDeGaleria(context, persona.foto)

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
            val personas = casoUso.listar()
            personas.forEach { persona ->
                eliminarFotoDeGaleria(context, persona.foto)
            }

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
                foto = persona.foto,
                personaSeleccionada = persona,
                esEdicion = true
            )
        }
    }

    private fun eliminarFotoDeGaleria(context: Context, uriString: String?) {
        if (uriString.isNullOrBlank()) return
        try {
            val uri = android.net.Uri.parse(uriString)
            val rows = context.contentResolver.delete(uri, null, null)
        } catch (e: Exception) {
            Log.e("ViewModel", "Error al eliminar foto de galería", e)
        }
    }

}