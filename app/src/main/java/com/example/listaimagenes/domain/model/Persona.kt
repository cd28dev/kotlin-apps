package com.example.listaimagenes.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personas")
data class Persona(
    @PrimaryKey val dni: String,
    val nombre: String,
    val apellido: String,
    val foto: String? = null
)