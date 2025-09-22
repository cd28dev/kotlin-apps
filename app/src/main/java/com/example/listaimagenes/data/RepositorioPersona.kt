package com.example.listaimagenes.data

import com.example.listaimagenes.domain.model.Persona

interface IPersonaRepository {
    suspend fun agregarPersona(persona: Persona): Boolean
    suspend fun obtenerPersonas(): List<Persona>
    suspend fun eliminarPersona(dni: String)
    suspend fun limpiarTodas()
}

class RepositorioPersona(private val dao:PersonaDao) : IPersonaRepository {

    override suspend fun agregarPersona(persona: Persona): Boolean {
        return try {
            val resultado = dao.agregarPersona(persona)
            resultado != -1L
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun obtenerPersonas(): List<Persona> {
        return dao.obtenerPersonas().map {
            Persona(it.dni, it.nombre, it.apellido, it.foto)
        }
    }

    override suspend fun eliminarPersona(dni: String) {
        dao.eliminarPersona(dni)
    }

    override suspend fun limpiarTodas() {
        dao.limpiarTodas()
    }
}
