package com.example.listaimagenes.domain.usecase

import com.example.listaimagenes.domain.model.Persona
import com.example.listaimagenes.data.IPersonaRepository

sealed class ResultadoAgregarPersona {
    object Exito : ResultadoAgregarPersona()
    data class Error(val mensaje: String) : ResultadoAgregarPersona()
}

class CasoUsoPersona(private val repo: IPersonaRepository) {
    suspend fun agregarPersona(
        nombre: String,
        apellido: String,
        dni: String,
        foto: String?
    ): ResultadoAgregarPersona {

        val dniLimpio = dni.trim()

        if (nombre.isBlank()) {
            return ResultadoAgregarPersona.Error("El nombre es obligatorio")
        }
        if (apellido.isBlank()) {
            return ResultadoAgregarPersona.Error("El apellido es obligatorio")
        }
        if (dniLimpio.isBlank()) {
            return ResultadoAgregarPersona.Error("El DNI es obligatorio")
        }
        if (!dniLimpio.matches(Regex("\\d{8}"))){
            return ResultadoAgregarPersona.Error("El DNI debe tener 8 dígitos")
        }
        if (foto.isNullOrBlank()) {
            return ResultadoAgregarPersona.Error("La foto es obligatoria")
        }

        val personas = repo.obtenerPersonas()
        if (personas.any { it.dni.trim() == dniLimpio })
            return ResultadoAgregarPersona.Error("Ya existe una persona con ese DNI")

        val nuevaPersona = Persona(
            dni = dniLimpio.trim(),
            nombre = nombre.trim(),
            apellido = apellido.trim(),
            foto = foto.trim()
        )
        return if (repo.agregarPersona(nuevaPersona)) {
            ResultadoAgregarPersona.Exito
        } else {
            ResultadoAgregarPersona.Error("Error al agregar la persona")
        }
    }

    suspend fun obtenerPersonas():List<Persona> {
        val personas = repo.obtenerPersonas()

        println("PERSONAS OBTENIDAS DE LA BD:")
        personas.forEach { persona ->
            println("DNI: '${persona.dni}' | Nombre: '${persona.nombre}' | Apellido: '${persona.apellido}'")
        }

        return personas
    }
    suspend fun eliminarPersona(dni: String) = repo.eliminarPersona(dni)
    suspend fun limpiarTodas() = repo.limpiarTodas()
}
