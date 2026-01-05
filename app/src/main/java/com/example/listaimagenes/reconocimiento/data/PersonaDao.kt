package com.example.listaimagenes.reconocimiento.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.listaimagenes.reconocimiento.domain.model.Persona

@Dao
interface PersonaDao {
    @Query("SELECT * FROM personas")
    suspend fun listar(): List<Persona>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun crear(persona: Persona): Long

    @Update
    suspend fun actualizar(persona: Persona): Int

    @Delete
    suspend fun eliminar(persona: Persona): Int

    @Query("DELETE FROM personas")
    suspend fun limpiarTodas()

    @Query("SELECT * FROM personas WHERE dni = :dni LIMIT 1")
    suspend fun obtenerPersonaPorDni(dni: String): Persona

    @Query("SELECT*FROM personas WHERE id=:id LIMIT 1")
    suspend fun obtenerPersonaPorId(id:Int):Persona

}