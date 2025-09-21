package com.example.listaimagenes.presentation.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ManejadorArchivos {
    private const val NOMBRE_CARPETA = "FacultadesUNP"
    
    fun crearCarpetaFotos(context: Context): File {
        val carpetaFotos = File(context.getExternalFilesDir(null), NOMBRE_CARPETA)
        if (!carpetaFotos.exists()) {
            carpetaFotos.mkdirs()
        }
        return carpetaFotos
    }
    
    fun crearArchivoFoto(context: Context): File {
        val carpeta = crearCarpetaFotos(context)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(carpeta, "FACULTAD_$timeStamp.jpg")
    }
    
    fun eliminarFoto(rutaArchivo: String) {
        try {
            val archivo = File(rutaArchivo)
            if (archivo.exists()) {
                archivo.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}