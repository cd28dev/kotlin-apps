package com.example.listaimagenes.data

import com.example.listaimagenes.domain.model.Persona

interface IPersonaRepository {
    suspend fun crear(persona: Persona): Boolean
    suspend fun listar(): List<Persona>
    suspend fun obtenerPersonaPorDni(dni: String): Persona?
    suspend fun obtenerPersonaPorId(id: Int): Persona?
    suspend fun eliminar(persona: Persona) : Int

    suspend fun actualizar(persona : Persona): Int
    suspend fun limpiarTodas():Boolean
}

class RepositorioPersona(private val dao:PersonaDao) : IPersonaRepository {

    override suspend fun crear(persona: Persona): Boolean {
        return try {
            val resultado = dao.crear(persona)
            resultado != -1L
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun listar(): List<Persona> {
        return dao.listar().map {
            Persona(it.id, it.dni,it.nombre, it.apellido,it.correo, it.foto)
        }
    }

    override suspend fun obtenerPersonaPorDni(dni: String): Persona? {
        return try {
            val entidad = dao.obtenerPersonaPorDni(dni)
            entidad?.let {
                Persona(it.id, it.dni, it.nombre, it.apellido, it.correo, it.foto)
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun obtenerPersonaPorId(id: Int): Persona? {
        return try {
            val entidad = dao.obtenerPersonaPorId(id)
            entidad?.let {
                Persona(it.id, it.dni, it.nombre, it.apellido, it.correo, it.foto)
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun eliminar(persona: Persona) : Int {
        return try{
            dao.eliminar(persona)
            1
        } catch (e: Exception){
            0
        }

    }

    override suspend fun actualizar(persona: Persona): Int {
        return try {
            dao.actualizar(persona)
        } catch (e: Exception) {
            0
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
