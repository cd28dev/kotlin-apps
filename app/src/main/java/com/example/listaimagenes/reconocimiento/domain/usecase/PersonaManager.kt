package com.example.listaimagenes.reconocimiento.domain.usecase

import android.content.Context
import com.example.listaimagenes.reconocimiento.data.AppDatabase
import com.example.listaimagenes.reconocimiento.data.IPersonaRepository
import com.example.listaimagenes.reconocimiento.data.RepositorioPersona

object PersonaManager {

    private lateinit var repositorio: IPersonaRepository
    lateinit var casoUso: CasoUsoPersona

    fun init(context: Context) {
        val dao = AppDatabase.getInstance(context).personaDao()
        repositorio = RepositorioPersona(dao)
        casoUso = CasoUsoPersona(
            repositorio,
            context.contentResolver,
            context
        )
    }
}