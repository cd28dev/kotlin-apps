package com.example.listaimagenes.domain.usecase

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import kotlinx.coroutines.tasks.await
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Clase para manejar el reconocimiento facial usando ML Kit
 */
class ReconocimientoFacial {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
    )

    /**
     * Detecta rostros en imagen y extrae características faciales únicas
     * Analiza puntos clave del rostro para crear "huella dactilar" facial
     */
    suspend fun detectarYExtraerCaracteristicas(imagenRostro: Bitmap): ResultadoDeteccion {
        return try {
            val imagenProcesar = InputImage.fromBitmap(imagenRostro, 0)
            val rostrosDetectados = detector.process(imagenProcesar).await()
            
            when {
                rostrosDetectados.isEmpty() -> ResultadoDeteccion.SinRostro
                rostrosDetectados.size > 1 -> ResultadoDeteccion.MultiplesRostros
                else -> {
                    val rostro = rostrosDetectados.first()
                    val caracteristicasFaciales = extraerCaracteristicasFaciales(rostro)
                    ResultadoDeteccion.Exito(rostro, caracteristicasFaciales)
                }
            }
        } catch (e: Exception) {
            ResultadoDeteccion.Error("Error al procesar imagen: ${e.message}")
        }
    }

    /**
     * Extrae las características únicas del rostro para crear su "huella digital"
     * Analiza proporciones, distancias y posiciones de puntos clave faciales
     * Normaliza valores para garantizar comparaciones precisas entre fotos
     */
    private fun extraerCaracteristicasFaciales(rostroDetectado: Face): String {
        val caracteristicas = mutableListOf<Float>()
        
        // Dimensiones del rostro normalizadas para comparación consistente
        val marcoRostro = rostroDetectado.boundingBox
        caracteristicas.add(marcoRostro.width().toFloat() / 1000f) // Ancho normalizado
        caracteristicas.add(marcoRostro.height().toFloat() / 1000f) // Alto normalizado
        
        // Proporción facial (ratio ancho/alto) - característica única de cada persona
        val proporcionFacial = marcoRostro.width().toFloat() / marcoRostro.height().toFloat()
        caracteristicas.add(proporcionFacial)
        
        // Posición del centro facial normalizada
        caracteristicas.add(marcoRostro.centerX().toFloat() / 1000f)
        caracteristicas.add(marcoRostro.centerY().toFloat() / 1000f)
        
        // Puntos clave faciales más confiables para identificación
        val puntosClaveConfiables = listOf(
            FaceLandmark.LEFT_EYE,
            FaceLandmark.RIGHT_EYE,
            FaceLandmark.NOSE_BASE
        )
        
        puntosClaveConfiables.forEach { tipoPunto ->
            rostroDetectado.getLandmark(tipoPunto)?.let { punto ->
                // Posición relativa del punto dentro del marco facial (0.0 a 1.0)
                val posicionX = (punto.position.x - marcoRostro.left) / marcoRostro.width().toFloat()
                val posicionY = (punto.position.y - marcoRostro.top) / marcoRostro.height().toFloat()
                caracteristicas.add(posicionX)
                caracteristicas.add(posicionY)
            } ?: run {
                // Valor por defecto si no se detecta el punto (centro del rostro)
                caracteristicas.add(0.5f)
                caracteristicas.add(0.5f)
            }
        }
        
        // Calcular distancias características entre puntos faciales clave
        val ojoIzquierdo = rostroDetectado.getLandmark(FaceLandmark.LEFT_EYE)
        val ojoDerecho = rostroDetectado.getLandmark(FaceLandmark.RIGHT_EYE)
        val nariz = rostroDetectado.getLandmark(FaceLandmark.NOSE_BASE)
        
        if (ojoIzquierdo != null && ojoDerecho != null) {
            // Distancia entre ojos - característica muy distintiva de cada persona
            val distanciaEntreOjos = kotlin.math.sqrt(
                (ojoIzquierdo.position.x - ojoDerecho.position.x).pow(2) +
                (ojoIzquierdo.position.y - ojoDerecho.position.y).pow(2)
            ) / marcoRostro.width().toFloat()
            caracteristicas.add(distanciaEntreOjos)
        } else {
            caracteristicas.add(0.3f) // Distancia promedio entre ojos
        }
        
        if (ojoIzquierdo != null && nariz != null) {
            // Distancia de ojo izquierdo a nariz - otra característica distintiva
            val distanciaOjoNariz = kotlin.math.sqrt(
                (ojoIzquierdo.position.x - nariz.position.x).pow(2) +
                (ojoIzquierdo.position.y - nariz.position.y).pow(2)
            ) / marcoRostro.width().toFloat()
            caracteristicas.add(distanciaOjoNariz)
        } else {
            caracteristicas.add(0.25f) // Distancia promedio ojo-nariz
        }
        
        // Convertir características a texto para almacenar en base de datos
        return caracteristicas.joinToString(",")
    }

    /**
     * Calcula la similitud entre dos conjuntos de características faciales
     * Retorna un valor entre 0-1, donde 1 significa rostros idénticos
     * Usa similitud coseno para mejor precisión en comparación facial
     */
    fun calcularSimilitud(caracteristicas1: String, caracteristicas2: String): Float {
        return try {
            val rasgos1 = caracteristicas1.split(",").map { it.toFloat() }
            val rasgos2 = caracteristicas2.split(",").map { it.toFloat() }
            
            if (rasgos1.size != rasgos2.size || rasgos1.isEmpty()) return 0f
            
            // Calcular similitud coseno para comparación facial precisa
            val productoEscalar = rasgos1.zip(rasgos2) { rasgo1, rasgo2 -> rasgo1 * rasgo2 }.sum()
            val magnitud1 = sqrt(rasgos1.map { it.pow(2) }.sum())
            val magnitud2 = sqrt(rasgos2.map { it.pow(2) }.sum())
            
            if (magnitud1 == 0f || magnitud2 == 0f) return 0f
            
            val similitudCoseno = productoEscalar / (magnitud1 * magnitud2)
            
            // Normalizar de rango [-1, 1] a [0, 1] para porcentaje de similitud
            val similitudNormalizada = (similitudCoseno + 1f) / 2f
            
            similitudNormalizada.coerceIn(0f, 1f)
        } catch (e: Exception) {
            0f
        }
    }

    /**
     * 🎯 AQUÍ PUEDES AJUSTAR LA PRECISIÓN DEL RECONOCIMIENTO:
     * 
     * umbralPrecision:
     * - 0.3f = MUY PERMISIVO (reconoce casi cualquier rostro)
     * - 0.5f = PERMISIVO (predeterminado anterior)
     * - 0.7f = BALANCEADO (recomendado) ⭐
     * - 0.8f = ESTRICTO (solo rostros muy similares)
     * - 0.9f = MUY ESTRICTO (casi imposible de reconocer)
     * 
     * Si no reconoce nada: bajar el valor (ej: 0.6f)
     * Si reconoce personas incorrectas: subir el valor (ej: 0.8f)
     */
    fun buscarPersonaMasSimilar(
        caracteristicasBuscadas: String,
        personasRegistradas: List<Pair<com.example.listaimagenes.domain.model.Persona, String>>,
        umbralPrecision: Float = 0.7f // 🔧 AJUSTA AQUÍ LA PRECISIÓN
    ): ResultadoBusqueda {
        if (personasRegistradas.isEmpty()) {
            return ResultadoBusqueda.NoEncontrado("No hay personas registradas")
        }
        
        var mejorCoincidencia: com.example.listaimagenes.domain.model.Persona? = null
        var mayorSimilitud = 0f
        
        personasRegistradas.forEach { (persona, caracteristicasAlmacenadas) ->
            val porcentajeSimilitud = calcularSimilitud(caracteristicasBuscadas, caracteristicasAlmacenadas)
            if (porcentajeSimilitud > mayorSimilitud) {
                mayorSimilitud = porcentajeSimilitud
                mejorCoincidencia = persona
            }
        }
        
        return if (mayorSimilitud >= umbralPrecision && mejorCoincidencia != null) {
            ResultadoBusqueda.Encontrado(mejorCoincidencia!!, mayorSimilitud)
        } else {
            ResultadoBusqueda.NoEncontrado("No se encontró una coincidencia suficiente (similitud: ${String.format("%.2f", mayorSimilitud)})")
        }
    }
}

/**
 * Resultado de la detección de rostro
 */
sealed class ResultadoDeteccion {
    data class Exito(val face: Face, val embedding: String) : ResultadoDeteccion()
    object SinRostro : ResultadoDeteccion()
    object MultiplesRostros : ResultadoDeteccion()
    data class Error(val mensaje: String) : ResultadoDeteccion()
}

/**
 * Resultado de la búsqueda de persona
 */
sealed class ResultadoBusqueda {
    data class Encontrado(val persona: com.example.listaimagenes.domain.model.Persona, val similitud: Float) : ResultadoBusqueda()
    data class NoEncontrado(val razon: String) : ResultadoBusqueda()
}