package com.example.listaimagenes.domain.usecase

import android.content.Context
import com.example.listaimagenes.data.AppDatabase
import com.example.listaimagenes.data.IPersonaRepository
import com.example.listaimagenes.data.RepositorioPersona

object PersonaManager {

    private lateinit var repositorio: IPersonaRepository
    lateinit var casoUso: CasoUsoPersona

    fun init(context: Context) {
        val dao = AppDatabase.getInstance(context).personaDao()
        repositorio = RepositorioPersona(dao)
        casoUso = CasoUsoPersona(repositorio)
    }
}