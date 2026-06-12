package com.example.agenta.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa la tabla 'tareas' en la base de datos.
 */
@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // ID único de la tarea
    val usuarioId: Int,                              // Relación con el usuario propietario
    val materia: String,                              // Nombre de la materia/asignatura
    val titulo: String,                               // Título o nombre de la tarea
    val fechaEntrega: String,                        // Fecha límite de entrega
    val descripcion: String,                          // Detalles adicionales
    var estaHecha: Boolean = false                    // Estado de completitud
)
