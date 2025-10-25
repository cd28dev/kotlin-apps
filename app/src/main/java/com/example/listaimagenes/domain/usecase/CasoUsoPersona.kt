package com.example.listaimagenes.domain.usecase

import android.content.ContentResolver
import android.graphics.BitmapFactory
import com.example.listaimagenes.domain.model.Persona
import com.example.listaimagenes.data.IPersonaRepository
import java.io.File

sealed class Resultado {
    object Exito : Resultado()
    data class Error(val mensaje: String) : Resultado()
}

class CasoUsoPersona(
    private val repo: IPersonaRepository,
    private val context : ContentResolver
) {
    private val reconocimientoFacial = ReconocimientoFacial()

    suspend fun crear(persona : Persona):Resultado{
        validar(persona);
        val personas = repo.listar()
        if (personas.any { it.dni.trim() == persona.dni.trim() }){
            return Resultado.Error("Ya existe una persona con ese DNI")
        }

        // Generar embedding facial si hay foto
        var faceEmbedding: String? = null
        persona.foto?.let { fotoPath ->
            try {
                val bitmap = BitmapFactory.decodeFile(fotoPath)
                if (bitmap != null) {
                    when (val resultado = reconocimientoFacial.detectarYExtraerCaracteristicas(bitmap)) {
                        is ResultadoDeteccion.Exito -> {
                            faceEmbedding = resultado.embedding
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
            foto = persona.foto?.trim(),
            correo = persona.correo.trim(),
            faceEmbedding = faceEmbedding
        )
        return if (repo.crear(nuevaPersona)) {
            Resultado.Exito
        } else {
            Resultado.Error("Error al agregar la persona")
        }
    }

    suspend fun actualizar(persona:Persona):Resultado{
        return if (repo.actualizar(persona) >0){
            Resultado.Exito
        }else{
            Resultado.Error("Error al actualizar")
        }
    }
    suspend fun listar():List<Persona> {
        val personas = repo.listar()
        return personas
    }

    suspend fun eliminar(persona: Persona): Int {
        val eliminado : Int = repo.eliminar(persona)

        if (eliminado==1 && persona?.foto != null) {
            try {
                val file = File(persona.foto)
                if (file.exists()) {
                    val fotoEliminada = file.delete()
                    println("Foto eliminada: $fotoEliminada - Ruta: ${persona.foto}")
                } else {
                    println("El archivo no existe: ${persona.foto}")
                }
            } catch (e: Exception) {
                println("Error al eliminar la foto: ${e.message}")
                e.printStackTrace()
            }
        }

        return eliminado
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
                    // Obtener todas las personas con embeddings
                    val personas = repo.listar()
                    val personasConEmbedding = personas.mapNotNull { persona ->
                        persona.faceEmbedding?.let { embedding ->
                            Pair(persona, embedding)
                        }
                    }

                    // Buscar coincidencia
                    when (val busqueda = reconocimientoFacial.buscarPersonaMasSimilar(
                        resultado.embedding, 
                        personasConEmbedding
                    )) {
                        is ResultadoBusqueda.Encontrado -> {
                            ResultadoReconocimiento.PersonaEncontrada(busqueda.persona, busqueda.similitud)
                        }
                        is ResultadoBusqueda.NoEncontrado -> {
                            ResultadoReconocimiento.PersonaNoEncontrada(busqueda.razon)
                        }
                    }
                }
                is ResultadoDeteccion.SinRostro -> {
                    ResultadoReconocimiento.Error("No se detectó ningún rostro en la imagen")
                }
                is ResultadoDeteccion.MultiplesRostros -> {
                    ResultadoReconocimiento.Error("Se detectaron múltiples rostros. Use una imagen con un solo rostro")
                }
                is ResultadoDeteccion.Error -> {
                    ResultadoReconocimiento.Error("Error al procesar el rostro: ${resultado.mensaje}")
                }
            }
        } catch (e: Exception) {
            ResultadoReconocimiento.Error("Error durante el reconocimiento: ${e.message}")
        }
    }

    suspend fun limpiarTodas(): Boolean {
        return try {
            val personas = repo.listar()

            repo.limpiarTodas()

            personas.forEach { persona ->
                persona.foto?.let { fotoPath ->
                    try {
                        val file = File(fotoPath)
                        if (file.exists()) {
                            val eliminada = file.delete()
                            println("Foto eliminada: $eliminada - Ruta: $fotoPath")
                        }
                    } catch (e: Exception) {
                        println("Error al eliminar la foto: ${e.message}")
                        e.printStackTrace()
                    }
                }
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun validar(persona : Persona) : Resultado{
        val dniLimpio = persona.dni.trim()

        if (persona.nombre.isBlank()) {
            return Resultado.Error("El nombre es obligatorio")
        }
        if(persona.correo.isBlank()){
            return Resultado.Error("El correo es obligatorio")
        }
        if (persona.apellido.isBlank()) {
            return Resultado.Error("El apellido es obligatorio")
        }
        if (dniLimpio.isBlank()) {
            return Resultado.Error("El DNI es obligatorio")
        }
        if (!dniLimpio.matches(Regex("\\d{8}"))){
            return Resultado.Error("El DNI debe tener 8 dígitos")
        }
        if (persona.foto.isNullOrBlank()) {
            return Resultado.Error("La foto es obligatoria")
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