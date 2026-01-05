package com.example.listaimagenes.reconocimiento.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listaimagenes.reconocimiento.domain.model.Persona
import com.example.listaimagenes.reconocimiento.domain.usecase.PersonaManager
import com.example.listaimagenes.reconocimiento.domain.usecase.ResultadoReconocimiento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel para manejar el reconocimiento facial
 */
class ReconocimientoViewModel : ViewModel() {
    
    private val casoUso = PersonaManager.casoUso
    private val _estado = MutableStateFlow(EstadoReconocimiento())
    val estado: StateFlow<EstadoReconocimiento> = _estado

    fun mostrarCamara() {
        _estado.update { it.copy(mostrarCamara = true) }
    }

    fun ocultarCamara() {
        _estado.update { it.copy(mostrarCamara = false) }
    }

    fun establecerFotoTomada(fotoPath: String) {
        _estado.update { 
            it.copy(
                fotoTomada = fotoPath,
                mostrarCamara = false,
                procesandoReconocimiento = true,
                personaReconocida = null,
                similitudReconocimiento = null,
                mensajeError = null
            )
        }
        procesarReconocimiento(fotoPath)
    }

    private fun procesarReconocimiento(fotoPath: String) {
        viewModelScope.launch {
            try {
                val resultado = withContext(Dispatchers.IO) {
                    casoUso.reconocerPersona(fotoPath)
                }
                
                _estado.update { estadoActual ->
                    when (resultado) {
                        is ResultadoReconocimiento.PersonaEncontrada -> {
                            estadoActual.copy(
                                procesandoReconocimiento = false,
                                personaReconocida = resultado.persona,
                                similitudReconocimiento = resultado.similitud,
                                mensajeError = null
                            )
                        }
                        is ResultadoReconocimiento.PersonaNoEncontrada -> {
                            estadoActual.copy(
                                procesandoReconocimiento = false,
                                personaReconocida = null,
                                similitudReconocimiento = null,
                                mensajeError = resultado.razon
                            )
                        }
                        is ResultadoReconocimiento.Error -> {
                            estadoActual.copy(
                                procesandoReconocimiento = false,
                                personaReconocida = null,
                                similitudReconocimiento = null,
                                mensajeError = resultado.mensaje
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _estado.update { 
                    it.copy(
                        procesandoReconocimiento = false,
                        mensajeError = "Error inesperado durante el reconocimiento: ${e.message}"
                    )
                }
            }
        }
    }

    fun reiniciarReconocimiento() {
        _estado.update { 
            EstadoReconocimiento() 
        }
    }
}

/**
 * Estado del reconocimiento facial
 */
data class EstadoReconocimiento(
    val mostrarCamara: Boolean = false,
    val fotoTomada: String? = null,
    val procesandoReconocimiento: Boolean = false,
    val personaReconocida: Persona? = null,
    val similitudReconocimiento: Float? = null,
    val mensajeError: String? = null
)