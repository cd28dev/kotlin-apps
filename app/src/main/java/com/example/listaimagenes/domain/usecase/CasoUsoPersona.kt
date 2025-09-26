package com.example.listaimagenes.domain.usecase

import android.content.ContentResolver
import androidx.compose.ui.graphics.RectangleShape
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

    suspend fun crear(persona : Persona):Resultado{
        validar(persona);
        val personas = repo.listar()
        if (personas.any { it.dni.trim() == persona.dni.trim() }){
            return Resultado.Error("Ya existe una persona con ese DNI")
        }

        val nuevaPersona = Persona(
            dni = persona.dni.trim(),
            nombre = persona.nombre.trim(),
            apellido = persona.apellido.trim(),
            foto = persona.foto?.trim(),
            correo = persona.correo.trim()
        )
        return if (repo.crear(nuevaPersona)) {
            Resultado.Exito
        } else {
            Resultado.Error("Error al agregar la persona")
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
                // Cambio principal: usar File.delete() en lugar de ContentResolver.delete()
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