package com.example.listaimagenes.reconocimiento.domain.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object UtilidadesImagen {

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

    fun byteArrayABitmap(bytesImagen: ByteArray): Bitmap? {
        return try {
            BitmapFactory.decodeByteArray(bytesImagen, 0, bytesImagen.size)
        } catch (e: Exception) {
            android.util.Log.e("UtilidadesImagen", "Error al convertir ByteArray a Bitmap", e)
            null
        }
    }

    fun redimensionarBitmap(
        bitmap: Bitmap, 
        anchoMaximo: Int = 512, 
        altoMaximo: Int = 512
    ): Bitmap {
        val anchoOriginal = bitmap.width
        val altoOriginal = bitmap.height
        
        val factorEscala = minOf(
            anchoMaximo.toFloat() / anchoOriginal,
            altoMaximo.toFloat() / altoOriginal
        )
        
        val nuevoAncho = (anchoOriginal * factorEscala).toInt()
        val nuevoAlto = (altoOriginal * factorEscala).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, nuevoAncho, nuevoAlto, true)
    }

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