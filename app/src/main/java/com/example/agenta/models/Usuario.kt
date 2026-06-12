package com.example.agenta.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Esta clase define la "Ficha de Usuario".
 * Es la estructura que tiene cada persona registrada en la base de datos.
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Un número único para cada persona
    val nombre: String,                              // El nombre que eligió el usuario
    val telefono: String = "",                       // Su número de contacto
    val contrasena: String                           // Su clave secreta
)
