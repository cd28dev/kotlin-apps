package com.example.listaimagenes.reconocimiento.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.listaimagenes.reconocimiento.domain.model.Persona

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Persona::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personaDao(): PersonaDao
    
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE personas_nueva (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        dni TEXT NOT NULL,
                        nombre TEXT NOT NULL,
                        apellido TEXT NOT NULL,
                        correo TEXT NOT NULL,
                        imagenFacial BLOB,
                        embeddingFacial TEXT
                    )
                """)
                database.execSQL("""
                    INSERT INTO personas_nueva (id, dni, nombre, apellido, correo, embeddingFacial)
                    SELECT id, dni, nombre, apellido, correo, faceEmbedding FROM personas
                """)
                
                database.execSQL("DROP TABLE personas")
                database.execSQL("ALTER TABLE personas_nueva RENAME TO personas")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRACION_2_3)
                    .fallbackToDestructiveMigration(true) // Solo para desarrollo
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}