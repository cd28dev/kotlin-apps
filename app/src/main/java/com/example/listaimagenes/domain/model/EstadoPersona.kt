package com.example.listaimagenes.domain.model

data class EstadoPersona(
    val nombre: String = "",
    val apellido: String = "",
    val dni: String = "",
    val correo: String = "",
    val foto: String? = null,
    val personas: List<Persona> = emptyList(),
    val mensaje: MensajeUI = MensajeUI.Ninguno,
    val personaSeleccionada: Persona? = null,
    val mostrarCamara: Boolean = false,
    val mostrarConfirmacionEliminar: Boolean = false,
    val mostrarConfirmacionLimpiar: Boolean = false
)