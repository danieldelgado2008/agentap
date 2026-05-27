package com.example.agenta.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TareaViewModel : ViewModel() {

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
        if (index != -1) {
            Repository.listaMemoria[index] = Repository.listaMemoria[index].copy(estaHecha = true)
        }

        _listaTareas.value = mutableListOf<Tarea>().apply { addAll(Repository.listaMemoria) }
    }
}