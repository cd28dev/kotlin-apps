package com.example.listaimagenes.domain.model

data class EstadoPersona(
    val nombre: String = "",
    val apellido: String = "",
    val dni: String = "",
    val correo: String = "",
    val imagenFacial: ByteArray? = null, // Cambio de foto String a imagenFacial ByteArray
    val personas: List<Persona> = emptyList(),
    val mensaje: MensajeUI = MensajeUI.Ninguno,
    val personaSeleccionada: Persona? = null,
    val mostrarCamara: Boolean = false,
    val mostrarConfirmacionEliminar: Boolean = false,
    val mostrarConfirmacionLimpiarTodo: Boolean = false,
    val esEdicion: Boolean = false,
    val procesandoRegistro: Boolean = false
) {
    // Override equals y hashCode para manejar ByteArray correctamente
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EstadoPersona

        if (nombre != other.nombre) return false
        if (apellido != other.apellido) return false
        if (dni != other.dni) return false
        if (correo != other.correo) return false
        if (imagenFacial != null) {
            if (other.imagenFacial == null) return false
            if (!imagenFacial.contentEquals(other.imagenFacial)) return false
        } else if (other.imagenFacial != null) return false
        if (personas != other.personas) return false
        if (mensaje != other.mensaje) return false
        if (personaSeleccionada != other.personaSeleccionada) return false
        if (mostrarCamara != other.mostrarCamara) return false
        if (mostrarConfirmacionEliminar != other.mostrarConfirmacionEliminar) return false
        if (mostrarConfirmacionLimpiarTodo != other.mostrarConfirmacionLimpiarTodo) return false
        if (esEdicion != other.esEdicion) return false
        if (procesandoRegistro != other.procesandoRegistro) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nombre.hashCode()
        result = 31 * result + apellido.hashCode()
        result = 31 * result + dni.hashCode()
        result = 31 * result + correo.hashCode()
        result = 31 * result + (imagenFacial?.contentHashCode() ?: 0)
        result = 31 * result + personas.hashCode()
        result = 31 * result + mensaje.hashCode()
        result = 31 * result + (personaSeleccionada?.hashCode() ?: 0)
        result = 31 * result + mostrarCamara.hashCode()
        result = 31 * result + mostrarConfirmacionEliminar.hashCode()
        result = 31 * result + mostrarConfirmacionLimpiarTodo.hashCode()
        result = 31 * result + esEdicion.hashCode()
        result = 31 * result + procesandoRegistro.hashCode()
        return result
    }
}