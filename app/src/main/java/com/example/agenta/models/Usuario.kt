package com.example.agenta.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Esta clase define la "Ficha de Usuario".
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val contrasena: String = ""
)
