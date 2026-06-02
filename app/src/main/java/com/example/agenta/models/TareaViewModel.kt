package com.example.agenta.models

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TareaViewModel(application: Application) : AndroidViewModel(application) {

    private val _listaTareas = MutableLiveData<MutableList<Tarea>>()
    val listaTareas: LiveData<MutableList<Tarea>> get() = _listaTareas

    var tareaSeleccionada: Tarea? = null

    init {
       _listaTareas.value = mutableListOf<Tarea>().apply { addAll(Repository.listaMemoria) }
    }

    fun agregarTarea(nuevaTarea: Tarea) {
        val nuevoId = if (Repository.listaMemoria.isEmpty()) 1 else Repository.listaMemoria.maxOf { it.id } + 1
        val tareaConId = nuevaTarea.copy(id = nuevoId)
        Repository.listaMemoria.add(tareaConId)
        _listaTareas.value = mutableListOf<Tarea>().apply { addAll(Repository.listaMemoria) }
    }

    fun marcarComoTerminada(tarea: Tarea) {
        val index = Repository.listaMemoria.indexOfFirst { it.id == tarea.id }
        if (index != -1 && !Repository.listaMemoria[index].estaHecha) {
            Repository.listaMemoria[index] = Repository.listaMemoria[index].copy(estaHecha = true)
            
            // Otorgar puntos
            val prefs = getApplication<Application>().getSharedPreferences("UserStats", Context.MODE_PRIVATE)
            val currentPoints = prefs.getInt("userPoints", 0)
            prefs.edit().putInt("userPoints", currentPoints + 5).apply()
        }

        _listaTareas.value = mutableListOf<Tarea>().apply { addAll(Repository.listaMemoria) }
    }
}
