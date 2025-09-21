package com.example.listaimagenes.data.model

data class Facultad(
    val nombre: String,
    val descripcion: String,
    val año: Int,
    val imagen: Int, // Imagen por defecto de drawable
    val fotoPersonalizada: String? = null //  Ruta de foto tomada con cámara
) {
    // Método para obtener la imagen a mostrar (prioriza foto personalizada)
    fun obtenerImagenParaMostrar(): Any {
        return fotoPersonalizada ?: imagen
    }
}