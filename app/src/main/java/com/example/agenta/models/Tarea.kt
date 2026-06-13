package com.example.agenta.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Esta clase define la "Ficha de Tarea".
 * Es la información que guardamos de cada deber que el usuario anota.
 */
@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int = 0,
    val materia: String = "",
    val titulo: String = "",
    val fechaEntrega: String = "",
    val descripcion: String = "",
    var estaHecha: Boolean = false
)
