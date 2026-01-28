package com.example.listaimagenes.reconocimiento.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.example.listaimagenes.reconocimiento.domain.ml.GeneradorEmbeddingsFaciales
import com.example.listaimagenes.reconocimiento.domain.model.Persona
import kotlinx.coroutines.tasks.await

class ReconocimientoFacial(private val contexto: Context) {

    private val detectorRostros = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE) // No necesitamos landmarks
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()
    )
    
    private val generadorEmbeddings = GeneradorEmbeddingsFaciales(contexto)
    
    init {
        generadorEmbeddings.inicializarModelo()
    }

    suspend fun detectarYExtraerCaracteristicas(imagenRostro: Bitmap): ResultadoDeteccion {
        return try {
            val imagenProcesar = InputImage.fromBitmap(imagenRostro, 0)
            val rostrosDetectados = detectorRostros.process(imagenProcesar).await()
            
            when {
                rostrosDetectados.isEmpty() -> ResultadoDeteccion.SinRostro
                rostrosDetectados.size > 1 -> ResultadoDeteccion.MultiplesRostros
                else -> {
                    val rostro = rostrosDetectados.first()
                    val rostroRecortado = recortarRostroDeImagen(imagenRostro, rostro.boundingBox)
                    val embedding = generadorEmbeddings.generarEmbedding(rostroRecortado)
                    
                    if (embedding != null) {
                        val embeddingString = generadorEmbeddings.embeddingAString(embedding)
                        ResultadoDeteccion.Exito(rostro, embeddingString)
                    } else {
                        ResultadoDeteccion.Error("No se pudo generar embedding facial")
                    }
                }
            }
        } catch (e: Exception) {
            ResultadoDeteccion.Error("Error al procesar imagen: ${e.message}")
        }
    }

    private fun recortarRostroDeImagen(imagenCompleta: Bitmap, marcaRostro: Rect): Bitmap {
        val expansion = 0.2f
        val anchoExpansion = (marcaRostro.width() * expansion).toInt()
        val altoExpansion = (marcaRostro.height() * expansion).toInt()
        
        val izquierda = maxOf(0, marcaRostro.left - anchoExpansion)
        val arriba = maxOf(0, marcaRostro.top - altoExpansion)
        val derecha = minOf(imagenCompleta.width, marcaRostro.right + anchoExpansion)
        val abajo = minOf(imagenCompleta.height, marcaRostro.bottom + altoExpansion)
        
        val ancho = derecha - izquierda
        val alto = abajo - arriba
        
        return Bitmap.createBitmap(imagenCompleta, izquierda, arriba, ancho, alto)
    }

    fun calcularSimilitud(embedding1: String, embedding2: String): Float {
        return try {
            val vector1 = generadorEmbeddings.stringAEmbedding(embedding1)
            val vector2 = generadorEmbeddings.stringAEmbedding(embedding2)
            
            if (vector1 != null && vector2 != null) {
                generadorEmbeddings.calcularSimilitudCoseno(vector1, vector2)
            } else {
                0f
            }
        } catch (e: Exception) {
            android.util.Log.e("ReconocimientoFacial", "Error al calcular similitud", e)
            0f
        }
    }

    fun buscarPersonaMasSimilar(
        embeddingBuscar: String,
        personasRegistradas: List<Persona>,
        umbralPrecision: Float = 0.75f
    ): ResultadoBusqueda {
        if (personasRegistradas.isEmpty()) {
            return ResultadoBusqueda.PersonaNoEncontrada("No hay personas registradas")
        }

        var mejorCoincidencia: Persona? = null
        var mejorSimilitud = 0f

        personasRegistradas.forEach { persona ->
            persona.embeddingFacial?.let { embeddingPersona ->
                val similitud = calcularSimilitud(embeddingBuscar, embeddingPersona)
                if (similitud > mejorSimilitud) {
                    mejorSimilitud = similitud
                    mejorCoincidencia = persona
                }
            }
        }

        return if (mejorCoincidencia != null && mejorSimilitud >= umbralPrecision) {
            ResultadoBusqueda.PersonaEncontrada(mejorCoincidencia, mejorSimilitud)
        } else {
            val razon = if (mejorSimilitud > 0) {
                "Similitud insuficiente: ${String.format("%.2f", mejorSimilitud)} < $umbralPrecision"
            } else {
                "No se encontraron rostros similares"
            }
            ResultadoBusqueda.PersonaNoEncontrada(razon)
        }
    }

    fun liberarRecursos() {
        generadorEmbeddings.liberarRecursos()
    }
}

sealed class ResultadoDeteccion {
    data class Exito(val face: Face, val embedding: String) : ResultadoDeteccion()
    object SinRostro : ResultadoDeteccion()
    object MultiplesRostros : ResultadoDeteccion()
    data class Error(val mensaje: String) : ResultadoDeteccion()
}

sealed class ResultadoBusqueda {
    data class PersonaEncontrada(val persona: Persona, val similitud: Float) : ResultadoBusqueda()
    data class PersonaNoEncontrada(val razon: String) : ResultadoBusqueda()
    data class Error(val mensaje: String) : ResultadoBusqueda()
}