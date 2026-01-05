package com.example.listaimagenes.reconocimiento.domain.usecase

import android.content.ContentResolver
import android.graphics.BitmapFactory
import com.example.listaimagenes.reconocimiento.domain.model.Persona
import com.example.listaimagenes.reconocimiento.data.IPersonaRepository
import com.example.listaimagenes.reconocimiento.domain.utils.UtilidadesImagen
import java.io.File

sealed class Resultado {
    object Exito : Resultado()
    data class Error(val mensaje: String) : Resultado()
}

class CasoUsoPersona(
    private val repo: IPersonaRepository,
    private val context : ContentResolver,
    private val contextoApp: android.content.Context
) {
    private val reconocimientoFacial = ReconocimientoFacial(contextoApp)

    suspend fun crear(persona: Persona): Resultado {
        validar(persona)
        val personas = repo.listar()
        if (personas.any { it.dni.trim() == persona.dni.trim() }) {
            return Resultado.Error("Ya existe una persona con ese DNI")
        }

        // Generar embedding facial si hay imagen
        var embeddingFacial: String? = null
        var imagenFacialBytes: ByteArray? = null
        
        persona.imagenFacial?.let { bytesImagen ->
            try {
                val bitmap = UtilidadesImagen.byteArrayABitmap(bytesImagen)
                if (bitmap != null) {
                    imagenFacialBytes = bytesImagen // Ya está en ByteArray
                    when (val resultado = reconocimientoFacial.detectarYExtraerCaracteristicas(bitmap)) {
                        is ResultadoDeteccion.Exito -> {
                            embeddingFacial = resultado.embedding
                        }
                        is ResultadoDeteccion.SinRostro -> {
                            return Resultado.Error("No se detectó ningún rostro en la imagen")
                        }
                        is ResultadoDeteccion.MultiplesRostros -> {
                            return Resultado.Error("Se detectaron múltiples rostros. Use una imagen con un solo rostro")
                        }
                        is ResultadoDeteccion.Error -> {
                            return Resultado.Error("Error al procesar el rostro: ${resultado.mensaje}")
                        }
                    }
                }
            } catch (e: Exception) {
                return Resultado.Error("Error al procesar la imagen: ${e.message}")
            }
        }

        val nuevaPersona = Persona(
            dni = persona.dni.trim(),
            nombre = persona.nombre.trim(),
            apellido = persona.apellido.trim(),
            correo = persona.correo.trim(),
            imagenFacial = imagenFacialBytes,
            embeddingFacial = embeddingFacial
        )
        
        return if (repo.crear(nuevaPersona)) {
            Resultado.Exito
        } else {
            Resultado.Error("Error al agregar la persona")
        }
    }

    suspend fun actualizar(persona: Persona): Resultado {
        return if (repo.actualizar(persona) > 0) {
            Resultado.Exito
        } else {
            Resultado.Error("Error al actualizar")
        }
    }
    
    suspend fun listar(): List<Persona> {
        return repo.listar()
    }

    suspend fun eliminar(persona: Persona): Int {
        // Con ByteArray no necesitamos eliminar archivos físicos
        return repo.eliminar(persona)
    }

    suspend fun reconocerPersona(fotoPath: String): ResultadoReconocimiento {
        return try {
            val bitmap = BitmapFactory.decodeFile(fotoPath)
            if (bitmap == null) {
                return ResultadoReconocimiento.Error("No se pudo cargar la imagen")
            }

            // Detectar rostro y generar embedding
            when (val resultado = reconocimientoFacial.detectarYExtraerCaracteristicas(bitmap)) {
                is ResultadoDeteccion.Exito -> {
                    val embeddingBuscar = resultado.embedding
                    val personas = repo.listar()
                    
                    // Buscar persona más similar
                    when (val busqueda = reconocimientoFacial.buscarPersonaMasSimilar(embeddingBuscar, personas)) {
                        is ResultadoBusqueda.PersonaEncontrada -> {
                            ResultadoReconocimiento.PersonaEncontrada(busqueda.persona, busqueda.similitud)
                        }
                        is ResultadoBusqueda.PersonaNoEncontrada -> {
                            ResultadoReconocimiento.PersonaNoEncontrada(busqueda.razon)
                        }
                        is ResultadoBusqueda.Error -> {
                            ResultadoReconocimiento.Error(busqueda.mensaje)
                        }
                    }
                }
                is ResultadoDeteccion.SinRostro -> {
                    ResultadoReconocimiento.PersonaNoEncontrada("No se detectó ningún rostro en la imagen")
                }
                is ResultadoDeteccion.MultiplesRostros -> {
                    ResultadoReconocimiento.Error("Se detectaron múltiples rostros. Use una imagen con un solo rostro")
                }
                is ResultadoDeteccion.Error -> {
                    ResultadoReconocimiento.Error("Error al procesar imagen: ${resultado.mensaje}")
                }
            }
        } catch (e: Exception) {
            ResultadoReconocimiento.Error("Error en reconocimiento: ${e.message}")
        }
    }

    suspend fun limpiarTodas(): Boolean {
        return try {
            // Con ByteArray solo necesitamos limpiar la base de datos
            repo.limpiarTodas()
        } catch (e: Exception) {
            false
        }
    }

    private fun validar(persona: Persona): Resultado {
        when {
            persona.nombre.isBlank() -> return Resultado.Error("El nombre es requerido")
            persona.apellido.isBlank() -> return Resultado.Error("El apellido es requerido")
            persona.dni.isBlank() -> return Resultado.Error("El DNI es requerido")
            persona.correo.isBlank() -> return Resultado.Error("El correo es requerido")
            
            persona.dni.trim().length != 8 -> return Resultado.Error("El DNI debe tener exactamente 8 dígitos")
            !persona.dni.trim().all { it.isDigit() } -> return Resultado.Error("El DNI solo puede contener números")
            
            !persona.correo.trim().contains("@") -> return Resultado.Error("El correo debe tener un formato válido")
            
            persona.nombre.trim().length < 2 -> return Resultado.Error("El nombre debe tener al menos 2 caracteres")
            persona.apellido.trim().length < 2 -> return Resultado.Error("El apellido debe tener al menos 2 caracteres")
        }
        return Resultado.Exito
    }
}

/**
 * Resultado del reconocimiento de persona
 */
sealed class ResultadoReconocimiento {
    data class PersonaEncontrada(val persona: Persona, val similitud: Float) : ResultadoReconocimiento()
    data class PersonaNoEncontrada(val razon: String) : ResultadoReconocimiento()
    data class Error(val mensaje: String) : ResultadoReconocimiento()
}