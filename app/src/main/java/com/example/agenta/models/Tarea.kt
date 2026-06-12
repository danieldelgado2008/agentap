package com.example.agenta.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val materia: String,
    val titulo: String,
    val fechaEntrega: String,
    val descripcion: String,
    var estaHecha: Boolean = false
)
