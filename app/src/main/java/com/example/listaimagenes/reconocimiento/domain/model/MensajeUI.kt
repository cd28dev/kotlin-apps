package com.example.listaimagenes.reconocimiento.domain.model

sealed class MensajeUI {
    object Ninguno : MensajeUI()
    data class Exito(val texto: String) : MensajeUI()
    data class Error(val texto: String) : MensajeUI()
}
