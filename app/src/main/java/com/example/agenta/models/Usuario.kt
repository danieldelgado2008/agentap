package com.example.agenta.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa la tabla 'usuarios' en la base de datos.
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // ID único autoincremental
    val nombre: String,                              // Nombre de usuario
    val telefono: String = "",                       // Número de teléfono
    val contrasena: String                           // Contraseña de acceso
)
