package com.example.listaimagenes.data

import com.example.listaimagenes.domain.model.Persona

interface IPersonaRepository {
    suspend fun agregarPersona(persona: Persona): Boolean
    suspend fun obtenerPersonas(): List<Persona>
    suspend fun obtenerPersonaPorDni(dni: String): Persona?
    suspend fun eliminarPersona(dni: String) : Boolean
    suspend fun limpiarTodas():Boolean
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

    override suspend fun obtenerPersonaPorDni(dni: String): Persona? {
        return try {
            val entidad = dao.obtenerPersonaPorDni(dni)
            entidad?.let {
                Persona(it.dni, it.nombre, it.apellido, it.foto)
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun eliminarPersona(dni: String) : Boolean {
        return try{
            dao.eliminarPersona(dni)
            true
        } catch (e: Exception){
            false
        }

    }

    override suspend fun limpiarTodas(): Boolean {
        return try {
            dao.limpiarTodas()
            true
        } catch (e: Exception) {
            false
        }
    }
}
