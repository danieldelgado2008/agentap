package com.example.agenta.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Esta clase define la "Ficha de Tarea".
 * Es la información que guardamos de cada deber que el usuario anota.
 */
@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Un número único para cada tarea
    val usuarioId: Int,                              // A quién le pertenece esta tarea
    val materia: String,                              // La clase o materia (Ej: Matemáticas)
    val titulo: String,                               // El nombre de la tarea
    val fechaEntrega: String,                        // Cuándo hay que entregarla
    val descripcion: String,                          // Notas extras sobre la tarea
    var estaHecha: Boolean = false                    // ¿Ya la terminó? (Sí o No)
)
