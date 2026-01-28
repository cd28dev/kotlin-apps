package com.example.listaimagenes.reconocimiento.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personas")
data class Persona(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dni: String,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val imagenFacial: ByteArray? = null,
    val embeddingFacial: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Persona

        if (id != other.id) return false
        if (dni != other.dni) return false
        if (nombre != other.nombre) return false
        if (apellido != other.apellido) return false
        if (correo != other.correo) return false
        if (imagenFacial != null) {
            if (other.imagenFacial == null) return false
            if (!imagenFacial.contentEquals(other.imagenFacial)) return false
        } else if (other.imagenFacial != null) return false
        if (embeddingFacial != other.embeddingFacial) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + dni.hashCode()
        result = 31 * result + nombre.hashCode()
        result = 31 * result + apellido.hashCode()
        result = 31 * result + correo.hashCode()
        result = 31 * result + (imagenFacial?.contentHashCode() ?: 0)
        result = 31 * result + (embeddingFacial?.hashCode() ?: 0)
        return result
    }
}