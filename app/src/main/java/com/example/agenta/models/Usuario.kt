package com.example.agenta.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa un usuario en la base de datos Room.
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // ID único autogenerado
    val nombre: String,     // Nombre del usuario (usado para login)
    val contrasena: String  // Contraseña del usuario
)
