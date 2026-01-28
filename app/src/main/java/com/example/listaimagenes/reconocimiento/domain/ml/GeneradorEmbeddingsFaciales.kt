package com.example.listaimagenes.reconocimiento.domain.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.sqrt

class GeneradorEmbeddingsFaciales(private val contexto: Context) {
    
    private var interprete: Interpreter? = null
    private val nombreModelo = "MobileFaceNet.tflite"
    private var inputHeight = 224
    private var inputWidth = 224
    private var dimensionesEmbedding = 128
    
    companion object {
        private const val TAG = "GeneradorEmbeddingsFaciales"
    }

    fun inicializarModelo(): Boolean {
        return try {
            val buffer = cargarArchivoModelo()
            if (buffer != null) {
                val opciones = Interpreter.Options().apply {
                    setNumThreads(4)
                    setUseXNNPACK(true)
                }
                val interpreter = Interpreter(buffer, opciones)
                    val inputTensor = interpreter.getInputTensor(0)
                    val inputShape = inputTensor.shape()
                    val inputType = inputTensor.dataType()
                    Log.d(TAG, "SHAPE DEL TENSOR DE ENTRADA: ${inputShape.joinToString()} (dims=${inputShape.size})")
                    Log.d(TAG, "TIPO DE DATO DEL TENSOR DE ENTRADA: $inputType")
                
                if (inputShape.size >= 4) {
                    inputHeight = inputShape[1]
                    inputWidth = inputShape[2]
                    Log.d(TAG, "Tamaño de entrada del modelo: ${inputHeight} x ${inputWidth}")
                }
                val outputShape = interpreter.getOutputTensor(0).shape() // [1, N]
                if (outputShape.size >= 2) {
                    dimensionesEmbedding = outputShape[1]
                    Log.d(TAG, "Dimensiones del embedding: $dimensionesEmbedding")
                }
                interprete = interpreter
                Log.d(TAG, "Modelo TensorFlow Lite cargado exitosamente")
                true
            } else {
                Log.w(TAG, "Modelo no encontrado, usando embeddings simulados")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar modelo TensorFlow Lite", e)
            false
        }
    }

    fun generarEmbedding(rostroDetectado: Bitmap): FloatArray? {
        return try {
            if (interprete != null) {
                generarEmbeddingReal(rostroDetectado)
            } else {
                generarEmbeddingSimulado(rostroDetectado)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al generar embedding", e)
            null
        }
    }
    

    private fun generarEmbeddingReal(rostro: Bitmap): FloatArray? {
        val interprete = this.interprete ?: return null
        try {
            val rostroRedimensionado = Bitmap.createScaledBitmap(
                rostro, inputWidth, inputHeight, true
            )
            val batchSize = 2
            val buffer = ByteBuffer.allocateDirect(4 * batchSize * inputWidth * inputHeight * 3)
            buffer.order(ByteOrder.nativeOrder())
            val pixels = IntArray(inputWidth * inputHeight)
            rostroRedimensionado.getPixels(pixels, 0, inputWidth, 0, 0, inputWidth, inputHeight)
            for (b in 0 until batchSize) {
                var pixelIndex = 0
                for (pixel in pixels) {
                    val r = ((pixel shr 16) and 0xFF) / 255.0f
                    val g = ((pixel shr 8) and 0xFF) / 255.0f
                    val bVal = (pixel and 0xFF) / 255.0f
                    buffer.putFloat(r)
                    buffer.putFloat(g)
                    buffer.putFloat(bVal)
                    if (b == 0 && (pixelIndex < 5 || pixelIndex > pixels.size - 5)) {
                        Log.d(TAG, "Pixel $pixelIndex: R=$r G=$g B=$bVal (raw=0x${pixel.toUInt().toString(16)})")
                    }
                    pixelIndex++
                }
            }
            buffer.rewind()
            Log.d(TAG, "Buffer de entrada preparado. Tamaño: ${buffer.capacity()} bytes. Esperado: ${4 * batchSize * inputWidth * inputHeight * 3} bytes")
            Log.d(TAG, "🟢 Ejecutando inferencia con tamaño: ${inputHeight} x ${inputWidth}, embedding: $dimensionesEmbedding, batchSize: $batchSize")
            val embedding = Array(batchSize) { FloatArray(dimensionesEmbedding) }
            interprete.run(buffer, embedding)
            val vectorEmbedding = embedding[0]
            return normalizarVectorL2(vectorEmbedding)
        } catch (e: Exception) {
            Log.e(TAG, "Error en inferencia TensorFlow Lite", e)
            return null
        }
    }

    private fun generarEmbeddingSimulado(rostro: Bitmap): FloatArray {
        Log.d(TAG, "🔧 Generando embedding simulado (reemplazar con modelo real)")
        
        val embedding = FloatArray(dimensionesEmbedding)
        val pixels = IntArray(rostro.width * rostro.height)
        rostro.getPixels(pixels, 0, rostro.width, 0, 0, rostro.width, rostro.height)
        
        var suma = 0L
        pixels.forEach { suma += it }
        val semilla = suma.hashCode()
        
        val random = kotlin.random.Random(semilla)
        for (i in embedding.indices) {
            embedding[i] = random.nextFloat() * 2 - 1
        }
        
        return normalizarVectorL2(embedding)
    }
    

    private fun prepararBufferEntrada(bitmap: Bitmap): ByteBuffer {
        val ancho = bitmap.width
        val alto = bitmap.height
        val buffer = ByteBuffer.allocateDirect(4 * ancho * alto * 3)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(ancho * alto)
        bitmap.getPixels(pixels, 0, ancho, 0, 0, ancho, alto)

        var pixelIndex = 0
        for (pixel in pixels) {
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            buffer.putFloat(r)
            buffer.putFloat(g)
            buffer.putFloat(b)
            if (pixelIndex < 5 || pixelIndex > pixels.size - 5) {
                Log.d(TAG, "Pixel $pixelIndex: R=$r G=$g B=$b (raw=0x${pixel.toUInt().toString(16)})")
            }
            pixelIndex++
        }

        Log.d(TAG, "Buffer de entrada preparado. Tamaño: ${buffer.capacity()} bytes. Esperado: ${4 * ancho * alto * 3} bytes")
        return buffer
    }

    private fun normalizarVectorL2(vector: FloatArray): FloatArray {
        val norma = sqrt(vector.map { it * it }.sum())
        return if (norma > 0) {
            vector.map { it / norma }.toFloatArray()
        } else {
            vector
        }
    }

    fun calcularSimilitudCoseno(embedding1: FloatArray, embedding2: FloatArray): Float {
        if (embedding1.size != embedding2.size) return 0f
        
        var productoEscalar = 0f
        var norma1 = 0f
        var norma2 = 0f
        
        for (i in embedding1.indices) {
            productoEscalar += embedding1[i] * embedding2[i]
            norma1 += embedding1[i] * embedding1[i]
            norma2 += embedding2[i] * embedding2[i]
        }
        
        val denominador = sqrt(norma1) * sqrt(norma2)
        return if (denominador > 0) productoEscalar / denominador else 0f
    }

    private fun cargarArchivoModelo(): MappedByteBuffer? {
        return try {
            val archivoModelo = contexto.assets.openFd(nombreModelo)
            val inputStream = FileInputStream(archivoModelo.fileDescriptor)
            val channel = inputStream.channel
            val startOffset = archivoModelo.startOffset
            val declaredLength = archivoModelo.declaredLength
            channel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo cargar modelo $nombreModelo desde assets", e)
            null
        }
    }

    fun liberarRecursos() {
        interprete?.close()
        interprete = null
        Log.d(TAG, "Recursos del modelo liberados")
    }

    fun embeddingAString(embedding: FloatArray): String {
        return embedding.joinToString(",")
    }

    fun stringAEmbedding(embeddingString: String): FloatArray? {
        return try {
            embeddingString.split(",").map { it.toFloat() }.toFloatArray()
        } catch (e: Exception) {
            Log.e(TAG, "Error al convertir string a embedding", e)
            null
        }
    }
}