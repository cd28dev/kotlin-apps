package com.example.listaimagenes.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personas")
data class Persona(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dni: String,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val foto: String? = null,
    val faceEmbedding: String? = null // Embedding facial como JSON string para comparación
)