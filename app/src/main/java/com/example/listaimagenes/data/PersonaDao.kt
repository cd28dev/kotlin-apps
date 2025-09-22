package com.example.listaimagenes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.listaimagenes.domain.model.Persona

@Dao
interface PersonaDao {
    @Query("SELECT * FROM personas")
    suspend fun obtenerPersonas(): List<Persona>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun agregarPersona(persona: Persona): Long

    @Query("DELETE FROM personas WHERE dni = :dni")
    suspend fun eliminarPersona(dni: String)

    @Query("DELETE FROM personas")
    suspend fun limpiarTodas()
}