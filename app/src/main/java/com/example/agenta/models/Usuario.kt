package com.example.agenta.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Esta clase define la "Ficha de Usuario".
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uid: String = "",                            // ID único de Firebase
    val nombre: String,
    val email: String = "",                          // Correo para entrar desde cualquier dispositivo
    val telefono: String = "",
    val contrasena: String
)
