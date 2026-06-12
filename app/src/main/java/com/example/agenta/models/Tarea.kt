package com.example.agenta.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa una tarea en la base de datos Room.
 */
@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // ID único autogenerado
    val materia: String,      // Nombre de la asignatura
    val titulo: String,       // Título de la tarea
    val fechaEntrega: String, // Fecha límite de entrega
    val descripcion: String,  // Detalles adicionales
    var estaHecha: Boolean = false // Estado de la tarea (Pendiente/Completada)
)
