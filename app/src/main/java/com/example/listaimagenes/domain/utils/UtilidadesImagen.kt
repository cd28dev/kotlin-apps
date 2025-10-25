package com.example.listaimagenes.domain.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

/**
 * Utilidades para conversión entre Bitmap y ByteArray
 * Implementa los requisitos de la guía para manejo de imágenes faciales
 */
object UtilidadesImagen {
    
    /**
     * Convierte un Bitmap a ByteArray para almacenamiento en Room
     * @param bitmap La imagen a convertir
     * @param calidadCompresion Calidad JPEG (0-100), por defecto 90
     * @return ByteArray de la imagen comprimida o null si hay error
     */
    fun bitmapAByteArray(
        bitmap: Bitmap, 
        calidadCompresion: Int = 90
    ): ByteArray? {
        return try {
            val flujoSalida = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, calidadCompresion, flujoSalida)
            flujoSalida.toByteArray()
        } catch (e: Exception) {
            android.util.Log.e("UtilidadesImagen", "Error al convertir Bitmap a ByteArray", e)
            null
        }
    }
    
    /**
     * Convierte un ByteArray a Bitmap para visualización
     * @param bytesImagen Los bytes de la imagen almacenada
     * @return Bitmap de la imagen o null si hay error
     */
    fun byteArrayABitmap(bytesImagen: ByteArray): Bitmap? {
        return try {
            BitmapFactory.decodeByteArray(bytesImagen, 0, bytesImagen.size)
        } catch (e: Exception) {
            android.util.Log.e("UtilidadesImagen", "Error al convertir ByteArray a Bitmap", e)
            null
        }
    }
    
    /**
     * Redimensiona un Bitmap manteniendo proporción
     * @param bitmap Imagen original
     * @param anchoMaximo Ancho máximo en píxeles
     * @param altoMaximo Alto máximo en píxeles
     * @return Bitmap redimensionado
     */
    fun redimensionarBitmap(
        bitmap: Bitmap, 
        anchoMaximo: Int = 512, 
        altoMaximo: Int = 512
    ): Bitmap {
        val anchoOriginal = bitmap.width
        val altoOriginal = bitmap.height
        
        // Calcular factor de escala manteniendo proporción
        val factorEscala = minOf(
            anchoMaximo.toFloat() / anchoOriginal,
            altoMaximo.toFloat() / altoOriginal
        )
        
        val nuevoAncho = (anchoOriginal * factorEscala).toInt()
        val nuevoAlto = (altoOriginal * factorEscala).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, nuevoAncho, nuevoAlto, true)
    }
    
    /**
     * Convierte imagen desde ruta de archivo a ByteArray
     * @param rutaArchivo Ruta del archivo de imagen
     * @return ByteArray de la imagen o null si hay error
     */
    fun archivoAByteArray(rutaArchivo: String): ByteArray? {
        return try {
            val bitmap = BitmapFactory.decodeFile(rutaArchivo)
            bitmap?.let { 
                val bitmapRedimensionado = redimensionarBitmap(it)
                bitmapAByteArray(bitmapRedimensionado)
            }
        } catch (e: Exception) {
            android.util.Log.e("UtilidadesImagen", "Error al leer archivo de imagen", e)
            null
        }
    }
}